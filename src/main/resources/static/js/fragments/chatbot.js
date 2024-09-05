/**
 * @author JSW
 *
 * 사용자가 입력한 메시지를 서버에 전송하고, 서버로부터 실시간으로 응답을 받아 화면에 표시하는 기능을 수행합니다. (SSE 연결)
 * 메시지 전송 시 HTML 특수 문자를 이스케이프 처리하고, 서버 응답을 마크다운 형식으로 렌더링하는 기능도 합니다.
 */

class ChatbotComponent {
  // 생성자 함수: backendUrl을 받아서 멤버 변수로 저장하고, init 메서드를 호출하여 초기화 작업을 수행
  constructor(backendUrl) {
    this.backendUrl = backendUrl;
    this.currentEventSource = null; // 현재 SSE 연결을 관리하는 변수
    this.isResponding = false; // AI가 응답 중인지 여부를 추적
    this.init(); // 초기화 함수 호출
  }

  // 초기화 함수: DOM 요소를 찾고, 이벤트 리스너를 설정하는 역할
  init() {
    this.hamburgerMenu = document.getElementById('hamburger-menu'); // 햄버거 메뉴 버튼 요소
    this.chatSidebar = document.getElementById('chat-sidebar'); // 채팅 사이드바 요소
    this.messages = document.getElementById('messages'); // 메시지 출력 요소
    this.userInput = document.getElementById('user-input'); // 사용자 입력란
    this.sendButton = document.getElementById('send-button'); // 전송 버튼

    // 햄버거 메뉴를 클릭할 때 챗봇을 토글
    this.hamburgerMenu.addEventListener('click', () => this.toggleChatbot());

    // Enter 키를 누르면서 AI가 응답 중이 아닐 때 메시지 전송
    this.userInput.addEventListener('keypress', (event) => {
      if (event.key === 'Enter' && !this.isResponding) this.sendMessage();
    });

    // 전송 버튼 클릭 시 메시지 전송 (AI가 응답 중이 아닐 때만)
    this.sendButton.addEventListener('click', () => {
      if (!this.isResponding) this.sendMessage();
    });

    // 화면의 다른 부분을 클릭하면 채팅창을 닫음
    document.addEventListener('click', (event) => {
      if (!this.chatSidebar.contains(event.target) && !this.hamburgerMenu.contains(event.target)) {
        document.body.classList.remove('chatbot-open');
      }
    });

    // 이전 대화 내용을 세션 스토리지에서 불러옴
    this.loadConversation();
    this.scrollToBottom(); // 대화 불러온 후 스크롤을 맨 아래로 이동

    // 창 크기 변경 시 스크롤을 맨 아래로 조정
    window.addEventListener('resize', () => this.scrollToBottom());
  }

  // 채팅창을 열거나 닫는 기능
  toggleChatbot() {
    document.body.classList.toggle('chatbot-open');
    // 챗봇이 열렸을 때 스크롤을 맨 아래로 이동
    if (document.body.classList.contains('chatbot-open')) {
      setTimeout(() => this.scrollToBottom(), 300); // 애니메이션 완료 후 스크롤
    }
  }

  // 스크롤을 메시지 영역의 맨 아래로 이동하는 함수
  scrollToBottom() {
    this.messages.scrollTop = this.messages.scrollHeight;
  }

  // HTML 특수문자를 이스케이프 처리하여 XSS 방지
  escapeHtml(unsafe) {
    return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
  }

  // 현재 대화를 세션 스토리지에 저장
  saveConversation() {
    sessionStorage.setItem('chatConversation', this.messages.innerHTML);
  }

  // 세션 스토리지에서 대화를 불러와 화면에 렌더링
  loadConversation() {
    const savedConversation = sessionStorage.getItem('chatConversation');
    if (savedConversation) {
      this.messages.innerHTML = savedConversation;
      // 코드 블록에 대한 하이라이팅 적용
      this.messages.querySelectorAll('pre code').forEach((block) => {
        hljs.highlightBlock(block);
      });
      this.scrollToBottom(); // 대화 로드 후 스크롤을 맨 아래로 이동
    }
  }

  // 대화 내용을 지우고 세션 스토리지에서 삭제
  clearConversation() {
    sessionStorage.removeItem('chatConversation');
    this.messages.innerHTML = '';
  }

  // 사용자 입력을 비활성화하는 함수
  disableUserInput() {
    this.sendButton.disabled = true; // 전송 버튼 비활성화
    this.isResponding = true; // AI 응답 중 상태로 변경
  }

  // 사용자 입력을 활성화하는 함수
  enableUserInput() {
    this.sendButton.disabled = false; // 전송 버튼 활성화
    this.isResponding = false; // AI 응답 완료 상태로 변경
  }

  // 사용자가 입력한 메시지를 서버에 전송하고 응답을 처리하는 함수
  sendMessage() {
    const message = this.userInput.value.trim(); // 입력한 메시지에서 공백 제거
    if (!message) return; // 입력이 비어있으면 종료
    this.userInput.value = ''; // 입력 필드 초기화

    // 기존 SSE 연결이 있으면 종료
    if (this.currentEventSource) {
      this.currentEventSource.close();
    }

    // 사용자의 메시지를 화면에 추가 (HTML 이스케이프 처리 후)
    const escapedMessage = this.escapeHtml(message);
    this.messages.insertAdjacentHTML('beforeend', `<p><strong>You:</strong> ${escapedMessage}</p>`);
    this.scrollToBottom(); // 메시지 추가 후 스크롤

    // AI 응답을 받을 요소 생성
    const aiResponseElement = document.createElement('div');
    aiResponseElement.className = 'ai-message';
    aiResponseElement.innerHTML = '<strong>AI:</strong> <span class="loading">Thinking...</span>';
    this.messages.appendChild(aiResponseElement);
    this.scrollToBottom(); // AI 응답 요소 추가 후 스크롤

    this.disableUserInput(); // 사용자의 입력을 비활성화

    // SSE를 통해 서버로 메시지 전송
    this.currentEventSource = new EventSource(`${this.backendUrl}/ai/api/chat/stream?content=${encodeURIComponent(message)}`);
    let lastResponse = ''; // 마지막으로 받은 응답을 저장할 변수

    // 서버로부터 메시지를 실시간으로 받음
    this.currentEventSource.onmessage = (event) => {
      try {
        const jsonResponse = JSON.parse(event.data); // 서버로부터 받은 데이터를 JSON으로 파싱
        if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
          lastResponse = jsonResponse.data.content; // 마지막 응답 내용 저장
          let parsedMarkdown = marked.parse(lastResponse); // 응답을 마크다운 형식으로 파싱
          // 코드 블록을 감싸는 추가 스타일 적용
          parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
          parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
          aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
          // 코드 블록 하이라이팅
          aiResponseElement.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightBlock(block);
          });
          this.saveConversation(); // 대화 저장
          this.scrollToBottom(); // 메시지 추가 후 스크롤
        }
      } catch (error) {
        console.error('Error processing response:', error, 'Raw data:', event.data);
      }
    };

    // SSE 오류 발생 시 처리
    this.currentEventSource.onerror = (event) => {
      console.error('EventSource failed:', event);
      this.currentEventSource.close();
      this.enableUserInput(); // 사용자 입력 활성화
      if (lastResponse) {
        // 마지막 응답이 있을 경우 마크다운 파싱 및 렌더링
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      } else {
        aiResponseElement.querySelector('.loading').textContent = 'Error occurred. Please try again.'; // 오류 메시지 표시
      }
      this.saveConversation(); // 대화 저장
      this.scrollToBottom(); // 스크롤 이동
    };

    // SSE 연결이 종료되었을 때 처리
    this.currentEventSource.onclose = () => {
      this.enableUserInput(); // 사용자 입력 활성화
      if (lastResponse) {
        // 마지막 응답이 있을 경우 마크다운 파싱 및 렌더링
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      }
      this.saveConversation(); // 대화 저장
      this.scrollToBottom(); // 스크롤 이동
    };
  }
}