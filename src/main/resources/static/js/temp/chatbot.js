/**
 * @author JSW
 *
 * 사용자가 입력한 메시지를 서버에 전송하고, 서버로부터 실시간으로 응답을 받아 화면에 표시하는 기능을 수행합니다. (SSE 연결)
 * 메시지 전송 시 HTML 특수 문자를 이스케이프 처리하고, 서버 응답을 마크다운 형식으로 렌더링하는 기능도 합니다.
 */

let currentEventSource = null;  // 현재 EventSource 객체를 저장하기 위한 변수

/**
 * 사용자 입력 메시지에서 안전하지 않은 HTML 문자를 이스케이프 처리하는 함수
 * @param {string} unsafe - 이스케이프 처리할 문자열
 * @return {string} - 이스케이프 처리된 문자열
 */
function escapeHtml(unsafe) {
  return unsafe
  .replace(/&/g, "&amp;")
  .replace(/</g, "&lt;")
  .replace(/>/g, "&gt;")
  .replace(/"/g, "&quot;")
  .replace(/'/g, "&#039;");
}

/**
 * 사용자가 메시지를 전송할 때 호출되는 함수
 * 메시지를 서버에 전송하고, 서버로부터의 응답을 처리함
 */
function sendMessage() {
  var input = document.getElementById('user-input');  // 사용자 입력 필드를 가져옴
  var message = input.value;  // 사용자 입력 값을 저장
  if (!message.trim()) return;  // 입력이 비어 있거나 공백만 있을 경우 함수 종료
  input.value = ''; // 입력 필드를 비움

  if (currentEventSource) {
    currentEventSource.close(); // 이전에 열려 있던 EventSource가 있으면 닫음
  }

  var escapedMessage = escapeHtml(message); // 사용자 입력 메시지를 HTML 이스케이프 처리
  $('#messages').append('<p><strong>You:</strong> ' + escapedMessage + '</p>'); // 사용자의 메시지를 화면에 추가
  var aiResponseElement = $('<p><strong>AI:</strong> <span class="loading">Thinking...</span></p>').appendTo('#messages');  // AI 응답에 대한 로딩 메시지를 화면에 추가

  $('#send-button').prop('disabled', true); // 메시지를 보내는 버튼을 비활성화

  // 서버와의 SSE (Server-Sent Events) 연결을 시작
  currentEventSource = new EventSource(backendUrl + '/ai/api/chat/stream?content=' + encodeURIComponent(message));
  var lastResponse = '';  // 서버에서 마지막으로 받은 응답을 저장하기 위한 변수

  // 서버에서 메시지를 받을 때 호출되는 이벤트 핸들러
  currentEventSource.onmessage = function(event) {
    try {
      var jsonResponse = JSON.parse(event.data);  // 서버로부터 받은 데이터를 JSON으로 파싱
      if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
        lastResponse = jsonResponse.data.content; // 마지막 응답 내용을 저장
        var parsedMarkdown = marked.parse(lastResponse);  // Markdown을 HTML로 변환

        // 코드 블록을 감싸는 div 태그 추가 (코드 블록 스타일링을 위해)
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');

        aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>'); // 변환된 HTML을 AI 응답 영역에 추가

        // 코드 블록 하이라이팅 적용
        aiResponseElement.find('pre code').each(function(i, block) {
          hljs.highlightBlock(block);
        });
      }
    } catch (error) {
      console.error('Error processing response:', error, 'Raw data:', event.data);  // 오류가 발생했을 경우 콘솔에 로그 출력
    }
  };

  // 서버와의 연결에서 오류가 발생했을 때 호출되는 이벤트 핸들러
  currentEventSource.onerror = function(event) {
    console.error('EventSource failed:', event);  // 오류 메시지를 콘솔에 출력
    currentEventSource.close(); // EventSource를 닫음
    $('#send-button').prop('disabled', false);  // 전송 버튼을 다시 활성화
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);  // 마지막으로 받은 응답을 Markdown에서 HTML로 변환
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>'); // 변환된 HTML을 AI 응답 영역에 추가
    } else {
      aiResponseElement.find('.loading').text('Error occurred. Please try again.'); // 오류 메시지를 표시
    }
  };

  // 서버와의 연결이 닫혔을 때 호출되는 이벤트 핸들러
  currentEventSource.onclose = function(event) {
    $('#send-button').prop('disabled', false);  // 전송 버튼을 다시 활성화
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);  // 마지막으로 받은 응답을 Markdown에서 HTML로 변환
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>'); // 변환된 HTML을 AI 응답 영역에 추가
    }
  };
}

// Enter 키를 눌렀을 때 sendMessage 함수 호출
document.getElementById('user-input').addEventListener('keypress', function(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
});

// 전송 버튼을 클릭했을 때 sendMessage 함수 호출
document.getElementById('send-button').addEventListener('click', sendMessage);