document.addEventListener('DOMContentLoaded', function () {
    // 캔버스 컨텍스트 가져오기
    const ctx = document.getElementById('myChart').getContext('2d');

    // 차트 생성
    const chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: [
                '완료',
                '진행 중',
                '시작 전',
            ],
            datasets: [{
                label: 'User`s commit Log',
                data: [0, 0, 0], // 초기 데이터 (나중에 업데이트됨)
                backgroundColor: [
                    '#ABC2E8',
                    '#F9F3CC',
                    '#b4ce8b'
                ],
                hoverOffset: 4
            }]
        },
        options: {
            responsive: false
        }
    });

    // FullCalendar 초기화
    const calendarEl = document.getElementById('calendar');
    const calendar = new FullCalendar.Calendar(calendarEl, {
        height: 500,
        initialView: 'dayGridMonth',
        stickyHeaderDates: true,
        expandRows: true,

        // 사용자 정의 버튼 및 헤더 툴바 설정
        customButtons: {
            addSchedule: {
                text: "일정 등록",
                click: function () {
                    window.location.href = '/algoy/planner/save-form';
                }
            }
        },
        headerToolbar: {
            start: "today prevYear,prev,next,nextYear",
            center: "title",
            end: "addSchedule"
        },

        // 월 변경 시 차트 업데이트
        datesSet: function (info) {
            updateMonthName(info.view.currentStart);
            updateChart(info.view.currentStart);
        },

        // 이벤트를 서버에서 가져오는 설정
        events: function(info, successCallback, failureCallback) {
            fetch('/algoy/planner/get/plans')
                .then(response => response.json())
                .then(data => {
                    const events = data.map(item => {
                        let color = '';

                        if (item.status === 'TODO') {
                            color = '#aebcd7';
                        } else if (item.status === 'IN_PROGRESS') {
                            color = '#F9F3CC';
                        } else if (item.status === 'DONE') {
                            color = '#b4ce8b';
                        }

                        return {
                            id: item.id,
                            title: item.title,
                            start: item.startAt,
                            end: item.endAt,
                            color: color
                        };
                    });

                    successCallback(events);
                })
                .catch(error => {
                    console.error('Error loading events:', error);
                });
        }
    });

    // 캘린더를 렌더링합니다.
    calendar.render();

    // 현재 월 이름을 업데이트하는 함수
    function updateMonthName(startDate) {
        const start = new Date(startDate);
        const monthName = start.toLocaleString('default', { month: 'long', year: 'numeric' });
        document.getElementById('monthName').textContent = monthName;
    }

    // 차트 업데이트 함수
    function updateChart(startDate) {
        const start = new Date(startDate);
        const end = new Date(start.getFullYear(), start.getMonth() + 1, 0); // 월의 마지막 날

        fetch('/algoy/planner/get/plans')
            .then(response => response.json())
            .then(data => {
                // 상태별 카운트 초기화
                let todoCount = 0;
                let inProgressCount = 0;
                let doneCount = 0;

                // 현재 월의 데이터만 필터링
                data.forEach(item => {
                    const itemStart = new Date(item.startAt);
                    const itemEnd = new Date(item.endAt);

                    if (itemStart >= start && itemEnd <= end) {
                        if (item.status === 'TODO') {
                            todoCount++;
                        } else if (item.status === 'IN_PROGRESS') {
                            inProgressCount++;
                        } else if (item.status === 'DONE') {
                            doneCount++;
                        }
                    }
                });

                // 차트 데이터 업데이트
                chart.data.datasets[0].data = [doneCount, inProgressCount, todoCount];
                chart.update();
            })
            .catch(error => {
                console.error('Error loading data:', error);
            });
    }
});
