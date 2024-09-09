const chatFrag = {
  stompClient: null,
  currentRoomId: null,
  currentUserNickname: null,
  currentView: 'room-list',

  connect: function() {
    const socket = new SockJS('/algoy/chat-websocket');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, () => {
      this.loadRooms();
      this.fetchCurrentUserInfo();
    }, (error) => {
      console.error('STOMP error:', error);
      setTimeout(() => this.connect(), 5000);
    });
  },

  fetchCurrentUserInfo: function() {
    fetch('/algoy/api/user/current')
    .then(response => response.json())
    .then(user => {
      this.currentUserNickname = user.nickname;
    })
    .catch(error => console.error('Error fetching current user info:', error));
  },

  loadRooms: function() {
    fetch('/algoy/api/chat/rooms')
    .then(response => response.json())
    .then(rooms => {
      const roomList = document.getElementById('chat-frag-room-list');
      roomList.innerHTML = '';
      rooms.forEach(room => {
        const roomElement = document.createElement('div');
        roomElement.classList.add('chat-frag-room-item');
        roomElement.textContent = room.name;
        roomElement.onclick = () => this.joinRoom(room.roomId);
        roomList.appendChild(roomElement);
      });
    })
    .catch(error => console.error('Error loading rooms:', error));
  },

  createRoom: function() {
    const roomName = document.getElementById('chat-frag-room-name').value.trim();
    const inviteUsers = document.getElementById('chat-frag-invite-users').value.split(',').map(s => s.trim()).filter(s => s !== '');

    if (!roomName) {
      alert('방 이름을 입력해주세요.');
      return;
    }

    if (inviteUsers.length === 0) {
      alert('초대할 사용자를 최소 한 명 이상 입력해주세요.');
      return;
    }

    if (inviteUsers.includes(this.currentUserNickname)) {
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
        return response.json().then(err => { throw err; });
      }
      return response.json();
    })
    .then(room => {
      this.joinRoom(room.roomId);
    })
    .catch(error => {
      console.error('Error creating room:', error);
      alert('방 생성 중 오류가 발생했습니다: ' + error.message);
    });
  },

  joinRoom: function(roomId) {
    if (this.currentRoomId) {
      this.stompClient.unsubscribe(this.currentRoomId);
    }
    fetch(`/algoy/api/chat/room/${roomId}/join`, {method: 'POST'})
    .then(response => response.json())
    .then(roomDto => {
      this.currentRoomId = roomDto.roomId;
      this.showChatRoomView(roomDto.name);
      this.loadMessages(roomDto.roomId);
      this.stompClient.subscribe(`/topic/room/${roomDto.roomId}`, this.onMessageReceived.bind(this), {id: this.currentRoomId});
    })
    .catch(error => console.error('Error joining room:', error));
  },

  leaveRoom: function() {
    if (this.currentRoomId) {
      fetch(`/algoy/api/chat/room/${this.currentRoomId}/leave`, {method: 'POST'})
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to leave room');
        }
        return response.json();
      })
      .then(data => {
        if (data.deleted) {
          alert('마지막으로 남은 사용자이므로 채팅방이 삭제됩니다.');
        }
        this.backToRoomList();
      })
      .catch(error => {
        console.error('Error leaving room:', error);
        this.backToRoomList();
      });
    }
  },

  loadMessages: function(roomId) {
    fetch(`/algoy/api/chat/room/${roomId}/messages`)
    .then(response => response.json())
    .then(data => {
      const chatMessages = document.getElementById('chat-frag-messages');
      chatMessages.innerHTML = '';
      data.content.reverse().forEach(message => {
        this.displayMessage(message);
      });
    })
    .catch(error => console.error('Error loading messages:', error));
  },

  sendMessage: function() {
    const messageContent = document.getElementById('chat-frag-user-input').value;
    if (messageContent && this.stompClient && this.currentRoomId) {
      const chatMessage = {
        roomId: this.currentRoomId,
        content: messageContent,
      };
      this.stompClient.send("/algoy/chat/sendMessage", {}, JSON.stringify(chatMessage));
      document.getElementById('chat-frag-user-input').value = '';
    }
  },

  onMessageReceived: function(payload) {
    const message = JSON.parse(payload.body);
    this.displayMessage(message);
  },

  displayMessage: function(message) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('chat-frag-message');

    if (message.nickname === 'System') {
      messageElement.classList.add('system');
    } else if (message.nickname === this.currentUserNickname) {
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

    const messagesContainer = document.getElementById('chat-frag-messages');
    messagesContainer.appendChild(messageElement);

    const scrollContainer = document.getElementById('chat-frag-messages-container');
    scrollContainer.scrollTop = scrollContainer.scrollHeight;
  },

  showRoomListView: function() {
    this.hideAllViews();
    document.getElementById('chat-frag-room-list-view').classList.remove('chat-frag-hidden');
    this.currentView = 'room-list';
  },

  showCreateRoomView: function() {
    this.hideAllViews();
    document.getElementById('chat-frag-create-room-view').classList.remove('chat-frag-hidden');
    this.currentView = 'create-room';
  },

  showChatRoomView: function(roomName) {
    this.hideAllViews();
    document.getElementById('chat-frag-room-view').classList.remove('chat-frag-hidden');
    document.getElementById('chat-frag-room-name-header').textContent = roomName;
    this.currentView = 'chat-room';
  },

  showInviteModal: function() {
    document.getElementById('chat-frag-invite-modal').classList.remove('chat-frag-hidden');
  },

  hideInviteModal: function() {
    document.getElementById('chat-frag-invite-modal').classList.add('chat-frag-hidden');
    document.getElementById('chat-frag-invite-nickname').value = '';
  },

  hideAllViews: function() {
    ['chat-frag-room-list-view', 'chat-frag-create-room-view', 'chat-frag-room-view'].forEach(id => {
      document.getElementById(id).classList.add('chat-frag-hidden');
    });
    this.hideInviteModal();
  },

  inviteUser: function() {
    const inviteeNickname = document.getElementById('chat-frag-invite-nickname').value.trim();
    if (!inviteeNickname) {
      alert('Please enter a nickname to invite.');
      return;
    }

    fetch(`/algoy/api/chat/room/${this.currentRoomId}/invite-by-nickname`, {
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
      this.hideInviteModal();
    })
    .catch(error => {
      console.error('Error inviting user:', error);
      alert('Failed to invite user: ' + (error.error || 'Unknown error'));
    });
  },

  goBack: function() {
    if (this.currentView === 'chat-room') {
      this.backToRoomList();
    } else if (this.currentView === 'create-room') {
      this.showRoomListView();
    }
  },

  backToRoomList: function() {
    if (this.currentRoomId) {
      this.stompClient.unsubscribe(this.currentRoomId);
    }
    this.showRoomListView();
    this.currentRoomId = null;
    this.loadRooms();
  },

  toggleChat: function() {
    document.body.classList.toggle('chat-open');
    const sidebar = document.getElementById('chat-frag-sidebar');
    if (sidebar) {
      sidebar.style.right = document.body.classList.contains('chat-open') ? '0' : '-500px';
    }
  },

  initialize: function() {
    const hamburgerMenu = document.getElementById('chat-frag-hamburger-menu');
    if (hamburgerMenu) {
      hamburgerMenu.addEventListener('click', this.toggleChat.bind(this));
    } else {
      console.error("Hamburger menu element not found for chat component");
    }

    document.getElementById('chat-frag-create-room-btn').addEventListener('click', this.showCreateRoomView.bind(this));
    document.getElementById('chat-frag-create-room-submit').addEventListener('click', this.createRoom.bind(this));
    document.getElementById('chat-frag-invite-user-btn').addEventListener('click', this.showInviteModal.bind(this));
    document.getElementById('chat-frag-send-button').addEventListener('click', this.sendMessage.bind(this));
    document.getElementById('chat-frag-send-invite').addEventListener('click', this.inviteUser.bind(this));
    document.getElementById('chat-frag-cancel-invite').addEventListener('click', this.hideInviteModal.bind(this));
    document.querySelectorAll('.chat-frag-back-btn').forEach(btn => {
      btn.addEventListener('click', this.goBack.bind(this));
    });
    document.getElementById('chat-frag-leave-room-btn').addEventListener('click', this.leaveRoom.bind(this));

    document.getElementById('chat-frag-user-input').addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        this.sendMessage();
      }
    });

    window.addEventListener('click', (event) => {
      let modal = document.getElementById('chat-frag-invite-modal');
      if (event.target === modal) {
        this.hideInviteModal();
      }
    });

    this.connect();
    this.loadRooms();
    this.hideInviteModal();
  }
};

document.addEventListener('DOMContentLoaded', function() {
  chatFrag.initialize();
});