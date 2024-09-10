$(document).ready(function() {
    let postUrl = window.location.href;
    const urlParams = new URL(location.href).searchParams;
    const studyId = urlParams.get('studyId'); // Example Study ID
    let isParticipant = false;
    let presentParticipant;
    let statusText;
    let statusClass;

    $.ajax({
        url: `/algoy/study/get/${studyId}`,
        type: "GET",
        success: function(study) {
            $('#study-title').text(study.title);
            $('#study-author').text(study.author);
            $('#study-createdAt').text(study.createdAt);
            $('#study-status').text(study.status);
            $('#study-language').text(study.language);
            $('#study-content').html(study.content);

            if (study.status === 'TODO') {
                statusClass = 'status-not-started';
                statusText = '준비 중';
            } else if (study.status === 'IN_PROGRESS') {
                statusClass = 'status-in-progress';
                statusText = '진행 중';
            } else if (study.status === 'DONE') {
                statusClass = 'status-done';
                statusText = '완료';
            }

            $('#study-status').text(statusText);

            $.ajax({
                url: "/algoy/study/count",
                data : { studyId : urlParams.get("studyId") },
                method : "GET",
                success: function (data) {
                    $('#study-participant').text(data + "/" + study.maxParticipant)
                    presentParticipant = data;
                    if(data >= study.maxParticipant)
                        isParticipant = true;
                }
            })

            $.ajax({
                url: "/algoy/comment/find-user?studyId=" + urlParams.get("studyId"),
                method: "GET",
                success: function (response) {
                    if(response === false) {
                        $('#btn-delete').remove();
                        $('#btn-edit').remove();
                    }
                }
            })
        }
    });

    $.ajax({
        url: `/algoy/comment/gets`,
        data: { studyId : studyId },
        type: "GET",
        success: function(data) {
            renderComment(data, $('#comments-list'));
        }
    });

    $('#comment-form').submit(function(e) {
        e.preventDefault();

        let commentDto = {
            content: $('#comment').val(),
            createdAt: null,
            updatedAt: null,
            secret: $('input[id=secret]').is(':checked')
        }

        $.ajax({
            url: `/algoy/comment/non-reply?studyId=`+studyId,
            type: "POST",
            data: JSON.stringify(commentDto),
            contentType: 'application/json; charset=utf-8',
            success: function (data) {
                location.href = postUrl;
            }
        });
    });

    function renderComment(comments, container) {
        comments.forEach(function (comment) {
            const marginLeft = (comment.depth + 1) * 40;
            const commentHTML = `
                <li>
                    <div class="comment" data-id="${comment.id}" data-user-id="${comment.loginId}">
                        <div class="comment-sec">
                            <p class="comment-author">${comment.author}</p>
                            <div class="comment-body-date">
                                <strong class="comment-body" id="comment-body-${comment.id}">${comment.content}</strong>
                                <small class="comment-date">최종 수정 날짜: ${comment.updatedAt}</small>
                            </div>
                            <div class="comment-btn">
                                <button class="accept-btn btn-square-small" data-comment-id="${comment.id}" id="comment-accept-${comment.id}">수락</button>
                                <button class="edit-btn btn-square-small" data-comment-id="${comment.id}" id="comment-edit-${comment.id}">수정</button>
                                <button class="delete-btn btn-square-small" data-comment-id="${comment.id}"id="comment-delete-${comment.id}">삭제</button>
                                <button class="reply-btn btn-square-small"
                                    data-comment-id="${comment.id}" data-comment-depth="${comment.depth}" id="comment-reply-${comment.id}">답글</button>
                            </div>
                        </div>
                        <div class="replies" style="margin-left: ${marginLeft}px;"></div>
                    </div>
                </li>`;
            const commentElement = $(commentHTML);
            container.append(commentElement);
            renderComment(comment.children, commentElement.find('.replies'));

            if (comment.presentId !== comment.loginId) {
                commentElement.find('#comment-edit-' + comment.id).remove();
                commentElement.find('#comment-delete-' + comment.id).remove();
            }

            if (comment.activated === false) {
                commentElement.find('#comment-edit-' + comment.id).remove();
                commentElement.find('#comment-delete-' + comment.id).remove();
                commentElement.find('#comment-reply-' + comment.id).remove();
            }

            if (comment.presentId === null) {
                commentElement.find('#comment-reply-' + comment.id).remove();
            }

            if(isParticipant === true) {
                commentElement.find('#comment-accept-' + comment.id).remove();
            }

            $.ajax({
                url: "/algoy/comment/find-part?studyId=" + urlParams.get("studyId") + "&commentId=" + comment.id,
                method: "GET",
                success: function (response) {
                    if(response === true) {
                        const text =
                            `<button class="out-btn btn-square-small" data-comment-id="${comment.id}" id="comment-out-${comment.id}">탈퇴</button>`
                        commentElement.find('#comment-accept-' + comment.id).replaceWith(text);
                    }
                }
            })

            $.ajax({
                url: "/algoy/comment/find-user?studyId=" + urlParams.get("studyId"),
                method: "GET",
                success: function (response) {
                    if(response === false) {
                        commentElement.find('#comment-accept-' + comment.id).remove();
                        commentElement.find('#comment-out-' + comment.id).remove();
                    }
                }
            })
        })
    }

    $('#comments-list').on('click', '.delete-btn', function () {
        const commentId = $(this).closest('.comment').data('id');
        deleteComment(commentId);
    });

    $('#comments-list').on('click', '.edit-btn', function () {
        const commentId = $(this).data('comment-id');
        const commentTextElement = $(`#comment-body-${commentId}`);
        const commentText = commentTextElement.text();

        if (!$(`#edit-textarea-${commentId}`).length) {
            const textarea = $(`<textarea id="edit-textarea-${commentId}" class="edit-textarea" style="margin-top:10px;">${commentText}</textarea>
                <div class="secret-container"><input class="form-check-input" type="checkbox" value="" id="edit-secret">
                    <label class="form-check-label" for="edit-secret">
                        비밀 댓글
                    </label></div>
            `);
            commentTextElement.replaceWith(textarea);
        }

        $(this).text('저장').removeClass('edit-btn').addClass('save-btn');
    });

    $('#comments-list').on('click', '.save-btn', function () {
        const commentElement = $(this).closest('.comment');
        const commentId = commentElement.data('id');
        const textarea = commentElement.find('.edit-textarea');
        const newText = textarea.val();

        if (newText) {
            editComment(commentId, newText);
        }
    });

    $('#comments-list').on('click', '.accept-btn', function (event) {
        if(isParticipant === true) {
            event.preventDefault();
            alert("스터디 가입 인원이 최대입니다.");
            return;
        }
        const commentElement = $(this).closest('.comment');
        const commentId = commentElement.data('id');
        acceptComment(commentId);
    });

    $('#comments-list').on('click', '.out-btn', function (event) {
        if(presentParticipant === 0) {
            event.preventDefault();
            alert("탈퇴할 인원이 없습니다");
            return;
        }
        const commentElement = $(this).closest('.comment');
        const commentId = commentElement.data('id');
        outComment(commentId);
    });

    $('#comments-list').on('click', '.reply-btn', function () {
        const commentId = $(this).data('comment-id');
        const commentElement = $(this).closest('.comment');
        const repliesDiv = commentElement.find('> .replies');
        const commentDepth = $(this).data('comment-depth');

        $('.reply-container').remove();

        const replyContainer = $(`
            <div class="reply-container" style="margin-left:(${commentDepth} + 1) * 27px">
                <textarea id="reply-textarea-${commentId}" class="reply-textarea" placeholder="답글을 입력하세요"></textarea>
                    <input class="form-check-input" type="checkbox" value="" id="reply-secret">
                    <label class="form-check-label" for="reply-secret">
                        비밀 댓글
                    </label>
                <button class="submit-reply-btn btn-square-small" data-comment-id="${commentId}">등록</button>
            </div>
            `);

        repliesDiv.before(replyContainer);
    });

    $('#comments-list').on('click', '.submit-reply-btn', function () {
        const parentCommentId = $(this).data('comment-id');
        const replyTextarea = $(`#reply-textarea-${parentCommentId}`);
        const replyText = replyTextarea.val();

        if (replyText) {
            replyToComment(parentCommentId, replyText);
        }
    });

    function deleteComment(commentId) {
        $.ajax({
            url: '/algoy/comment/delete?commentId=' + commentId,
            method: 'POST',
            contentType: 'application/json',
            data: {},
            success: function (response) {
                alert("댓글 삭제에 성공했습니다.");
                location.href = postUrl;

            },
            error: function (error) {
                alert('댓글 삭제에 실패했습니다.');
                location.href = postUrl;
            }
        });
    }

    function acceptComment(commentId){
        $.ajax({
            url:'/algoy/comment/join?studyId=' + urlParams.get('studyId') + '&commentId=' + commentId,
            method: 'POST',
            success: function (response) {
                alert("스터디 가입을 수락했습니다.");
                location.href = postUrl;
            },
            error: function (error) {
                alert("스터디 가입에 실패했습니다.");
                location.href = postUrl;
            }
        })
    }

    function outComment(commentId){
        $.ajax({
            url:'/algoy/comment/out?studyId=' + urlParams.get('studyId') + '&commentId=' + commentId,
            method: 'POST',
            success: function (response) {
                alert("스터디에서 탈퇴시켰습니다..");
                location.href = postUrl;
            },
            error: function (error) {
                alert("스터디 탈퇴에 실패했습니다.");
                location.href = postUrl;
            }
        })
    }

    function editComment(commentId, newText) {
        $.ajax({
            url: '/algoy/comment/update?commentId=' + commentId,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                content: newText,
                createdAt: null,
                updatedAt: null,
                secret: $('input[id=edit-secret]').is(':checked')
            }),
            success: function (response) {
                alert("댓글 수정에 성공했습니다.");
                location.href = postUrl;
            },
            error: function (error) {
                alert('댓글 수정에 실패했습니다.');
                location.href = postUrl;
            }
        });
    }

    function replyToComment(parentCommentId, replyText) {
        $.ajax({
            url: '/algoy/comment/reply?studyId=' + urlParams.get('studyId') + '&commentId=' + parentCommentId,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                content: replyText,
                createdAt: null,
                updatedAt: null,
                secret: $('input[id=reply-secret]').is(':checked')
            }),
            success: function (response) {
                alert("댓글 입력에 성공했습니다.");
                location.href = postUrl;

            },
            error: function (error) {
                alert('댓글 입력에 실패했습니다.');
                location.href = postUrl;
            }
        });
    }

    $('#btn-update').click(function () {

        $.ajax({
            method: "GET",
            url: "/algoy/study/updateForm?postId=" + urlParams.get('postId'),
            success: function (response) {
                location.href = '/algoy/comment/updateForm?studyId=' + urlParams.get('studyId');
            },
            error: function (error) {
                alert('수정 페이지 조회에 실패했습니다.');
            }
        })
    })

    $('#btn-delete').click(function () {
        $.ajax({
            method: "POST",
            url: "/algoy/study/delete/" + urlParams.get('studyId'),
            success: function (response) {
                alert('페이지 삭제 완료');
                location.href = "/algoy/study/main"
            },
            error: function (error) {
                alert('삭제에 실패했습니다.');
            }
        })
    })

    $('#btn-edit').click(function(event) {
        event.preventDefault();
        $.ajax({
            url: "/algoy/study/edit-form?studyId=" + urlParams.get('studyId'),
            method: "GET",
            success: function (response) {
                location.href = "/algoy/study/edit-form?studyId=" + urlParams.get('studyId')
            },
            error: function(error) {
                alert('수정 페이지로 이동이 실패했습니다.');
            }
        })
    })
});

$('.search-button').click(function() {
    let keyword = $('.search-input').val(); // 검색어 입력받기
    if (keyword) {
        localStorage.setItem('searchKeyword', keyword); // 검색어를 localStorage에 저장
        window.location.href = '/algoy/study/main'; // 페이지 이동
    }
});
