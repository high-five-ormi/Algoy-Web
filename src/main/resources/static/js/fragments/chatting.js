/**
 * @author JSW
 * 채팅 자바스크립트
 */

/**
 * chatFrag 객체: 채팅 기능을 관리하는 메인 객체
 */
const chatFrag = {
  stompClient: null,
  currentRoomId: null,
  currentView: 'room-list',

  /**
   * WebSocket 연결을 설정하고 초기화하는 메서드
   */
  connect: function() {
    const socket = new SockJS('/algoy/chat-websocket');
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, () => {
      this.loadRooms();
      this.fetchCurrentUserInfo();
    }, (error) => {
      console.error('STOMP error:', error);
      setTimeout(() => this.connect(), 5000);  // 5초 후 재연결 시도
    });
  },

  /**
   * 현재 사용자 정보를 가져오는 메서드
   */
  fetchCurrentUserInfo: function() {
    return fetch('/algoy/api/user/current')
    .then(response => response.json())
    .then(user => {
      // sessionStorage에 사용자 정보 저장
      sessionStorage.setItem('currentUserNickname', user.nickname);
    })
    .catch(error => console.error('Error fetching current user info:', error));
  },

  /**
   * 채팅방 목록을 로드하고 화면에 표시하는 메서드
   */
  loadRooms: function() {
    fetch('/algoy/api/chat/rooms')
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // 서버 응답 구조에 따라 적절히 처리
      const rooms = Array.isArray(data) ? data : data.content;

      const roomList = document.getElementById('chat-frag-room-list');
      roomList.innerHTML = '';

      if (Array.isArray(rooms)) {
        rooms.forEach(room => {
          const roomElement = document.createElement('div');
          roomElement.classList.add('chat-frag-room-item');
          roomElement.textContent = room.name;
          roomElement.onclick = () => this.joinRoom(room.roomId);
          roomList.appendChild(roomElement);
        });
      } else {
        console.error('Unexpected data format:', data);
      }
    })
    .catch(error => {
      console.error('Error loading rooms:', error);
      // 사용자에게 오류 메시지 표시
      const roomList = document.getElementById('chat-frag-room-list');
      roomList.innerHTML = '<p>채팅방 목록을 불러오는 데 실패했습니다.</p>';
    });
  },

  /**
   * 새로운 채팅방을 생성하는 메서드
   */
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
      let errorMessage;
      switch(error.code) {
        case 'INVALID_ROOM_NAME':
          errorMessage = '유효하지 않은 방 이름입니다.';
          break;
        case 'NO_INVITEES':
          errorMessage = '초대할 사용자가 없습니다.';
          break;
        case 'INVALID_INVITEES':
          errorMessage = '유효한 초대 대상자가 없습니다.';
          break;
        default:
          errorMessage = '방 생성 중 오류가 발생했습니다: ' + (error.message || '알 수 없는 오류');
      }
      alert(errorMessage);
    });
  },

  /**
   * 특정 채팅방에 입장하는 메서드
   * @param {string} roomId - 입장할 채팅방 ID
   */
  joinRoom: function(roomId) {
    if (this.currentRoomId) {
      this.stompClient.unsubscribe(this.currentRoomId);
    }
    fetch(`/algoy/api/chat/room/${roomId}/join`, {method: 'POST'})
    .then(response => response.json())
    .then(roomDto => {
      this.currentRoomId = roomDto.roomId;
      this.showChatRoomView(roomDto.name);
      this.fetchCurrentUserInfo().then(() => {
        this.loadMessages(roomDto.roomId);
      });
      this.stompClient.subscribe(`/topic/room/${roomDto.roomId}`, this.onMessageReceived.bind(this), {id: this.currentRoomId});
    })
    .catch(error => console.error('Error joining room:', error));
  },

  /**
   * 현재 채팅방에서 나가는 메서드
   */
  leaveRoom: function() {
    if (this.currentRoomId) {
      fetch(`/algoy/api/chat/room/${this.currentRoomId}/leave`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'}
      })
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

  /**
   * 특정 채팅방의 메시지를 로드하는 메서드
   * @param {string} roomId - 메시지를 로드할 채팅방 ID
   */
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

  /**
   * 메시지를 전송하는 메서드
   */
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

  /**
   * 새 메시지를 수신했을 때 호출되는 콜백 메서드
   * @param {Object} payload - 수신된 메시지 페이로드
   */
  onMessageReceived: function(payload) {
    const message = JSON.parse(payload.body);
    this.displayMessage(message);
  },

  /**
   * 메시지를 화면에 표시하는 메서드
   * @param {Object} message - 표시할 메시지 객체
   */
  displayMessage: function(message) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('chat-frag-message');

    const currentUserNickname = sessionStorage.getItem('currentUserNickname');

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

    const messagesContainer = document.getElementById('chat-frag-messages');
    messagesContainer.appendChild(messageElement);

    const scrollContainer = document.getElementById('chat-frag-messages-container');
    scrollContainer.scrollTop = scrollContainer.scrollHeight;
  },

  // 뷰 전환 메서드들
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

  /**
   * 사용자를 채팅방에 초대하는 메서드
   */
  inviteUser: function() {
    const inviteeNickname = document.getElementById('chat-frag-invite-nickname').value.trim();
    if (!inviteeNickname) {
      alert('초대하려는 사용자의 닉네임을 입력해주세요.');
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
      alert('초대에 성공하였습니다.');
      this.hideInviteModal();
    })
    .catch(error => {
      console.error('Error inviting user:', error);
      let errorMessage;
      switch(error.code) {
        case 'USER_NOT_FOUND':
          errorMessage = '초대하려는 사용자가 존재하지 않습니다.';
          break;
        case 'NOT_ROOM_OWNER':
          errorMessage = '채팅방 소유자만 초대할 수 있습니다.';
          break;
        case 'USER_ALREADY_IN_ROOM':
          errorMessage = '사용자가 이미 채팅방에 참여하고 있습니다.';
          break;
        case 'SELF_INVITATION_NOT_ALLOWED':
          errorMessage = '자기 자신을 초대할 수 없습니다.';
          break;
        default:
          errorMessage = '사용자 초대 중 오류가 발생했습니다: ' + (error.message || '알 수 없는 오류');
      }
      alert(errorMessage);
    });
  },

  /**
   * 이전 화면으로 돌아가는 메서드
   */
  goBack: function() {
    if (this.currentView === 'chat-room') {
      this.backToRoomList();
    } else if (this.currentView === 'create-room') {
      this.showRoomListView();
    }
  },

  /**
   * 채팅방 목록 화면으로 돌아가는 메서드
   */
  backToRoomList: function() {
    if (this.currentRoomId) {
      this.stompClient.unsubscribe(this.currentRoomId);
    }
    this.showRoomListView();
    this.currentRoomId = null;
    this.loadRooms();
  },

  /**
   * 채팅 사이드바를 토글하는 메서드
   */
  toggleChat: function() {
    document.body.classList.toggle('chat-open');
    const sidebar = document.getElementById('chat-frag-sidebar');
    if (sidebar) {
      sidebar.style.right = document.body.classList.contains('chat-open') ? '0' : '-500px';
    }
  },

  /**
   * 채팅 컴포넌트를 초기화하는 메서드
   */
  initialize: function() {
    // 이벤트 리스너 설정
    const hamburgerMenu = document.getElementById('chat-frag-hamburger-menu');
    if (hamburgerMenu) {
      hamburgerMenu.addEventListener('click', this.toggleChat.bind(this));
    } else {
      console.error("Hamburger menu element not found for chat component");
    }

    document.getElementById('chat-frag-create-room-btn').addEventListener(
        'click', this.showCreateRoomView.bind(this));
    document.getElementById('chat-frag-create-room-submit').addEventListener(
        'click', this.createRoom.bind(this));
    document.getElementById('chat-frag-invite-user-btn').addEventListener(
        'click', this.showInviteModal.bind(this));
    document.getElementById('chat-frag-send-button').addEventListener('click',
        this.sendMessage.bind(this));
    document.getElementById('chat-frag-send-invite').addEventListener('click',
        this.inviteUser.bind(this));
    document.getElementById('chat-frag-cancel-invite').addEventListener('click',
        this.hideInviteModal.bind(this));
    document.querySelectorAll('.chat-frag-back-btn').forEach(btn => {
      btn.addEventListener('click', this.goBack.bind(this));
    });
    document.getElementById('chat-frag-leave-room-btn').addEventListener(
        'click', this.leaveRoom.bind(this));

    document.getElementById('chat-frag-user-input').addEventListener('keypress',
        (e) => {
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

    // 초기 설정
    this.fetchCurrentUserInfo().then(() => {
      this.connect();
      this.loadRooms();
      this.hideInviteModal();
    });
  }
};

// DOM이 로드된 후 채팅 컴포넌트 초기화
document.addEventListener('DOMContentLoaded', function() {
  chatFrag.initialize();
});