$(document).ready(function () {
    $('#deleteUserBtn').on('click', function () {
        if (confirm("정말로 회원 탈퇴를 하시겠습니까?")) {
            $.ajax({
                type: "POST",
                url: "/algoy/user/delete", // 탈퇴 요청을 처리할 서버 URL
                success: function (response) {
                    alert("회원 탈퇴 요청이 완료되었습니다.");
                    window.location.href = "/algoy/home"; // 탈퇴 후 리다이렉션할 URL
                },
                error: function (xhr) {
                    alert("탈퇴 요청 처리 중 오류가 발생했습니다.");
                }
            });
        }
    });
});