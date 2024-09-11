// DOMContentLoaded 이벤트 리스너 추가: 초기 HTML 문서가 완전히 로드되고 파싱되었을 때 발생
document.addEventListener('DOMContentLoaded', function () {
    // 모달 창 요소
    let modal = document.getElementById('modal');
    // 밴 폼 요소
    let banForm = document.getElementById('banForm');
    // 밴 사유 입력 필드
    let banReasonInput = document.getElementById('banReason');
    // 밴 대상 유저 ID를 저장할 hidden input
    let banUserIdInput = document.getElementById('banUserId');

    // 페이지 내의 모든 '유저 정지' 버튼들에게 각각 클릭 이벤트 리스너 추가
    let banButtons = document.querySelectorAll('.ban-btn');
    banButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            // 클릭된 버튼의 data-user-id 속성 값을 가져와 hidden input에 설정
            banUserIdInput.value = this.getAttribute('data-user-id');
            // 모달 창 표시
            modal.style.display = 'block';
        });
    });

    // '취소' 버튼에 클릭 이벤트 리스너 추가
    let cancelButton = document.getElementById('cancelBan');
    cancelButton.addEventListener('click', function () {
        // 모달 창을 닫기
        modal.style.display = 'none';
    });

    // 밴 폼 제출 시 유효성 검사 수행
    banForm.addEventListener('submit', function (e) {
        // 밴 사유가 비어있는지 확인
        if (!banReasonInput.value.trim()) { // 비어있으면 폼 제출을 막고, 경고 메시지 출력
            e.preventDefault();
            alert('정지 사유를 입력해 주세요!');
        }
    });
});