class ChatbotComponent {
  constructor(backendUrl) {
    this.backendUrl = backendUrl;
    this.currentEventSource = null;
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
      if (event.key === 'Enter') this.sendMessage();
    });
    this.sendButton.addEventListener('click', () => this.sendMessage());

    document.addEventListener('click', (event) => {
      if (!this.chatSidebar.contains(event.target) && !this.hamburgerMenu.contains(event.target)) {
        document.body.classList.remove('chatbot-open');
      }
    });

    this.loadConversation();
  }

  toggleChatbot() {
    document.body.classList.toggle('chatbot-open');
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
    }
  }

  clearConversation() {
    sessionStorage.removeItem('chatConversation');
    this.messages.innerHTML = '';
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
    const aiResponseElement = document.createElement('div');
    aiResponseElement.className = 'ai-message';
    aiResponseElement.innerHTML = '<strong>AI:</strong> <span class="loading">Thinking...</span>';
    this.messages.appendChild(aiResponseElement);

    this.sendButton.disabled = true;

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
        }
      } catch (error) {
        console.error('Error processing response:', error, 'Raw data:', event.data);
      }
    };

    this.currentEventSource.onerror = (event) => {
      console.error('EventSource failed:', event);
      this.currentEventSource.close();
      this.sendButton.disabled = false;
      if (lastResponse) {
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      } else {
        aiResponseElement.querySelector('.loading').textContent = 'Error occurred. Please try again.';
      }
      this.saveConversation();
    };

    this.currentEventSource.onclose = () => {
      this.sendButton.disabled = false;
      if (lastResponse) {
        let parsedMarkdown = marked.parse(lastResponse);
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
        aiResponseElement.innerHTML = `<strong>AI:</strong> <div class="markdown-body">${parsedMarkdown}</div>`;
      }
      this.saveConversation();
    };
  }
}