/**
 * @author JSW
 * 챗봇 자바스크립트
 */

/**
 * BotFragChatbotComponent 클래스
 * 채팅봇 UI와 기능을 관리하는 클래스입니다.
 */
class BotFragChatbotComponent {
  /**
   * @param {string} backendUrl - 백엔드 서버 URL
   */
  constructor(backendUrl) {
    this.backendUrl = backendUrl;
    this.currentEventSource = null;
    this.isResponding = false;
    this.init();
  }

  /**
   * 컴포넌트 초기화 메서드
   * DOM 요소를 찾고 이벤트 리스너를 설정합니다.
   */
  init() {
    // DOM 요소 찾기
    this.hamburgerMenu = document.getElementById('bot-frag-hamburger-menu');
    this.chatSidebar = document.getElementById('bot-frag-sidebar');
    this.messages = document.getElementById('bot-frag-messages');
    this.userInput = document.getElementById('bot-frag-user-input');
    this.sendButton = document.getElementById('bot-frag-send-button');

    // 이벤트 리스너 설정
    if (this.hamburgerMenu) {
      this.hamburgerMenu.addEventListener('click', () => this.toggleChatbot());
    } else {
      console.error("Hamburger menu element not found for bot component");
    }

    if (this.userInput) {
      this.userInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter' && !this.isResponding) this.sendMessage();
      });
    }

    if (this.sendButton) {
      this.sendButton.addEventListener('click', () => {
        if (!this.isResponding) this.sendMessage();
      });
    }

    // 저장된 대화 불러오기 및 화면 조정
    this.loadConversation();
    this.scrollToBottom();

    window.addEventListener('resize', () => this.scrollToBottom());
  }

  /**
   * 채팅봇 UI 토글 메서드
   */
  toggleChatbot() {
    document.body.classList.toggle('bot-frag-open');
    if (this.chatSidebar) {
      this.chatSidebar.style.right = document.body.classList.contains('bot-frag-open') ? '0' : '-500px';
    }
    if (document.body.classList.contains('bot-frag-open')) {
      setTimeout(() => this.scrollToBottom(), 300);
    }
  }

  /**
   * 메시지 영역을 스크롤하여 최신 메시지를 보여주는 메서드
   */
  scrollToBottom() {
    if (this.messages) {
      this.messages.scrollTop = this.messages.scrollHeight;
    }
  }

  /**
   * HTML 특수 문자 이스케이프 처리 메서드
   * @param {string} unsafe - 이스케이프 처리할 문자열
   * @return {string} 이스케이프 처리된 문자열
   */
  escapeHtml(unsafe) {
    return unsafe
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
  }

  /**
   * 현재 대화 내용을 세션 스토리지에 저장하는 메서드
   */
  saveConversation() {
    sessionStorage.setItem('bot-frag-conversation', this.messages.innerHTML);
  }

  /**
   * 저장된 대화 내용을 불러오는 메서드
   */
  loadConversation() {
    const savedConversation = sessionStorage.getItem('bot-frag-conversation');
    if (savedConversation && this.messages) {
      this.messages.innerHTML = savedConversation;
      this.messages.querySelectorAll('pre code').forEach((block) => {
        hljs.highlightBlock(block);
      });
      this.scrollToBottom();
    }
  }

  /**
   * 대화 내용을 초기화하는 메서드
   */
  clearConversation() {
    sessionStorage.removeItem('bot-frag-conversation');
    if (this.messages) {
      this.messages.innerHTML = '';
    }
  }

  /**
   * 사용자 입력을 비활성화하는 메서드
   */
  disableUserInput() {
    if (this.sendButton) {
      this.sendButton.disabled = true;
    }
    this.isResponding = true;
  }

  /**
   * 사용자 입력을 활성화하는 메서드
   */
  enableUserInput() {
    if (this.sendButton) {
      this.sendButton.disabled = false;
    }
    this.isResponding = false;
  }

  /**
   * 메시지를 전송하고 응답을 처리하는 메서드
   */
  sendMessage() {
    const message = this.userInput.value.trim();
    if (!message) return;
    this.userInput.value = '';

    if (this.currentEventSource) {
      this.currentEventSource.close();
    }

    // 사용자 메시지 표시
    const escapedMessage = this.escapeHtml(message);
    this.messages.insertAdjacentHTML('beforeend', `<p><strong>You:</strong> ${escapedMessage}</p>`);
    this.scrollToBottom();

    // AI 응답 요소 생성
    const aiResponseElement = document.createElement('div');
    aiResponseElement.className = 'bot-frag-ai-message';
    aiResponseElement.innerHTML = '<strong>AI:</strong> <span class="bot-frag-loading">Thinking...</span>';
    this.messages.appendChild(aiResponseElement);
    this.scrollToBottom();

    this.disableUserInput();

    // EventSource를 사용하여 서버로부터 스트리밍 응답 받기
    this.currentEventSource = new EventSource(`${this.backendUrl}/ai/api/chat/stream?content=${encodeURIComponent(message)}`);
    let lastResponse = '';

    this.currentEventSource.onmessage = (event) => {
      try {
        const jsonResponse = JSON.parse(event.data);
        if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
          lastResponse = jsonResponse.data.content;
          let parsedMarkdown = marked.parse(lastResponse);
          parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="bot-frag-code-block-wrapper"><pre><code$1>');
          parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
          aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="bot-frag-markdown-body">${parsedMarkdown}</div>`;
          aiResponseElement.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightBlock(block);
          });
          this.saveConversation();
          this.scrollToBottom();
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
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="bot-frag-code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="bot-frag-markdown-body">${parsedMarkdown}</div>`;
      } else {
        aiResponseElement.querySelector('.bot-frag-loading').textContent = 'Error occurred. Please try again.';
      }
      this.saveConversation();
      this.scrollToBottom();
    };

    this.currentEventSource.onclose = () => {
      this.enableUserInput();
      if (lastResponse) {
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="bot-frag-code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="bot-frag-markdown-body">${parsedMarkdown}</div>`;
      }
      this.saveConversation();
      this.scrollToBottom();
    };
  }
}

// 전역 변수로 인스턴스 저장
let botFragChatbotInstance = null;

// DOM이 로드된 후 실행되는 코드
document.addEventListener('DOMContentLoaded', function() {
  const chatbotElement = document.getElementById('bot-frag-component');
  if (chatbotElement) {
    const backendUrl = chatbotElement.dataset.backendUrl;
    if (backendUrl) {
      botFragChatbotInstance = new BotFragChatbotComponent(backendUrl);
    } else {
      console.error("Backend URL is not set properly");
    }
  } else {
    console.error("Chatbot component element not found");
  }

  // 햄버거 메뉴 이벤트 리스너 추가
  const hamburgerMenu = document.getElementById('bot-frag-hamburger-menu');
  if (hamburgerMenu) {
    hamburgerMenu.addEventListener('click', function() {
      if (botFragChatbotInstance) {
        botFragChatbotInstance.toggleChatbot();
      } else {
        console.error("Chatbot instance not initialized");
      }
    });
  } else {
    console.error("Hamburger menu element not found");
  }
});