document.addEventListener('DOMContentLoaded', function () {
    // 사용자 닉네임을 가져오는 함수
    function fetchUserNickname() {
        fetch('/algoy/user/nickname')
            .then(response => {
                if (response.status === 401) {
                    // 로그인하지 않은 경우
                    document.querySelector('.nickname').textContent = "로그인하세요!";
                } else if (response.ok) {
                    // 정상적으로 데이터를 받아오는 경우
                    return response.json();
                } else {
                    // 기타 서버 오류
                    document.querySelector('.nickname').textContent = "오류가 발생했습니다.";
                }
            })
            .then(data => {
                if (data && data.nickname) {
                    document.querySelector('.nickname').textContent = data.nickname;
                }
            })
            .catch(error => {
                document.querySelector('.nickname').textContent = "오류가 발생했습니다.";
            });
    }

    // 페이지가 로드되면 사용자 닉네임을 가져옵니다.
    fetchUserNickname();


    let isModalOpen = false;

    function toggleModal() {
        if (isModalOpen) {
            closeModal();
        } else {
            openModal();
        }
    }

    function openModal() {
        const modal = document.querySelector('#myModal');
        const modalBtn = document.querySelector('#header-my-page');
        const rect = modalBtn.getBoundingClientRect();
        const modalRect = modal.getBoundingClientRect();

        // 모달을 버튼 중앙에 맞추기
        modal.style.top = rect.bottom + 'px';
        modal.style.left = rect.left + 'px';
        modal.style.display = 'block';
        isModalOpen = true;
    }

    function closeModal() {
        document.querySelector('#myModal').style.display = 'none';
        isModalOpen = false;
    }

    // 모달 열기 버튼에 이벤트 리스너 추가
    const modalBtn = document.querySelector('#header-my-page');
    if (modalBtn) {
        modalBtn.addEventListener('click', function (event) {
            event.stopPropagation(); // 클릭 이벤트 버블링 방지
            toggleModal();
        });
    }

    // 문서 전체에 클릭 이벤트 리스너 추가
    document.addEventListener('click', function (event) {
        const modal = document.querySelector('#myModal');
        const modalBtn = document.querySelector('#header-my-page');

        if (isModalOpen && !modal.contains(event.target) && event.target !== modalBtn) {
            closeModal();
        }
    });

    // 모달 내부의 닫기 버튼에 대한 이벤트 리스너
    const closeBtn = document.querySelector('#myModal .close');
    if (closeBtn) {
        closeBtn.addEventListener('click', function (event) {
            event.stopPropagation(); // 클릭 이벤트 버블링 방지
            closeModal();
        });
    }
});
