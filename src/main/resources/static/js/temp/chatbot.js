let currentEventSource = null;

function escapeHtml(unsafe) {
  return unsafe
  .replace(/&/g, "&amp;")
  .replace(/</g, "&lt;")
  .replace(/>/g, "&gt;")
  .replace(/"/g, "&quot;")
  .replace(/'/g, "&#039;");
}

function sendMessage() {
  var input = document.getElementById('user-input');
  var message = input.value;
  if (!message.trim()) return;
  input.value = '';

  if (currentEventSource) {
    currentEventSource.close();
  }

  var escapedMessage = escapeHtml(message);
  $('#messages').append('<p><strong>You:</strong> ' + escapedMessage + '</p>');
  var aiResponseElement = $('<p><strong>AI:</strong> <span class="loading">Thinking...</span></p>').appendTo('#messages');

  $('#send-button').prop('disabled', true);

  currentEventSource = new EventSource(backendUrl + '/ai/api/chat/stream?content=' + encodeURIComponent(message));
  var lastResponse = '';

  currentEventSource.onmessage = function(event) {
    try {
      var jsonResponse = JSON.parse(event.data);
      if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
        lastResponse = jsonResponse.data.content;
        var parsedMarkdown = marked.parse(lastResponse);

        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');

        aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');

        aiResponseElement.find('pre code').each(function(i, block) {
          hljs.highlightBlock(block);
        });
      }
    } catch (error) {
      console.error('Error processing response:', error, 'Raw data:', event.data);
    }
  };

  currentEventSource.onerror = function(event) {
    console.error('EventSource failed:', event);
    currentEventSource.close();
    $('#send-button').prop('disabled', false);
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    } else {
      aiResponseElement.find('.loading').text('Error occurred. Please try again.');
    }
  };

  currentEventSource.onclose = function(event) {
    $('#send-button').prop('disabled', false);
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    }
  };
}

document.getElementById('user-input').addEventListener('keypress', function(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
});

document.getElementById('send-button').addEventListener('click', sendMessage);

// 햄버거 메뉴 토글 기능
document.getElementById('hamburger-menu').addEventListener('click', function() {
  document.body.classList.toggle('open');
});

// 사이드바 외부 클릭 시 닫기
document.addEventListener('click', function(event) {
  if (!document.getElementById('chat-sidebar').contains(event.target) &&
      !document.getElementById('hamburger-menu').contains(event.target)) {
    document.body.classList.remove('open');
  }
});