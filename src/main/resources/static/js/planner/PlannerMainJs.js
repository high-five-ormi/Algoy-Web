$(document).ready(function () {
    // 새 플랜 입력으로 넘어가는 버튼 입력 이벤트 핸들러
    $('#btn-calendar').on('click', function (event) {
        event.preventDefault();
        $.ajax({
            url: '/algoy/planner/save-form',
            method: 'GET',
            success: function (data) {
                location.href = '/algoy/planner/save-form';
            },
            error: function (error) {
                alert('플래너 로딩 에러 : ' + error.statusText);
            }
        });
    })

    // 플랜 수정으로 넘어가는 버튼 입력 이벤트 핸들러
    $('#btn-edit').on('click', function (event) {
        event.preventDefault();

        // 클릭된 버튼에서 data-id 값을 가져와 변수에 저장
        let plannerId = $(this).data('id');
        $.ajax({
            url: '/algoy/planner/edit-form?id=' + plannerId,
            method: 'GET',
            success: function (data) {
                location.href = '/algoy/planner/edit-form?id=' + plannerId;
            },
            error: function (error) {
                alert('플래너 로딩 에러 : ' + error.statusText);
            }
        });
    })

    // 플랜 삭제로 넘어가는 버튼 입력 이벤트 핸들러
    $('#btn-delete').on('click', function (event) {
        event.preventDefault();

        // 클릭된 버튼에서 data-id 값을 가져와 변수에 저장
        let plannerId = $(this).data('id');
        $.ajax({
            url: '/algoy/planner/delete/' + plannerId,
            method: 'POST',
            success: function (data) {
                alert('플랜 삭제 성공');
                location.href = '/algoy/planner/calender';
            },
            error: function (error) {
                alert('플래너 삭제 에러 : ' + error.statusText);
            }
        });
    })

    // 모달 내 닫기 버튼
    $('.modal-main-close').on('click', function () {
        $('#planner-modal').hide();
    });

    // 모달 바깥 클릭 시 모달 닫기
    $(window).on('click', function (event) {
        if ($(event.target).is('#planner-modal')) {
            $('#planner-modal').hide();
        }
    });
});

$(document).ready(function () {
    // 페이지 로드 시 localStorage에서 저장된 검색어를 가져와서 플래너 목록 불러오기
    let keyword = localStorage.getItem('searchKeyword') || ''; // 저장된 검색어가 있으면 가져옴

    if(keyword !== '')
        loadPlannerList(keyword);
    else
        initPlannerList();

    // 검색 버튼 클릭 이벤트
    $('.search-button').on('click', function (event) {
        event.preventDefault();
        let searchKeyword = $('.search-input').val(); // 검색 입력 값
        localStorage.setItem('searchKeyword', searchKeyword); // 검색어 저장
        loadPlannerList(searchKeyword); // 검색어에 따라 플래너 목록을 불러옴
    });

    // 새로고침 또는 페이지 나가기 시 localStorage 초기화
    window.onbeforeunload = function () {
        localStorage.removeItem('searchKeyword'); // 검색어 초기화
    };
});

// 모달 불러오는 ajax 메소드
function loadPlannerDetail(plannerId) {
    $.ajax({
        url: '/algoy/planner/' + plannerId,
        method: 'GET',
        success: function (data) {
            $('#modal-main-title').text(data.title);
            $('#modal-main-content').html(data.content);
            $('#modal-main-createdAt').text(`Created on ${formatDateTime(data.createAt)}`);
            $('#modal-main-timeAt').text(`${formatDate(data.startAt)} ~ ${formatDate(data.endAt)}`);
            $('#modal-main-question').text(data.questionName);
            if (data.siteName.trim() === "BOJ") {
                $('#modal-main-site').text("Baekjoon");
            } else if (data.siteName.trim() === "PGS") {
                $('#modal-main-site').text("Programmers");
            } else if (data.siteName.trim() === "SWEA") {
                $('#modal-main-site').text("SW Expert Academy");
            } else if (data.siteName.trim() === "ETC") {
                $('#modal-main-site').text(data.etcName);
            }
            $('#modal-main-link').attr('href', data.link);
            $('#modal-main-link').text(data.link);
            $('#planner-modal').show();
            $('#btn-edit').attr('data-id', data.id);
            $('#btn-delete').attr('data-id', data.id);
        },
        error: function (error) {
            alert('플래너 로딩 에러 : ' + error.statusText);
        }
    });
}

function initPlannerList() {
    $('.loader').show();

    // 서버에서 플래너 가져와서 정보 입력
    $.ajax({
        url: '/algoy/planner/get/plans',
        method: 'GET',
        success: function (data) {
            $('.loader').hide();

            let tableBody = $('#planner-table tbody');
            $.each(data, function (index, item) {
                // status에 맞는 입력값 한글화
                let statusClass = '';
                let statusText = '';
                if (item.status === 'TODO') {
                    statusClass = 'status-not-started';
                    statusText = '준비 중';
                } else if (item.status === 'IN_PROGRESS') {
                    statusClass = 'status-in-progress';
                    statusText = '진행 중';
                } else if (item.status === 'DONE') {
                    statusClass = 'status-done';
                    statusText = '완료';
                }

                let convertSiteName = '';

                if (item.siteName.trim() === "BOJ") {
                    convertSiteName = 'Baekjoon';
                } else if (item.siteName.trim() === "PGS") {
                    convertSiteName = 'Programmers';
                } else if (item.siteName.trim() === "SWEA") {
                    convertSiteName = 'SW Expert Academy';
                } else if (item.siteName.trim() === "ETC") {
                    convertSiteName = 'ETC';
                }

                // 테이블 행 추가
                let row = '<tr>' +
                    '<td><a href="#" class="table-content" data-id="' + item.id + '">' + item.title + '</a></td>' +
                    '<td><span>' + item.startAt + '<br>~<br>' + item.endAt + '</span></td>' +
                    '<td>' + convertSiteName + '</td>' +
                    '<td>' + item.questionName + '</td>' +
                    '<td class="planner-table-status-container"><div class="' + statusClass + '">' + statusText + '</div></td>' +
                    '</tr>';

                tableBody.append(row);
            });

            // 테이블 content 클릭 시 모달 실행
            $('.table-content').on('click', function (event) {
                event.preventDefault();
                let plannerId = $(this).data('id');
                loadPlannerDetail(plannerId);
            });
        },
        error: function (error) {
            $('.loader').hide();
            alert('에러 로딩 데이터 : ' + error.statusText);
        }
    });
}

// 플래너 목록을 불러오는 함수
function loadPlannerList(keyword) {
    // 로더 표시 (로딩 중일 때)
    $('.loader').show();

    // 서버에서 플래너 가져오기
    $.ajax({
        url: '/algoy/planner/search?keyword=' + encodeURIComponent(keyword),
        method: 'GET',
        success: function (data) {
            $('.loader').hide(); // 로더 숨기기

            let tableBody = $('#planner-table tbody');
            tableBody.empty(); // 기존 테이블 비우기

            // 받은 데이터로 테이블 행 추가
            $.each(data, function (index, item) {
                // status에 맞는 한글화 및 클래스 설정
                let statusClass = '';
                let statusText = '';
                if (item.status === 'TODO') {
                    statusClass = 'status-not-started';
                    statusText = '준비 중';
                } else if (item.status === 'IN_PROGRESS') {
                    statusClass = 'status-in-progress';
                    statusText = '진행 중';
                } else if (item.status === 'DONE') {
                    statusClass = 'status-done';
                    statusText = '완료';
                }

                // 사이트 이름 변환
                let convertSiteName = '';
                if (item.siteName.trim() === "BOJ") {
                    convertSiteName = 'Baekjoon';
                } else if (item.siteName.trim() === "PGS") {
                    convertSiteName = 'Programmers';
                } else if (item.siteName.trim() === "SWEA") {
                    convertSiteName = 'SW Expert Academy';
                } else if (item.siteName.trim() === "ETC") {
                    convertSiteName = 'ETC';
                }

                // 테이블 행 생성
                let row = '<tr>' +
                    '<td><a href="#" class="table-content" data-id="' + item.id + '">' + item.title + '</a></td>' +
                    '<td><span>' + item.startAt + '<br>~<br>' + item.endAt + '</span></td>' +
                    '<td>' + convertSiteName + '</td>' +
                    '<td>' + item.questionName + '</td>' +
                    '<td class="planner-table-status-container"><div class="' + statusClass + '">' + statusText + '</div></td>' +
                    '</tr>';

                tableBody.append(row);
            });

            // 테이블의 각 content 클릭 시 모달 실행
            $('.table-content').on('click', function (event) {
                event.preventDefault();
                let plannerId = $(this).data('id');
                loadPlannerDetail(plannerId);
            });
        },
        error: function (error) {
            $('.loader').hide(); // 에러 시 로더 숨기기
            alert('에러 로딩 데이터 : ' + error.statusText);
        }
    });
}

// 시간을 포맷팅하는 함수
function formatDateTime(dateTimeString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'};
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('en-US', options);
}

function formatDate(dateTimeString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric'};
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('en-US', options);
}