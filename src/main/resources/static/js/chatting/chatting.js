let stompClient = null;
let currentRoomId = null;
let currentUserNickname = null;
let currentView = 'room-list';

function connect() {
  const socket = new SockJS('/algoy/chat-websocket');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function () {
    loadRooms();
    fetchCurrentUserInfo();
  }, function (error) {
    console.error('STOMP error:', error);
    setTimeout(connect, 5000); // 5초 후 재연결 시도
  });
}

function fetchCurrentUserInfo() {
  fetch('/algoy/api/user/current')
  .then(response => response.json())
  .then(user => {
    currentUserNickname = user.nickname;
  })
  .catch(error => console.error('Error fetching current user info:', error));
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

function createRoom() {
  const roomName = document.getElementById('room-name').value.trim();
  const inviteUsers = document.getElementById('invite-users').value.split(
      ',').map(s => s.trim()).filter(s => s !== '');

  if (!roomName) {
    alert('방 이름을 입력해주세요.');
    return;
  }

  if (inviteUsers.length === 0) {
    alert('초대할 사용자를 최소 한 명 이상 입력해주세요.');
    return;
  }

  if (inviteUsers.includes(currentUserNickname)) {
    alert('자기 자신을 초대할 수 없습니다.');
    return;
  }

  fetch('/algoy/api/chat/room', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({name: roomName, invitees: inviteUsers})
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => {
        throw err;
      });
    }
    return response.json();
  })
  .then(room => {
    joinRoom(room.roomId);
  })
  .catch(error => {
    console.error('Error creating room:', error);
    alert('방 생성 중 오류가 발생했습니다: ' + error.message);
  });
}

function joinRoom(roomId) {
  if (currentRoomId) {
    stompClient.unsubscribe(currentRoomId);
  }
  fetch(`/algoy/api/chat/room/${roomId}/join`, {method: 'POST'})
  .then(response => response.json())
  .then(roomDto => {
    currentRoomId = roomDto.roomId;
    showChatRoomView(roomDto.name);
    loadMessages(roomDto.roomId);
    stompClient.subscribe(`/topic/room/${roomDto.roomId}`, onMessageReceived,
        {id: currentRoomId});
  })
  .catch(error => console.error('Error joining room:', error));
}

function leaveRoom() {
  if (currentRoomId) {
    fetch(`/algoy/api/chat/room/${currentRoomId}/leave`, {method: 'POST'})
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to leave room');
      }
      return response.json();
    })
    .then(data => {
      if (data.deleted) {
        // 방이 삭제된 경우
        alert('마지막으로 남은 사용자이므로 채팅방이 삭제됩니다.');
      }
      backToRoomList();
    })
    .catch(error => {
      console.error('Error leaving room:', error);
      // 에러가 발생해도 일단 방 목록으로 돌아갑니다.
      backToRoomList();
    });
  }
}

function loadMessages(roomId) {
  fetch(`/algoy/api/chat/room/${roomId}/messages`)
  .then(response => response.json())
  .then(data => {
    const chatMessages = document.getElementById('messages');
    chatMessages.innerHTML = '';
    // 메시지를 역순으로 표시
    data.content.reverse().forEach(message => {
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
    stompClient.send("/algoy/chat/sendMessage", {},
        JSON.stringify(chatMessage));
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

  if (message.nickname === 'System') {
    messageElement.classList.add('system');
  } else if (message.nickname === currentUserNickname) {
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

  // 스크롤을 최신 메시지로 이동
  const scrollContainer = document.getElementById('messages-container');
  scrollContainer.scrollTop = scrollContainer.scrollHeight;
}

function showRoomListView() {
  hideAllViews();
  document.getElementById('room-list-view').classList.remove('hidden');
  currentView = 'room-list';
}

function showCreateRoomView() {
  hideAllViews();
  document.getElementById('create-room-view').classList.remove('hidden');
  currentView = 'create-room';
}

function showChatRoomView(roomName) {
  hideAllViews();
  document.getElementById('chat-room-view').classList.remove('hidden');
  document.getElementById('room-name-header').textContent = roomName;
  currentView = 'chat-room';
}

function showInviteModal() {
  document.getElementById('invite-modal').classList.remove('hidden');
}

function hideInviteModal() {
  document.getElementById('invite-modal').classList.add('hidden');
  document.getElementById('invite-nickname').value = '';
}

function hideAllViews() {
  ['room-list-view', 'create-room-view', 'chat-room-view'].forEach(id => {
    document.getElementById(id).classList.add('hidden');
  });
  hideInviteModal();
}

function inviteUser() {
  const inviteeNickname = document.getElementById('invite-nickname').value.trim();
  if (!inviteeNickname) {
    alert('Please enter a nickname to invite.');
    return;
  }

  fetch(`/algoy/api/chat/room/${currentRoomId}/invite-by-nickname`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nickname: inviteeNickname })
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(err => { throw err; });
    }
    return response.json();
  })
  .then(data => {
    alert(data.message || '초대에 성공하였습니다.');
    hideInviteModal();
  })
  .catch(error => {
    console.error('Error inviting user:', error);
    alert('Failed to invite user: ' + (error.error || 'Unknown error'));
  });
}

function goBack() {
  if (currentView === 'chat-room') {
    backToRoomList();
  } else if (currentView === 'create-room') {
    showRoomListView();
  }
}

function backToRoomList() {
  if (currentRoomId) {
    stompClient.unsubscribe(currentRoomId);
  }
  showRoomListView();
  currentRoomId = null;
  loadRooms();
}

function initializeChat() {
  document.getElementById('create-room-btn').addEventListener('click',
      showCreateRoomView);
  document.getElementById('create-room-submit').addEventListener('click',
      createRoom);
  document.getElementById('invite-user-btn').addEventListener('click',
      showInviteModal);
  document.getElementById('send-button').addEventListener('click', sendMessage);
  document.getElementById('send-invite').addEventListener('click', inviteUser);
  document.getElementById('cancel-invite').addEventListener('click',
      hideInviteModal);
  document.querySelectorAll('.back-btn').forEach(btn => {
    btn.addEventListener('click', goBack);
  });
  document.getElementById('leave-room-btn').addEventListener('click',
      leaveRoom);

  document.getElementById('user-input').addEventListener('keypress',
      function (e) {
        if (e.key === 'Enter') {
          sendMessage();
        }
      });

  // 모달 외부 클릭 시 닫기
  window.addEventListener('click', function (event) {
    let modal = document.getElementById('invite-modal');
    if (event.target === modal) {
      hideInviteModal();
    }
  });
}

window.onload = function () {
  connect();
  initializeChat();
  hideInviteModal(); // 초기에 모달이 표시되지 않도록 함
};