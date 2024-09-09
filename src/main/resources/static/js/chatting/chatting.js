let stompClient = null;
let currentRoomId = null;
let currentUserId = null; // 이 값은 서버에서 받아오거나 로그인 시 설정해야 합니다.

function connect() {
  const socket = new SockJS('/algoy/chat-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    loadRooms();
  }, function(error) {
    console.error('STOMP error:', error);
    setTimeout(connect, 5000); // 5초 후 재연결 시도
  });
}

function loadRooms() {
  fetch('/algoy/api/chat/rooms')
  .then(response => response.json())
  .then(rooms => {
    const roomList = document.getElementById('room-list');
    roomList.innerHTML = '';
    rooms.forEach(room => {
      const roomElement = document.createElement('div');
      roomElement.classList.add('room-item');
      roomElement.textContent = room.name;
      roomElement.onclick = () => joinRoom(room.roomId);
      roomList.appendChild(roomElement);
    });
  })
  .catch(error => console.error('Error loading rooms:', error));
}

function showView(viewId) {
  ['room-list-view', 'create-room-view', 'chat-room-view'].forEach(id => {
    document.getElementById(id).classList.add('hidden');
  });
  document.getElementById(viewId).classList.remove('hidden');
}

document.getElementById('create-room-btn').onclick = () => showView('create-room-view');
document.querySelectorAll('.back-btn').forEach(btn => {
  btn.onclick = () => showView('room-list-view');
});

document.getElementById('create-room-submit').onclick = createRoom;
document.getElementById('leave-room-btn').onclick = leaveRoom;
document.getElementById('send-button').onclick = sendMessage;

function createRoom() {
  const roomName = document.getElementById('room-name').value;
  const inviteUsers = document.getElementById('invite-users').value.split(',').map(s => s.trim());

  fetch('/algoy/api/chat/room', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name: roomName })
  })
  .then(response => response.json())
  .then(room => {
    inviteUsers.forEach(nickname => {
      fetch(`/algoy/api/chat/room/${room.roomId}/invite-by-nickname`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nickname: nickname })
      }).then(response => {
        if (!response.ok) {
          console.error('Failed to invite user:', nickname);
        }
      }).catch(error => {
        console.error('Error inviting user:', error);
      });
    });
    joinRoom(room.roomId);
  })
  .catch(error => console.error('Error creating room:', error));
}

function joinRoom(roomId) {
  if (currentRoomId) {
    stompClient.unsubscribe(currentRoomId);
  }
  fetch(`/algoy/api/chat/room/${roomId}/join`, { method: 'POST' })
  .then(() => {
    currentRoomId = roomId;
    showView('chat-room-view');
    document.getElementById('room-name-header').textContent = `Room: ${roomId}`;
    loadMessages(roomId);
    stompClient.subscribe(`/topic/room/${roomId}`, onMessageReceived, {id: currentRoomId});
  })
  .catch(error => console.error('Error joining room:', error));
}

function leaveRoom() {
  if (currentRoomId) {
    fetch(`/algoy/api/chat/room/${currentRoomId}/leave`, { method: 'POST' })
    .then(() => {
      stompClient.unsubscribe(currentRoomId);
      showView('room-list-view');
      currentRoomId = null;
    })
    .catch(error => console.error('Error leaving room:', error));
  }
}

function loadMessages(roomId) {
  fetch(`/algoy/api/chat/room/${roomId}/messages`)
  .then(response => response.json())
  .then(data => {
    const chatMessages = document.getElementById('messages');
    chatMessages.innerHTML = '';
    data.content.forEach(message => {
      displayMessage(message);
    });
  })
  .catch(error => console.error('Error loading messages:', error));
}

function sendMessage() {
  const messageContent = document.getElementById('user-input').value;
  if (messageContent && stompClient && currentRoomId) {
    const chatMessage = {
      roomId: currentRoomId,
      content: messageContent,
    };
    stompClient.send("/algoy/chat/sendMessage", {}, JSON.stringify(chatMessage));
    document.getElementById('user-input').value = '';
  }
}

function onMessageReceived(payload) {
  const message = JSON.parse(payload.body);
  displayMessage(message);
}

function displayMessage(message) {
  const messageElement = document.createElement('div');
  messageElement.classList.add('message');
  if (message.userId === currentUserId) {
    messageElement.classList.add('sent');
  } else {
    messageElement.classList.add('received');
  }
  const time = new Date(message.createdAt).toLocaleString();
  messageElement.innerHTML = `
        <strong>${message.nickname}</strong><br>
        ${message.content}<br>
        <small>${time}</small>
    `;
  const messagesContainer = document.getElementById('messages');
  messagesContainer.appendChild(messageElement);
  messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 엔터 키로 메시지 전송
document.getElementById('user-input').addEventListener('keypress', function(e) {
  if (e.key === 'Enter') {
    sendMessage();
  }
});

window.onload = connect;