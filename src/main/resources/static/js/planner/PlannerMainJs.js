$(document).ready(function() {
    $('.loader').show();

    // 서버에서 플래너 가져와서 정보 입력
    $.ajax({
        url: '/algoy/planner/get/plans',
        method: 'GET',
        success: function(data) {
            $('.loader').hide();

            let tableBody = $('#planner-table tbody');
            $.each(data, function(index, item) {
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

                if(item.siteName.trim() === "BOJ") {
                    convertSiteName = 'Baekjoon';
                } else if(item.siteName.trim() === "PGS"){
                    convertSiteName = 'Programmers';
                } else if(item.siteName.trim() === "SWEA") {
                    convertSiteName = 'SW Expert Academy';
                } else if(item.siteName.trim() === "ETC") {
                    convertSiteName = 'ETC';
                }

                // 테이블 행 추가
                let row = '<tr>' +
                    '<td>' + item.id + '</td>' +
                    '<td>' + item.title + '</td>' +
                    '<td><a href="#" class="table-content" data-id="' + item.id + '">Content</a></td>' +
                    '<td>' + convertSiteName + '</td>' +
                    '<td>' + item.questionName + '</td>' +
                    '<td class="planner-table-status-container"><div class="' + statusClass + '">' + statusText + '</div></td>' +
                    '</tr>';

                tableBody.append(row);
            });

            // 테이블 content 클릭 시 모달 실행
            $('.table-content').on('click', function(event) {
                event.preventDefault();
                let plannerId = $(this).data('id');
                loadPlannerDetail(plannerId);
            });
        },
        error: function(error) {
            $('.loader').hide();
            alert('에러 로딩 데이터 : ' + error.statusText);
        }
    });

    // 새 플랜 입력으로 넘어가는 버튼 입력 이벤트 핸들러
    $('#btn-calendar').on('click', function(event) {
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
    $('#btn-edit').on('click', function(event) {
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
    $('#btn-delete').on('click', function(event) {
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

    // 모달 불러오는 ajax 메소드
    function loadPlannerDetail(plannerId) {
        $.ajax({
            url: '/algoy/planner/' + plannerId,
            method: 'GET',
            success: function(data) {
                $('#modal-title').text(data.title);
                $('#modal-content').text(data.content);
                $('#modal-createdAt').text(`Created on ${formatDateTime(data.createAt)}`);
                $('#modal-timeAt').text(`${formatDate(data.startAt)} ~ ${formatDate(data.endAt)}`);
                $('#modal-question').text(data.questionName);
                if(data.siteName.trim() === "BOJ") {
                    $('#modal-site').text("Baekjoon");
                } else if(data.siteName.trim() === "PGS"){
                    $('#modal-site').text("Programmers");
                } else if(data.siteName.trim() === "SWEA") {
                    $('#modal-site').text("SW Expert Academy");
                } else if(data.siteName.trim() === "ETC") {
                    $('#modal-site').text(data.etcName);
                }
                $('#modal-link').attr('href', data.link);
                $('#modal-link').text(data.link);
                $('#planner-modal').show();
                $('#btn-edit').attr('data-id', data.id);
                $('#btn-delete').attr('data-id', data.id);
            },
            error: function(error) {
                alert('플래너 로딩 에러 : ' + error.statusText);
            }
        });
    }

    // 모달 내 닫기 버튼
    $('.modal-close').on('click', function() {
        $('#planner-modal').hide();
    });

    // 모달 바깥 클릭 시 모달 닫기
    $(window).on('click', function(event) {
        if ($(event.target).is('#planner-modal')) {
            $('#planner-modal').hide();
        }
    });

    // 시간을 포맷팅하는 함수
    function formatDateTime(dateTimeString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        const date = new Date(dateTimeString);
        return date.toLocaleDateString('en-US', options);
    }

    function formatDate(dateTimeString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric'};
        const date = new Date(dateTimeString);
        return date.toLocaleDateString('en-US', options);
    }
});