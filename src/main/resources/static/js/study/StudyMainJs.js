let currentPage = 0;  // 현재 페이지 추적

$(document).ready(function() {
    let keyword = localStorage.getItem('searchKeyword') || ''; // 저장된 검색어가 있으면 가져옴

    // 페이지 로드 시 스터디 목록 불러오기
    loadStudyList(0, keyword);

    // 새로고침 또는 페이지 나가기 시 localStorage 초기화
    window.onbeforeunload = function () {
        localStorage.removeItem('searchKeyword'); // 검색어 초기화
    };
});

// 스터디 목록을 로드하는 함수
function loadStudyList(page, keyword = '') {
    $.ajax({
        url: "/algoy/study/gets",
        type: "GET",
        data: { page: page,
        keyword : keyword },
        success: function(data) {
            $('#study-list-container').empty(); // 이전 데이터를 지움
            $('#pagination').empty();  // 이전 페이지네이션을 지움

            // 스터디 목록을 출력
            data.content.forEach(function(study) {
                let statusText = '';
                let statusClass = '';

                // 스터디 상태에 따라 표시할 텍스트 및 클래스 결정
                switch(study.status) {
                    case 'ING':
                        statusText = '모집중';
                        statusClass = 'ING';
                        break;
                    case 'DONE':
                        statusText = '완료';
                        statusClass = 'DONE';
                        break;
                    case 'STOP':
                        statusText = '중단됨';
                        statusClass = 'STOP';
                        break;
                    default:
                        statusText = 'Unknown';
                        statusClass = 'UNKNOWN';
                }

                // 스터디 카드 생성 및 추가
                $('#study-list-container').append(`
                        <div class="study-card">
                            <div class="status ${statusClass}"><button class="${study.status}">${statusText}</button></div>
                            <h3><a href="#" class="study-title" data-id="${study.id}">${study.title}</a></h3>
                            <p>Create By ${study.author}</p>
                            <div class="language-tag"><button class="${study.language}">${study.language}</button></div>
                            <div class="participant-count" data-id="${study.id}"></div>
                        </div>
                    `);
                $.ajax({
                    url: "/algoy/study/count",
                    type: "GET",
                    data: { studyId : study.id },
                    success: function (response) {
                        $(`.participant-count[data-id="${study.id}"]`).text(`${response}/${study.maxParticipant}명`)
                    },
                    error: function (error) {
                        console.error('인원 수 불러오기 에러:', error);
                    }
                })
            });

            $('.study-title').on('click', function (event) {
                event.preventDefault();
                let studyId = $(this).data('id');
                loadStudyPage(studyId);
            });

            // 페이징 생성
            for (let i = 0; i < data.totalPages; i++) {
                let pageButtonClass = (i === page) ? 'active-page' : 'page-button';
                $('#pagination').append(`
                        <div class="${pageButtonClass}" data-page="${i}">${i + 1}</div>
                    `);
            }

            // 페이지 버튼 클릭 이벤트
            $('.page-button').click(function() {
                let selectedPage = $(this).data('page');
                loadStudyList(selectedPage, keyword);
            });

            currentPage = page;  // 현재 페이지 업데이트
        },
        error: function(error) {
            console.error('스터디 리스트 불러오기 에러:', error);
        }
    });
}

$('#btn-calendar').on('click', function (event) {
    event.preventDefault();

    $.ajax({
        url: "/algoy/study/new-form",
        method: "GET",
        success() {
            location.href = "/algoy/study/new-form";
        },
        error: function(error) {
            console.error('스터디 작성 폼을 불러오지 못했습니다.')
        }
    })
})

// 검색 버튼 클릭 이벤트
$('.search-button').click(function() {
    let keyword = $('.search-input').val(); // 검색어 입력받기
    loadStudyList(0, keyword);  // 검색 결과를 첫 페이지부터 다시 로드
});

$(document).on('click', '.ING', function() {
    let status = 'ING'; // 모집중 상태
    let page = 0; // 첫 페이지에서부터 시작

    // 상태에 맞는 목록을 로드
    loadStudyListByStatus(page, status);
});

$(document).on('click', '.DONE', function() {
    let status = 'DONE'; // 완료 상태
    let page = 0;

    loadStudyListByStatus(page, status);
});

$(document).on('click', '.STOP', function() {
    let status = 'STOP'; // 중단된 상태
    let page = 0;

    loadStudyListByStatus(page, status);
});

function loadStudyPage(studyId) {
    $.ajax({
        url: "/algoy/study/detail",
        data: { studyId : studyId },
        type: "GET",
        success: function () {
            location.href = "/algoy/study/detail?studyId=" + studyId
        },
        error: function (error) {
            console.error('페이지를 불러올 수 없습니다.')
        }
    })
}

function loadStudyListByStatus(page, status) {
    $.ajax({
        url: "/algoy/study/search-status",  // /algoy/study/search-status 사용
        type: "GET",
        data: { page: page, status: status },  // status 값 전달
        success: function(data) {
            $('#study-list-container').empty(); // 이전 데이터를 지움
            $('#pagination').empty();  // 이전 페이지네이션을 지움

            // 스터디 목록을 출력
            data.content.forEach(function(study) {
                let statusText = '';
                let statusClass = '';

                // 스터디 상태에 따라 표시할 텍스트 및 클래스 결정
                switch(study.status) {
                    case 'ING':
                        statusText = '모집중';
                        statusClass = 'ING';
                        break;
                    case 'DONE':
                        statusText = '완료';
                        statusClass = 'DONE';
                        break;
                    case 'STOP':
                        statusText = '중단됨';
                        statusClass = 'STOP';
                        break;
                    default:
                        statusText = 'Unknown';
                        statusClass = 'UNKNOWN';
                }

                // 스터디 카드 생성 및 추가
                $('#study-list-container').append(`
                    <div class="study-card">
                        <div class="status ${study.status}"><button class="${study.status}">${statusText}</button></div>
                        <h3><a href="#" class="study-title" data-id="${study.id}">${study.title}</a></h3>
                        <p>Create By ${study.author}</p>
                        <div class="language-tag"><button class="${study.language}">${study.language}</button></div>
                        <div class="participant-count" data-id="${study.id}"></div>
                    </div>
                `);

                // 참여 인원 수 가져오기
                $.ajax({
                    url: "/algoy/study/count",
                    type: "GET",
                    data: { studyId: study.id },
                    success: function(response) {
                        $(`.participant-count[data-id="${study.id}"]`).text(`${response}/${study.maxParticipant}명`);
                    },
                    error: function(error) {
                        console.error('인원 수 불러오기 에러:', error);
                    }
                });
            });

            // 스터디 제목 클릭 이벤트
            $('.study-title').on('click', function(event) {
                event.preventDefault();
                let studyId = $(this).data('id');
                loadStudyPage(studyId);
            });

            // 페이지네이션 생성
            for (let i = 0; i < data.totalPages; i++) {
                let pageButtonClass = (i === page) ? 'active-page' : 'page-button';
                $('#pagination').append(`
                    <div class="${pageButtonClass}" data-page="${i}">${i + 1}</div>
                `);
            }

            // 페이지 버튼 클릭 이벤트
            $('.page-button').click(function() {
                let selectedPage = $(this).data('page');
                loadStudyListByStatus(selectedPage, status);  // 선택한 상태로 목록 재로드
            });

            currentPage = page;  // 현재 페이지 업데이트
        },
        error: function(error) {
            console.error('스터디 리스트 불러오기 에러:', error);
        }
    });
}
