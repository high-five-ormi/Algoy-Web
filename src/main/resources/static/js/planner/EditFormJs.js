const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get('id');
$(document).ready(function (){
    $.ajax({
        type: "GET",
        url: "/algoy/planner/" + id,
        success: function (data) {
            $('#title').val(data.title);

            let restoredText = data.content
                .replace(/<br>/g, '\n')
                .replace(/&nbsp;/g, ' ')
                .replace(/<\/?p>/g, '')
                .replace(/<\/?pre>/g, '');
            $('#content').val(restoredText);
            $('#link').val(data.link);
            $(`input:radio[name="status"][value="${data.status}"]`).prop('checked', true);
            $('#start-date').val(data.startAt);
            $('#end-date').val(data.endAt);
            $('#site-dropdown').val(data.siteName);
            if(data.siteName === 'ETC') {
                $('#etc-input').show();
                $('#etc-input').val(data.etcName);
            }
            $('#question').val(data.questionName);
        },
        error: function (error) {
            alert('조회에 실패했습니다.');
        }
    });
})

$('.btn-update').on('click', function(event) {
    event.preventDefault();
    // 기본 값이 존재하는 값 제외하고 입력 받은 값을 String 화하여 빈 값이면 입력 받음
    const title = $('#title').val().trim();
    const content = $('#content').val().trim();
    const link = $('#link').val().trim();
    const startAt = $('#start-date').val().trim();
    const endAt = $('#end-date').val().trim();
    const questionName = $('#question').val().trim();
    const siteName = $('#site-dropdown').val().trim();
    const etcName = $('#etc-input').val().trim();

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
    if(questionName === "") {
        alert('문제 이름을 입력해주세요');
        return;
    }
    if(siteName === "ETC" && etcName === "") {
        alert('사이트 이름을 입력해주세요');
        return;
    }

    if(questionName.length > 20) {
        alert('문제 이름이 너무 깁니다.');
        $('#question').val('');
        return;
    }
    if(etcName.length > 20) {
        alert('사이트 이름이 너무 깁니다.');
        $('#etc-input').val('');
        return;
    }

    const startDate = new Date($('#start-date').val());
    const endDate = new Date($('#end-date').val());
    if(startDate.getTime() > endDate.getTime()) {
        alert('시작 날짜는 종료 날짜를 지날 수 없습니다.');
        return;
    }


    let text = $('#content').val();

    var html = text
        .trim()
        .split('\n\n') // Separate paragraphs
        .map(function(paragraph) {
            return '<p>' + paragraph
                .replace(/\n/g, '<br>')
                .replace(/ /g, '&nbsp;') + '</p>';
        })
        .join('');

    // 이미지 html 태그 변환
    html = html.replace(/!\[(.*?)\]\((.*?)\)/g, '<img src="$2" alt="$1">');

    // 링크 html 태그 변환
    html = html.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2">$1</a>');

    // json 통신할 객체 생성
    let plannerDto = {
        title: $('#title').val(),
        content: html,
        link: $('#link').val(),
        startAt: $('#start-date').val(),
        endAt: $('#end-date').val(),
        status: $(':radio[name="status"]:checked').val(),
        questionName: $('#question').val()
    };

    if(siteName === "ETC") {
        plannerDto.siteName = $('#site-dropdown').val();
        plannerDto.etcName = $('#etc-input').val();
    } else {
        plannerDto.siteName = $('#site-dropdown').val();
    }

    $.ajax({
        type: "POST",
        url: "/algoy/planner/edit/" + id,
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(plannerDto),
        success: function () {
            alert('수정에 성공했습니다.')
            location.href = "/algoy/planner/calender"
        },
        error: function (error) {
            alert('수정에 실패했습니다.');
            // 실패 시 의도한 URL로 이동
            location.href = '/algoy/planner/edit-form?id=' + id;
        }
    })
})

$(document).ready(function() {
    $('#site-dropdown').change(function () {
        if ($('#site-dropdown').val() === 'ETC') {
            $('#etc-input').show();
        } else {
            $('#etc-input').hide();
        }
    });
});