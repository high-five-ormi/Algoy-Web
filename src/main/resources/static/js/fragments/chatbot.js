/**
 * @author JSW
 *
 * 사용자가 입력한 메시지를 서버에 전송하고, 서버로부터 실시간으로 응답을 받아 화면에 표시하는 기능을 수행합니다. (SSE 연결)
 * 메시지 전송 시 HTML 특수 문자를 이스케이프 처리하고, 서버 응답을 마크다운 형식으로 렌더링하는 기능도 합니다.
 */

class ChatbotComponent {
  constructor(backendUrl) {
    this.backendUrl = backendUrl;
    this.currentEventSource = null;
    this.isResponding = false;
    this.init();
  }

  init() {
    this.hamburgerMenu = document.getElementById('hamburger-menu');
    this.chatSidebar = document.getElementById('chat-sidebar');
    this.messages = document.getElementById('messages');
    this.userInput = document.getElementById('user-input');
    this.sendButton = document.getElementById('send-button');

    this.hamburgerMenu.addEventListener('click', () => this.toggleChatbot());
    this.userInput.addEventListener('keypress', (event) => {
      if (event.key === 'Enter' && !this.isResponding) this.sendMessage();
    });
    this.sendButton.addEventListener('click', () => {
      if (!this.isResponding) this.sendMessage();
    });

    document.addEventListener('click', (event) => {
      if (!this.chatSidebar.contains(event.target) && !this.hamburgerMenu.contains(event.target)) {
        document.body.classList.remove('chatbot-open');
      }
    });

    this.loadConversation();
    this.scrollToBottom(); // 초기 로드 시 스크롤

    // 창 크기 변경 시 스크롤 조정
    window.addEventListener('resize', () => this.scrollToBottom());
  }

  toggleChatbot() {
    document.body.classList.toggle('chatbot-open');
    // 챗봇을 열 때마다 스크롤
    if (document.body.classList.contains('chatbot-open')) {
      setTimeout(() => this.scrollToBottom(), 300); // 애니메이션 완료 후 스크롤
    }
  }

  scrollToBottom() {
    this.messages.scrollTop = this.messages.scrollHeight;
  }

  escapeHtml(unsafe) {
    return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
  }

  saveConversation() {
    sessionStorage.setItem('chatConversation', this.messages.innerHTML);
  }

  loadConversation() {
    const savedConversation = sessionStorage.getItem('chatConversation');
    if (savedConversation) {
      this.messages.innerHTML = savedConversation;
      this.messages.querySelectorAll('pre code').forEach((block) => {
        hljs.highlightBlock(block);
      });
      this.scrollToBottom(); // 대화 로드 후 스크롤
    }
  }

  clearConversation() {
    sessionStorage.removeItem('chatConversation');
    this.messages.innerHTML = '';
  }

  disableUserInput() {
    this.sendButton.disabled = true;
    this.isResponding = true;
  }

  enableUserInput() {
    this.sendButton.disabled = false;
    this.isResponding = false;
  }

  sendMessage() {
    const message = this.userInput.value.trim();
    if (!message) return;
    this.userInput.value = '';

    if (this.currentEventSource) {
      this.currentEventSource.close();
    }

    const escapedMessage = this.escapeHtml(message);
    this.messages.insertAdjacentHTML('beforeend', `<p><strong>You:</strong> ${escapedMessage}</p>`);
    this.scrollToBottom(); // 사용자 메시지 추가 후 스크롤

    const aiResponseElement = document.createElement('div');
    aiResponseElement.className = 'ai-message';
    aiResponseElement.innerHTML = '<strong>AI:</strong> <span class="loading">Thinking...</span>';
    this.messages.appendChild(aiResponseElement);
    this.scrollToBottom(); // AI 응답 요소 추가 후 스크롤

    this.disableUserInput();

    this.currentEventSource = new EventSource(`${this.backendUrl}/ai/api/chat/stream?content=${encodeURIComponent(message)}`);
    let lastResponse = '';

    this.currentEventSource.onmessage = (event) => {
      try {
        const jsonResponse = JSON.parse(event.data);
        if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
          lastResponse = jsonResponse.data.content;
          let parsedMarkdown = marked.parse(lastResponse);
          parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
          parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
          aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
          aiResponseElement.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightBlock(block);
          });
          this.saveConversation();
          this.scrollToBottom(); // 메시지 업데이트 후 스크롤
        }
      } catch (error) {
        console.error('Error processing response:', error, 'Raw data:', event.data);
      }
    };

    this.currentEventSource.onerror = (event) => {
      console.error('EventSource failed:', event);
      this.currentEventSource.close();
      this.enableUserInput();
      if (lastResponse) {
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      } else {
        aiResponseElement.querySelector('.loading').textContent = 'Error occurred. Please try again.';
      }
      this.saveConversation();
      this.scrollToBottom(); // 오류 발생 후 스크롤
    };

    this.currentEventSource.onclose = () => {
      this.enableUserInput();
      if (lastResponse) {
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      }
      this.saveConversation();
      this.scrollToBottom(); // 응답 완료 후 스크롤
    };
  }
}