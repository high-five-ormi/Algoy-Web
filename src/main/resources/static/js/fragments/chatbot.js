class BotFragChatbotComponent {
  constructor(backendUrl) {
    this.backendUrl = backendUrl;
    this.currentEventSource = null;
    this.isResponding = false;
    this.init();
  }

  init() {
    this.hamburgerMenu = document.getElementById('bot-frag-hamburger-menu');
    this.chatSidebar = document.getElementById('bot-frag-sidebar');
    this.messages = document.getElementById('bot-frag-messages');
    this.userInput = document.getElementById('bot-frag-user-input');
    this.sendButton = document.getElementById('bot-frag-send-button');

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

    this.loadConversation();
    this.scrollToBottom();

    window.addEventListener('resize', () => this.scrollToBottom());
  }

  toggleChatbot() {
    document.body.classList.toggle('bot-frag-open');
    if (this.chatSidebar) {
      this.chatSidebar.style.right = document.body.classList.contains('bot-frag-open') ? '0' : '-500px';
    }
    if (document.body.classList.contains('bot-frag-open')) {
      setTimeout(() => this.scrollToBottom(), 300);
    }
  }

  scrollToBottom() {
    if (this.messages) {
      this.messages.scrollTop = this.messages.scrollHeight;
    }
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
    sessionStorage.setItem('bot-frag-conversation', this.messages.innerHTML);
  }

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

  clearConversation() {
    sessionStorage.removeItem('bot-frag-conversation');
    if (this.messages) {
      this.messages.innerHTML = '';
    }
  }

  disableUserInput() {
    if (this.sendButton) {
      this.sendButton.disabled = true;
    }
    this.isResponding = true;
  }

  enableUserInput() {
    if (this.sendButton) {
      this.sendButton.disabled = false;
    }
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
    this.scrollToBottom();

    const aiResponseElement = document.createElement('div');
    aiResponseElement.className = 'bot-frag-ai-message';
    aiResponseElement.innerHTML = '<strong>AI:</strong> <span class="bot-frag-loading">Thinking...</span>';
    this.messages.appendChild(aiResponseElement);
    this.scrollToBottom();

    this.disableUserInput();

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