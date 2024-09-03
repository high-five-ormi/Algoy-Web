$(document).ready(function() {
    $(".updateUserBtn").click(function () {
        let password = $("#password").val();
        let confirmPassword = $("#confirm-password").val();

        // 비밀번호와 닉네임을 업데이트하기 위한 객체 생성
        let userDto = {
            email: $("#email").val(),
            nickname: $("#nickname").val(),
            name: $("#username").val(),
            // 비밀번호가 입력된 경우에만 설정, 아니면 null
            password: password.length > 0 ? password : undefined
        };

        console.log("User DTO:", userDto);

        // 비밀번호가 입력된 경우, 비밀번호 확인 일치 여부 검사
        if (password.length > 0 && password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
            return;
        }

        // AJAX POST 요청
        $.ajax({
            type: "POST",
            url: "/algoy/user/update",
            contentType: "application/json",
            data: JSON.stringify(userDto),
            success: function (response) {
                alert("회원 정보가 성공적으로 수정되었습니다.");
                window.location.href = "/algoy/home";
            },
            error: function (xhr) {
                alert("오류 발생: " + xhr.responseText);
            }
        });
    });
});

// 비밀번호 일치 여부 확인 함수
function checkPasswordMatch() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const messageElement = document.getElementById('password-message');

    if (password === confirmPassword) {
        messageElement.textContent = '비밀번호가 일치합니다.';
        messageElement.style.color = 'green';
    } else {
        messageElement.textContent = '비밀번호가 일치하지 않습니다!';
        messageElement.style.color = 'red';
    }
}

function checkDuplicateNickname(event) {
    event.preventDefault(); // 폼의 기본 제출 동작 막기

    const nickname = document.getElementById("nickname").value; // 사용자가 입력한 값
    const nicknameMessage = document.getElementById("nickname-message"); // 메시지를 표시할 요소

    // 닉네임 입력란에 값이 있는지 확인
    if (nickname) { // 닉네임 입력란에 값이 있으면
        // fetch API를 사용하여 서버에 GET 요청 보내기
        fetch(`/algoy/check-nickname-duplicate?nickname=${encodeURIComponent(nickname)}`)
            .then(response => {
                // 서버 응답이 성공적인지 확인 (HTTP 상태 코드가 200번대인지 확인)
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.json(); // 응답 본문을 JSON으로 파싱하여 반환
            })
            .then(data => {
                // 서버에서 반환된 데이터에 'exists' 필드가 있는지 확인하여 닉네임 중복 여부 판단
                if (data.exists) { // 입력한 닉네임이 이미 존재하는 경우
                    nicknameMessage.textContent = "이미 사용 중인 닉네임입니다!";
                    nicknameMessage.style.color = "red";
                } else { // 입력한 닉네임이 이미 존재하지 않는(사용 가능한) 경우
                    nicknameMessage.textContent = "사용 가능한 닉네임입니다!";
                    nicknameMessage.style.color = "green";
                }
            })
            .catch(error => { // fetch 요청 또는 JSON 파싱 중 오류가 발생한 경우
                console.error("Error checking email:", error);
                nicknameMessage.textContent = "닉네임 확인 중 오류가 발생했습니다.";
                nicknameMessage.style.color = "red";
            });
    } else { // 입력란이 비어있으면
        nicknameMessage.textContent = "사용하실 닉네임을 입력하세요.";
        nicknameMessage.style.color = "red";
    }

}
