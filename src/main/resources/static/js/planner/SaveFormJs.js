
// 저장 버튼 클릭 시

$('.btn-create').on('click', function (event) {
    event.preventDefault();
    // 기본 값이 존재하는 값 제외하고 입력 받은 값을 String 화하여 빈 값이면 입력 받음
    const title = $('#title').val().trim();
    const content = $('#content').val().trim();
    const link = $('#link').val().trim();
    const startAt = $('#start-date').val().trim();
    const endAt = $('#end-date').val().trim();

    if (title === "") {
        alert('제목을 입력해주세요.');
        return;  // 제목이 비어있으면 요청 중단
    }
    if (content === "") {
        alert('본문을 입력해주세요.');
        return;  // 본문이 비어있으면 요청 중단
    }
    if (link === "") {
        alert('링크를 입력해주세요');
        return;
    }
    if (startAt === "") {
        alert('시작 날짜를 입력해주세요');
        return;
    }
    if (endAt === "") {
        alert('종료 날짜를 입력해주세요');
        return;
    }

    // json 통신할 객체 생성
    const plannerDto = {
        title: $('#title').val(),
        content: $('#content').val(),
        link: $('#link').val(),
        startAt: $('#start-date').val(),
        endAt: $('#end-date').val(),
        status: $(':radio[name="status"]:checked').val()
    };

    // json 통신
    $.ajax({
        method: "POST",
        url: "/algoy/planner/save",
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(plannerDto),
        success: function (response) {
            location.href = "/algoy/planner/calender"
        },
        error: function (error) {
            alert('등록에 실패했습니다.');
        }
    });
});