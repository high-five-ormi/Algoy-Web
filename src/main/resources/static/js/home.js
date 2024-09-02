document.addEventListener('DOMContentLoaded', function() {
    let isModalOpen = false;

    function toggleModal() {
        const modal = document.querySelector('#myModal');
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
        modal.style.top = rect.bottom + 'px'; // 버튼 바로 아래 위치
        modal.style.left = rect.left + 'px'; // 버튼과 같은 수평 위치

        modal.style.display = 'block';
        isModalOpen = true;
    }

    function closeModal() {
        const modal = document.querySelector('#myModal');
        modal.style.display = 'none';
        isModalOpen = false;
    }

    // 모달 열기 버튼에 이벤트 리스너 추가
    const modalBtn = document.querySelector('#header-my-page');
    if (modalBtn) {
        modalBtn.addEventListener('click', function(event) {
            event.stopPropagation(); // 클릭 이벤트 버블링 방지
            toggleModal();
        });
    }

    // 문서 전체에 클릭 이벤트 리스너 추가
    document.addEventListener('click', function(event) {
        const modal = document.querySelector('#myModal');
        const modalBtn = document.querySelector('#header-my-page');

        // 모달이 열려있고, 클릭된 요소가 모달 외부이며 모달 버튼이 아닌 경우에만 모달 닫기
        if (isModalOpen && !modal.contains(event.target) && event.target !== modalBtn) {
            closeModal();
        }
    });

    // 모달 내부의 닫기 버튼에 대한 이벤트 리스너
    const closeBtn = document.querySelector('#myModal .close');
    if (closeBtn) {
        closeBtn.addEventListener('click', function(event) {
            event.stopPropagation(); // 클릭 이벤트 버블링 방지
            closeModal();
        });
    }
});
