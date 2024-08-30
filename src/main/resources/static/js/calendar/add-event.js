// DOMContentLoaded 이벤트를 사용하여 DOM이 로드된 후에 FullCalendar를 초기화
document.addEventListener('DOMContentLoaded', function () {
    // 캘린더를 렌더링할 HTML 요소를 가져옵니다.
    var calendarEl = document.getElementById('calendar');

    // FullCalendar 인스턴스를 생성하고 옵션을 설정합니다.
    var calendar = new FullCalendar.Calendar(calendarEl, {
        height: 500, // 캘린더의 높이 설정
        initialView: 'dayGridMonth', // 초기 보기는 월간 보기
        stickyHeaderDates: true, //헤더에 요일 고정
        expandRows: true, //화면에 맞게 높이 재설정

        //locale: 'ko', // 한국어 설정
        customButtons: {
            addSchedule: {
                text: "일정 등록",
                click: function () {
                    alert("일정 등록 하세요!");
                },
            },
        },
        headerToolbar: {
            start: "today prevYear,prev,next,nextYear",
            center: "title",
            end: "addSchedule",
        },

    });

    // 캘린더를 렌더링합니다.
    calendar.render();
});
