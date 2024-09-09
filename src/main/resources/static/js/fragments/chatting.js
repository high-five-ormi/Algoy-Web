let chatStompClient = null;
let chatCurrentRoomId = null;
let chatCurrentUserNickname = null;
let chatCurrentView = 'room-list';

function chatConnect() {
  const socket = new SockJS('/algoy/chat-websocket');
  chatStompClient = Stomp.over(socket);
  chatStompClient.connect({}, function () {
    chatLoadRooms();
    chatFetchCurrentUserInfo();
  }, function (error) {
    console.error('STOMP error:', error);
    setTimeout(chatConnect, 5000); // 5초 후 재연결 시도
  });
}

function chatFetchCurrentUserInfo() {
  fetch('/algoy/api/user/current')
  .then(response => response.json())
  .then(user => {
    chatCurrentUserNickname = user.nickname;
  })
  .catch(error => console.error('Error fetching current user info:', error));
}

function chatLoadRooms() {
  fetch('/algoy/api/chat/rooms')
  .then(response => response.json())
  .then(rooms => {
    const roomList = document.getElementById('chat-room-list');
    roomList.innerHTML = '';
    rooms.forEach(room => {
      const roomElement = document.createElement('div');
      roomElement.classList.add('chat-frag-room-item');
      roomElement.textContent = room.name;
      roomElement.onclick = () => chatJoinRoom(room.roomId);
      roomList.appendChild(roomElement);
    });
  })
  .catch(error => console.error('Error loading rooms:', error));
}

function chatCreateRoom() {
  const roomName = document.getElementById('chat-room-name').value.trim();
  const inviteUsers = document.getElementById('chat-invite-users').value.split(',').map(s => s.trim()).filter(s => s !== '');

  if (!roomName) {
    alert('방 이름을 입력해주세요.');
    return;
  }

  if (inviteUsers.length === 0) {
    alert('초대할 사용자를 최소 한 명 이상 입력해주세요.');
    return;
  }

  if (inviteUsers.includes(chatCurrentUserNickname)) {
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
    chatJoinRoom(room.roomId);
  })
  .catch(error => {
    console.error('Error creating room:', error);
    alert('방 생성 중 오류가 발생했습니다: ' + error.message);
  });
}

function chatJoinRoom(roomId) {
  if (chatCurrentRoomId) {
    chatStompClient.unsubscribe(chatCurrentRoomId);
  }
  fetch(`/algoy/api/chat/room/${roomId}/join`, {method: 'POST'})
  .then(response => response.json())
  .then(roomDto => {
    chatCurrentRoomId = roomDto.roomId;
    chatShowChatRoomView(roomDto.name);
    chatLoadMessages(roomDto.roomId);
    chatStompClient.subscribe(`/topic/room/${roomDto.roomId}`, chatOnMessageReceived, {id: chatCurrentRoomId});
  })
  .catch(error => console.error('Error joining room:', error));
}

function chatLeaveRoom() {
  if (chatCurrentRoomId) {
    fetch(`/algoy/api/chat/room/${chatCurrentRoomId}/leave`, {method: 'POST'})
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
      chatBackToRoomList();
    })
    .catch(error => {
      console.error('Error leaving room:', error);
      // 에러가 발생해도 일단 방 목록으로 돌아갑니다.
      chatBackToRoomList();
    });
  }
}

function chatLoadMessages(roomId) {
  fetch(`/algoy/api/chat/room/${roomId}/messages`)
  .then(response => response.json())
  .then(data => {
    const chatMessages = document.getElementById('chat-messages');
    chatMessages.innerHTML = '';
    // 메시지를 역순으로 표시
    data.content.reverse().forEach(message => {
      chatDisplayMessage(message);
    });
  })
  .catch(error => console.error('Error loading messages:', error));
}

function chatSendMessage() {
  const messageContent = document.getElementById('chat-user-input').value;
  if (messageContent && chatStompClient && chatCurrentRoomId) {
    const chatMessage = {
      roomId: chatCurrentRoomId,
      content: messageContent,
    };
    chatStompClient.send("/algoy/chat/sendMessage", {}, JSON.stringify(chatMessage));
    document.getElementById('chat-user-input').value = '';
  }
}

function chatOnMessageReceived(payload) {
  const message = JSON.parse(payload.body);
  chatDisplayMessage(message);
}

function chatDisplayMessage(message) {
  const messageElement = document.createElement('div');
  messageElement.classList.add('chat-frag-message');

  if (message.nickname === 'System') {
    messageElement.classList.add('system');
  } else if (message.nickname === chatCurrentUserNickname) {
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

  const messagesContainer = document.getElementById('chat-messages');
  messagesContainer.appendChild(messageElement);

  // 스크롤을 최신 메시지로 이동
  const scrollContainer = document.getElementById('chat-messages-container');
  scrollContainer.scrollTop = scrollContainer.scrollHeight;
}

function chatShowRoomListView() {
  chatHideAllViews();
  document.getElementById('chat-room-list-view').classList.remove('chat-frag-hidden');
  chatCurrentView = 'room-list';
}

function chatShowCreateRoomView() {
  chatHideAllViews();
  document.getElementById('chat-create-room-view').classList.remove('chat-frag-hidden');
  chatCurrentView = 'create-room';
}

function chatShowChatRoomView(roomName) {
  chatHideAllViews();
  document.getElementById('chat-room-view').classList.remove('chat-frag-hidden');
  document.getElementById('chat-room-name-header').textContent = roomName;
  chatCurrentView = 'chat-room';
}

function chatShowInviteModal() {
  document.getElementById('chat-invite-modal').classList.remove('chat-frag-hidden');
}

function chatHideInviteModal() {
  document.getElementById('chat-invite-modal').classList.add('chat-frag-hidden');
  document.getElementById('chat-invite-nickname').value = '';
}

function chatHideAllViews() {
  ['chat-room-list-view', 'chat-create-room-view', 'chat-room-view'].forEach(id => {
    document.getElementById(id).classList.add('chat-frag-hidden');
  });
  chatHideInviteModal();
}

function chatInviteUser() {
  const inviteeNickname = document.getElementById('chat-invite-nickname').value.trim();
  if (!inviteeNickname) {
    alert('Please enter a nickname to invite.');
    return;
  }

  fetch(`/algoy/api/chat/room/${chatCurrentRoomId}/invite-by-nickname`, {
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
    chatHideInviteModal();
  })
  .catch(error => {
    console.error('Error inviting user:', error);
    alert('Failed to invite user: ' + (error.error || 'Unknown error'));
  });
}

function chatGoBack() {
  if (chatCurrentView === 'chat-room') {
    chatBackToRoomList();
  } else if (chatCurrentView === 'create-room') {
    chatShowRoomListView();
  }
}

function chatBackToRoomList() {
  if (chatCurrentRoomId) {
    chatStompClient.unsubscribe(chatCurrentRoomId);
  }
  chatShowRoomListView();
  chatCurrentRoomId = null;
  chatLoadRooms();
}

function chatToggleChat() {
  document.body.classList.toggle('chat-open');
}

function chatInitialize() {
  // 햄버거 메뉴 토글 기능 추가
  document.getElementById('chat-hamburger-menu').addEventListener('click', chatToggleChat);

  document.getElementById('chat-create-room-btn').addEventListener('click', chatShowCreateRoomView);
  document.getElementById('chat-create-room-submit').addEventListener('click', chatCreateRoom);
  document.getElementById('chat-invite-user-btn').addEventListener('click', chatShowInviteModal);
  document.getElementById('chat-send-button').addEventListener('click', chatSendMessage);
  document.getElementById('chat-send-invite').addEventListener('click', chatInviteUser);
  document.getElementById('chat-cancel-invite').addEventListener('click', chatHideInviteModal);
  document.querySelectorAll('.chat-frag-back-btn').forEach(btn => {
    btn.addEventListener('click', chatGoBack);
  });
  document.getElementById('chat-leave-room-btn').addEventListener('click', chatLeaveRoom);

  document.getElementById('chat-user-input').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
      chatSendMessage();
    }
  });

  // 모달 외부 클릭 시 닫기
  window.addEventListener('click', function (event) {
    let modal = document.getElementById('chat-invite-modal');
    if (event.target === modal) {
      chatHideInviteModal();
    }
  });

  // 초기 연결 및 방 목록 로드
  chatConnect();
  chatLoadRooms();
  chatHideInviteModal(); // 초기에 모달이 표시되지 않도록 함
}

// DOM이 로드된 후 초기화 함수 실행
document.addEventListener('DOMContentLoaded', chatInitialize);