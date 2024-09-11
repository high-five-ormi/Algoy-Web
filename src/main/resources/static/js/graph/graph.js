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
                    '#b4ce8b',
                    '#cab8d9',
                    '#ABC2E8'
                ],
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: {
                        font: {
                            size: 16 // 레이블의 폰트 크기
                        },
                        color: '#333' // 레이블의 색상
                    }
                },
                tooltip: {
                    bodyFont: {
                        size: 14 // 툴팁 본문의 폰트 크기
                    },
                    titleFont: {
                        size: 16 // 툴팁 제목의 폰트 크기
                    }
                }
            }
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
            toggleView: {
                text: '주간 보기',
                click: function () {
                    const currentView = calendar.view.type;
                    if (currentView === 'dayGridWeek') {
                        calendar.changeView('dayGridMonth');
                        this.textContent = '주간 보기';
                    } else {
                        calendar.changeView('dayGridWeek');
                        this.textContent = '월간 보기';
                    }
                }
            },
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
            end: "toggleView addSchedule"
        },

        // 월 변경 시 차트 업데이트
        datesSet: function (info) {
            updateMonthName(info.view.currentStart);
            updateChart(info.view.currentStart);
        },

        // 이벤트를 서버에서 가져옴
        events: function(info, successCallback, failureCallback) {
            fetch('/algoy/planner/get/plans')
                .then(response => response.json())
                .then(data => {
                    const events = data.map(item => {
                        let endDate = new Date(item.endAt);
                        let color = '';

                        let allDay = false;
                        if (item.allDay || isOneDayEvent(item.startAt, item.endAt)) {
                            allDay = true;
                            endDate = new Date(item.startAt); // 하루짜리 이벤트의 경우 endDate를 startAt과 동일하게 설정
                        }

                        if (item.status === 'TODO') {
                            color = '#aebcd7';
                        } else if (item.status === 'IN_PROGRESS') {
                            color = '#cab8d9';
                        } else if (item.status === 'DONE') {
                            color = '#b4ce8b';
                        }

                        return {
                            id: item.id,
                            title: item.title,
                            start: item.startAt,
                            end: allDay ? null : endDate.toISOString(), // allDay 이벤트의 경우 end를 설정하지 않음
                            allDay: allDay, // allDay 설정
                            backgroundColor: color // FullCalendar의 색상 설정
                        };
                    });

                    successCallback(events);
                })
                .catch(error => {
                    console.error('Error loading events:', error);
                });
        },

        // 이벤트의 글자색
        eventContent: function(arg) {
            const title = document.createElement('div');
            title.className = 'fc-event-title';
            title.textContent = arg.event.title;

            title.style.color = '#212121';

            return { domNodes: [title] };
        },

        eventClick: function(info) {
            const eventId = info.event.id;
            window.location.href = `/algoy/planner/edit-form?id=${eventId}`; // 클릭 시 해당 플래너 페이지로 이동
        },
    });

    // 캘린더를 렌더링합니다.
    calendar.render();

    // 현재 월 이름을 업데이트
    function updateMonthName(startDate) {
        const start = new Date(startDate);
        const monthName = start.toLocaleString('default', { month: 'long', year: 'numeric' });
        document.getElementById('monthName').textContent = monthName;
    }

    // 차트 업데이트
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

    // 하루짜리 이벤트 체크 함수
    function isOneDayEvent(start, end) {
        const startDate = new Date(start);
        const endDate = new Date(end);
        return startDate.toDateString() === endDate.toDateString();
    }
});
