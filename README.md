# high-five: Algoy (AI가 추천해 주는 플래너 및 오답노트)

## 목차

[1. 프로젝트 소개](#1-프로젝트-소개)  
[2. 팀 편성](#2-팀-편성)  
[3. 개발 기간](#3-개발-기간)  
[4. 개발 환경](#4-개발-환경)  
[5. 프로젝트 구조](#5-프로젝트-구조)  
[6. 아키텍처](#6-아키텍처)  
[7. UI 설계](#7-UI-설계)  
[8. 기능 명세](#8-기능-명세)  
[9. API 명세](#9-API-명세)  
[10. ERD](#10-ERD)  
[11. 시연 영상](#11-시연-영상)  
[12. Trouble Shooting](#12-Trouble-Shooting)  
[13. 프로젝트 회고](#13-프로젝트-회고)  
[14. 기타](#14-기타)

[//]: # (기타에는 코딩 컨벤션, 노션 페이지 등 위키 링크)

## 1. 프로젝트 소개

이 프로젝트는 Alan API를 활용한 코딩 테스트 전용 플래너 오답노트 서비스입니다.  
여러 코딩 테스트 관리를 한 곳에서, 플래너와 오답노트를 같이 이용할 수 있습니다.  
또한, ESTsoft에서 제공하는 Alan AI를 통해 맞춤형 문제를 제공받을 수 있습니다.

### URL: http://15.165.12.111/algoy/home

관리자용 계정

- ID: admin@gmail.com
- PW: 123

테스트용 계정

- ID: test00@gamil.com
- PW: 123

## 2. 팀 편성

|                              지승우                              |                              김주영                              |                              김창섭                              |                              성창용                              |                              안유석                              |                              조아라                              |
|:-------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|:-------------------------------------------------------------:|
| <img src="assets/img/profile/1.png" width="100" height="100"> | <img src="assets/img/profile/2.png" width="100" height="100"> | <img src="assets/img/profile/3.png" width="100" height="100"> | <img src="assets/img/profile/4.png" width="100" height="100"> | <img src="assets/img/profile/5.png" width="100" height="100"> | <img src="assets/img/profile/6.png" width="100" height="100"> |
|                              팀장                               |                              팀원                               |                              팀원                               |                              팀원                               |                              팀원                               |                              팀원                               |
|                     챗봇 기능 구현<br/>채팅 기능 구현                     |        로그인 구현<br/>회원 정보 수정 구현<br/>회원 탈퇴 구현<br/>홈화면 구현         |                       플래너 구현<br/>스터디 구현                       |                            오답노트 구현                            |      회원가입 구현<br/>구글 로그인 구현<br/>비밀번호 찾기 구현<br/>관리자 화면 구현       |            AI 문제 추천 기능 구현<br/>CI/CD 배포 및 Nginx 설정             |

## 3. 개발 기간

프로젝트 일정: 08/21 (수) ~ 09/12 (목)  
프로젝트 발표: 09/13 (금)

## 4. 개발 환경

[Front_end]  
<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/thymeleaf-0e641c?style=for-the-badge&logo=thymeleaf&logoColor=white">  
[Back_end]  
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white">  
[DB]  
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/mongoDB-47A248?style=for-the-badge&logo=MongoDB&logoColor=white">  
[IDE]  
<img src="https://img.shields.io/badge/intellij idea-black?style=for-the-badge&logo=intellij idea&logoColor=white">  
[버전 관리]  
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">  
[협업 도구]  
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">  
[개발 환경 및 도구]  
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/docker-0091e2?style=for-the-badge&logo=docker&logoColor=white">  
[서버 및 배포]  
<img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white">
<img src="https://img.shields.io/badge/Nginx-6DB33F?style=for-the-badge&logo=Nginx&logoColor=white">
<img src="https://img.shields.io/badge/GitHub%20Actions-262525?style=for-the-badge&logo=GitHub%20Actions&logoColor=white">

## 5. 프로젝트 구조

```plaintext
├─main
│  ├─generated
│  ├─java
│  │  └─com
│  │      └─example
│  │          └─algoyweb
│  │              ├─config # 애플리케이션 설정 파일
│  │              │  └─chatting
│  │              ├─controller # 컨트롤러 클래스 (클라이언트 요청 처리)
│  │              │  ├─allen
│  │              │  ├─chatting
│  │              │  ├─planner
│  │              │  ├─study
│  │              │  ├─temp
│  │              │  ├─user
│  │              │  └─WrongAnswerNote
│  │              ├─exception # 예외 처리
│  │              │  ├─chatting
│  │              │  ├─errorcode
│  │              │  └─user
│  │              ├─model # 데이터 모델 정의
│  │              │  ├─dto # 데이터 전송 객체
│  │              │  │  ├─allen
│  │              │  │  ├─auth
│  │              │  │  ├─chatting
│  │              │  │  ├─comment
│  │              │  │  ├─planner
│  │              │  │  ├─study
│  │              │  │  ├─user
│  │              │  │  └─WrongAnswerNote
│  │              │  └─entity # 엔티티
│  │              │      ├─allen
│  │              │      ├─auth
│  │              │      ├─chatting
│  │              │      ├─planner
│  │              │      ├─study
│  │              │      ├─user
│  │              │      └─WrongAnswerNote
│  │              ├─repository # 레포지토리
│  │              │  ├─allen
│  │              │  ├─chatting
│  │              │  ├─planner
│  │              │  ├─study
│  │              │  ├─user
│  │              │  └─WrongAnswerNote
│  │              ├─service # 서비스 클래스 (비즈니스 로직 처리)
│  │              │  ├─allen
│  │              │  ├─auth
│  │              │  ├─chatting
│  │              │  ├─planner
│  │              │  ├─study
│  │              │  ├─user
│  │              │  └─WrongAnswerNote
│  │              └─util  # 유틸리티 클래스 (공통 기능 제공)
│  │                  ├─chatting
│  │                  ├─user
│  │                  └─WrongAnswerNote
│  └─resources
│      ├─static # 정적 파일 (CSS, JavaScript, 이미지)
│      │  ├─css
│      │  │  ├─allen
│      │  │  ├─fragments
│      │  │  ├─home
│      │  │  ├─mypage
│      │  │  ├─password
│      │  │  ├─planner
│      │  │  ├─signup
│      │  │  ├─study
│      │  │  ├─user
│      │  │  └─wronganswernote
│      │  ├─img
│      │  └─js
│      │      ├─fragments
│      │      ├─graph
│      │      ├─mypage
│      │      ├─planner
│      │      ├─signup
│      │      ├─study
│      │      ├─user
│      │      └─wronganswernote
│      ├─templates # 템플릿 파일 (HTML)
│      │  ├─fragments
│      │  ├─mypage
│      │  ├─password
│      │  ├─planner
│      │  ├─signup
│      │  ├─study
│      │  ├─temp
│      │  ├─user
│      │  └─wronganswernote
│      └─application.yml # 애플리케이션 설정 파일 (예: 데이터베이스, 서버 포트, 프로파일 설정 등)
```

## 6. 아키텍처

![architecture](assets/img/architecture.png)

## 7. UI 설계

[algoy-figma](https://www.figma.com/design/cFtdGffRUuFPeJqK6kcBUc/Algoy?node-id=0-1&node-type=canvas&t=jG8aeeZxixqGCodM-0)

|                                 홈 화면                                  |                                회원가입                                 |
|:---------------------------------------------------------------------:|:-------------------------------------------------------------------:|
|                  ![home](assets/img/figma/home.png)                   |               ![signup](assets/img/figma/signup.png)                |
|                             비밀번호 찾기: 본인확인                             |                          비밀번호 찾기: 비밀번호 재설정                          |
|               ![find_pw](assets/img/figma/find_pw1.png)               |              ![find_pw](assets/img/figma/find_pw2.png)              |
|                                  로그인                                  |                               구글 로그인                                |
|                 ![login](assets/img/figma/login.png)                  |         ![login_google](assets/img/figma/login_google.png)          |
|                               스터디 모집 목록                               |                              스터디 모집 작성                              |
|               ![study](assets/img/figma/study_list.png)               |             ![study](assets/img/figma/study_write.png)              |
|                              스터디 모집 상세보기                              |                              스터디 모집 수정                              |
|              ![study](assets/img/figma/study_detail.png)              |             ![study](assets/img/figma/study_update.png)             |
|                                플래너 목록                                 |                               플래너 작성                                |
|             ![planner](assets/img/figma/planner_list.png)             |           ![planner](assets/img/figma/planner_write.png)            |
|                                플래너 수정                                 |                               플래너 모달                                |
|            ![planner](assets/img/figma/planner_update.png)            |           ![planner](assets/img/figma/planner_modal.png)            |
|                                오답노트 목록                                |                               오답노트 작성                               |
|   ![wrong answer note](assets/img/figma/wrong_answer_note_list.png)   | ![wrong answer note](assets/img/figma/wrong_answer_note_write.png)  |
|                               오답노트 상세보기                               |                               오답노트 수정                               |
|  ![wrong answer note](assets/img/figma/wrong_answer_note_detail.png)  | ![wrong answer note](assets/img/figma/wrong_answer_note_update.png) |
|                              오답노트: 코드 추가                              |                                마이페이지                                |
| ![wrong answer note](assets/img/figma/wrong_answer_note_add_code.png) |              ![my page](assets/img/figma/my_page.png)               |
|                                회원정보 수정                                |                                회원 탈퇴                                |
|             ![user edit](assets/img/figma/user_edit.png)              |          ![user delete](assets/img/figma/user_delete.png)           |
|                                 계정 복구                                 |                               관리자 페이지                               |
|      ![account recovery](assets/img/figma/account_recovery.png)       |           ![admin page](assets/img/figma/admin_page.png)            |
|                            유저 정지: 정지 사유 입력                            |                                                                     |
|          ![user ban reason](assets/img/figma/ban_reason.png)          |                                                                     |

## 8. 기능 명세

NAME은 컨트롤러에서 만든 메서드명 작성

### chatting

| 🏷NAME                 | ⚙METHOD | 📎URL                                            | 📖DESCRIPTION |
|------------------------|---------|--------------------------------------------------|---------------|
| getRooms               | GET     | /algoy/apo/chat/rooms                            | 채팅방 목록 가져오기   |
| getRoomMessages        | GET     | /algoy/apo/chat/room/{roomId}/messages           | 채팅 내역 가져오기    |
| createRoom             | POST    | /algoy/apo/chat/room                             | 채팅방 생성        |
| joinRoom               | POST    | /algoy/apo/chat/room/{roomId}/join               | 채팅방 참여        |
| leaveRoom              | POST    | /algoy/apo/chat/room/{roomId}/leave              | 채팅방 나가기       |
| inviteToRoomByNickname | POST    | /algoy/apo/chat/room/{roomId}/invite-by-nickname | 채팅방에 초대       |

### chat bot

| 🏷NAME   | ⚙METHOD | 📎URL                 | 📖DESCRIPTION          |
|----------|---------|-----------------------|------------------------|
| solvedac | GET     | /algoy/allen/solvedac | solvedAC 기반으로 문제 추천 받기 |
| chatPage | GET     | /algoy/chatbot-demo   | 챗봇 페이지                 |

### wrong answer note

| 🏷NAME                      | ⚙METHOD | 📎URL                          | 📖DESCRIPTION |
|-----------------------------|---------|--------------------------------|---------------|
| getAllWrongAnswerNotes      | GET     | /algoy/commit                  | 오답 노트 조회      |
| getWrongAnswerNoteById      | GET     | /algoy/commit/{id}             | 오답 노트 상세보기    |
| createWrongAnswerNoteForm   | GET     | /algoy/commit/create           | 오답 노트 작성 폼    |
| createWrongAnswerNote       | POST    | /algoy/commit/create           | 오답 노트 작성하기    |
| editWrongAnswerNoteForm     | GET     | /algoy/commit/{id}/edit        | 오답 노트 수정 폼    |
| updateWrongAnswerNote       | POST    | /algoy/commit/{id}/edit        | 오답 노트 수정하기    |
| deleteWrongAnswerNote       | POST    | /api/algoy/commit/{id}         | 오답 노트 삭제하기    |
| getCodesByWrongAnswerNoteId | GET     | /api/codes/{wrongAnswerNoteId} | 오답 노트 코드 목록   |
| saveCode                    | POST    | /api/codes                     | 오답 노트 코드 작성하기 |
| updateCode                  | PUT     | /api/codes/{codeId}            | 오답 노트 코드 수정하기 |
| deleteCode                  | DELETE  | /api/codes/{codeId}            | 오답 노트 코드 삭제하기 |

### planner

| 🏷NAME      | ⚙METHOD | 📎URL                      | 📖DESCRIPTION |
|-------------|---------|----------------------------|---------------|
| getCalender | GET     | /algoy/planner             | 캘린더 월 별 조회    |
| getPlan     | GET     | /algoy/planner/{id}        | 플랜 상세 보기      |
| saveForm    | GET     | /algoy/planner/save-form   | 플랜 생성 폼       |
| savePlan    | POST    | /algoy/planner/save        | 플랜 생성         |
| editForm    | GET     | /algoy/planner/edit-form   | 플랜 수정 폼       |
| editPlan    | POST    | /algoy/planner/edit/{id}   | 플랜 수정         |
| deletePlan  | POST    | /algoy/planner/delete/{id} | 플랜 삭제         |
| search      | GET     | /algoy/planner/search      | 플랜 검색         |
| viewMain    | GET     | /algoy/planner/main        | 플래너 메인 페이지    |
| getPlanner  | GET     | /algoy/planner/get/plans   | 유저의 전체 플래너 조회 |

### study

| 🏷NAME       | ⚙METHOD | 📎URL                    | 📖DESCRIPTION |
|--------------|---------|--------------------------|---------------|
| getStudyList | GET     | /algoy/study/gets        | 게시물 목록 조회     |
| getStudy     | GET     | /algoy/study/get/{id}    | 게시글 조회        |
| getNew       | GET     | /algoy/study/new-form    | 게시글 작성 폼      |
| createStudy  | POST    | /algoy/study/new         | 게시글 작성        |
| getEdit      | GET     | /algoy/study/edit-form   | 게시글 수정폼 조회    |
| updateStudy  | POST    | /algoy/study/update/{id} | 게시물 수정        |
| deleteStudy  | POST    | /algoy/study/delete/{id} | 게시물 삭제        |

### comment

| 🏷NAME         | ⚙METHOD | 📎URL                 | 📖DESCRIPTION |
|----------------|---------|-----------------------|---------------|
| getComments    | GET     | /algoy/comment/gets   | 댓글 조회         |
| createNonReply | POST    | /algoy/comment/reply  | 댓글 작성         |
| updateComment  | POST    | /algoy/comment/update | 댓글 수정         |
| deleteComment  | POST    | /algoy/comment/delete | 댓글 삭제         |

### mypage

| 🏷NAME             | ⚙METHOD | 📎URL                | 📖DESCRIPTION |
|--------------------|---------|----------------------|---------------|
|                    | GET     | /algoy/user          | 마이페이지         |
| editForm           | GET     | /algoy/user/edit     | 회원 정보 수정 폼    |
| updateUser         | POST    | /algoy/user/update   | 회원 정보 수정      |
| deleteForm         | GET     | /algoy/user/delete   | 회원 탈퇴 폼       |
| deleteRequest      | POST    | /algoy/user/delete   | 회원 탈퇴         |
| getUserNickname    | GET     | /algoy/user/nickname | 닉네임 표시        |
| restoreAccountPage | GET     | /algoy/user/restore  | 계정 복구         |

### user

| 🏷NAME                 | ⚙METHOD | 📎URL                           | 📖DESCRIPTION |
|------------------------|---------|---------------------------------|---------------|
| showSignUpForm         | GET     | /algoy/sign                     | 회원 가입 폼       |
| signUp                 | POST    | /algoy/sign                     | 회원 가입         |
| checkEmailDuplicate    | POST    | /algoy/check-email-duplicate    | 이메일 중복 확인     |
| checkNicknameDuplicate | POST    | /algoy/check-nickname-duplicate | 닉네임 중복 확인     |
| showLoginForm          | GET     | /algoy/login                    | 로그인 폼         |
| showFindPasswordPage   | GET     | /algoy/find-password            | 비밀번호 찾기 폼     |
| findPassword           | POST    | /algoy/find-password            | 비밀번호 찾기       |
| setPasswordPage        | GET     | /algoy/set-password             | 비밀번호 재설정 폼    |
| setPassword            | POST    | /algoy/set-password             | 비밀번호 재설정      |
| showAdminPage          | GET     | /algoy/admin                    | 관리자 페이지 폼     |
| changeUserRole         | POST    | /algoy/admin/role-control       | 유저 권한 변경      |

## 9. API 명세

| 카테고리      | 요구사항명             | Method | url                                              | 비고          |
|-----------|-------------------|--------|--------------------------------------------------|-------------|
| HOME      | 홈 화면              | GET    | /algoy/home                                      | 홈 화면        |
| QUIZ NOTE | 오답 노트 조회          | GET    | /algoy/commit                                    |             |
|           | 오답 노트 상세보기        | GET    | /algoy/commit/{id}                               |             |
|           | 오답 노트 작성 폼        | GET    | /algoy/commit/create                             |             |
|           | 오답 노트 작성하기        | POST   | /algoy/commit/create                             |             |
|           | 오답 노트 수정 폼        | GET    | /algoy/commit/{id}/edit                          |             |
|           | 오답 노트 수정하기        | POST   | /algoy/commit/{id}/edit                          |             |
|           | 오답 노트 삭제하기        | POST   | /api/algoy/commit/{id}                           |             |
|           | 오답 노트 코드 목록       | GET    | /api/codes/{wrongAnswerNoteId}                   |             |
|           | 오답 노트 코드 작성하기     | POST   | /api/codes                                       |             |
|           | 오답 노트 코드 수정하기     | PUT    | /api/codes/{codeId}                              |             |
|           | 오답 노트 코드 삭제하기     | DELETE | /api/codes/{codeId}                              |             |
| PLANNER   | 캘린더 월 별 조회        | GET    | /algoy/planner                                   |             |
|           | 플래너 메인 페이지        | GET    | /algoy/planner/main                              |             |
|           | 투두리스트 검색          | GET    | /algoy/planner/search                            |             |
|           | 유저의 투두리스트 조회      | GET    | /algoy/planner/get/plans                         |             |
|           | 투두리스트 상세보기        | GET    | /algoy/planner/{id}                              |             |
|           | 투두리스트 생성폼         | GET    | /algoy/planner/save-form                         |             |
|           | 투두리스트 생성          | POST   | /algoy/planner/save                              |             |
|           | 투두리스트 수정 폼        | GET    | /algoy/planner/edit-form                         |             |
|           | 투두리스트 수정          | POST   | /algoy/planner/edit/{id}                         |             |
|           | 투두리스트 삭제          | POST   | /algoy/planner/delete/{id}                       |             |
| MY PAGE   | 마이 페이지            | GET    | /algoy/user                                      |             |
|           | 회원 정보 수정 폼        | GET    | /algoy/user/edit                                 |             |
|           | 회원 정보 수정          | POST   | /algoy/user/update                               |             |
|           | 회원 탈퇴 폼           | GET    | /algoy/user/delete                               |             |
|           | 회원 탈퇴             | POST   | /algoy/user/delete                               | 소프트 딜리트     |
|           | 닉네임 표시            | GET    | /algoy/user/nickname                             |             |
|           | 계정 복구             | GET    | /algoy/user/resotre                              |             |
| USER      | 로그인 폼             | GET    | /algoy/algoy/login                               |             |
|           | 로그인               | POST   | /algoy/login                                     |             |
|           | 로그아웃              | GET    | /algoy/logout                                    |             |
|           | 회원 가입 폼           | GET    | /algoy/sign                                      |             |
|           | 회원 가입             | POST   | /algoy/sign                                      |             |
|           | e-mail 중복 확인      | POST   | /algoy/check-email-duplicate                     |             |
|           | 닉네임 중복 확인         | POST   | /algoy/check-nickname-duplicate                  |             |
|           | 비밀번호 찾기 폼         | GET    | /algoy/find-password                             |             |
|           | 비밀번호 찾기 요청        | POST   | /algoy/find-password                             |             |
|           | 비밀번호 재설정 폼        | GET    | /algoy/set-password                              |             |
|           | 비밀번호 재설정 요청       | POST   | /algoy/set-password                              |             |
|           | 관리자 페이지           | GET    | /algoy/admin                                     |             |
|           | 유저 권한 관리          | POST   | /algoy/admin/role-control                        | 관리자, 일반, 정지 |
| CHATTING  | 채팅방 목록 가져오기       | GET    | /algoy/apo/chat/rooms                            |             |
|           | 채팅 내역 가져오기        | GET    | /algoy/apo/chat/room/{roomId}/messages           |             |
|           | 채팅방 생성            | POST   | /algoy/apo/chat/room                             |             |
|           | 채팅방 참여            | POST   | /algoy/apo/chat/room/{roomId}/join               |             |
|           | 채팅방 나가기           | POST   | /algoy/apo/chat/room/{roomId}/leave              |             |
|           | 채팅방에 초대           | POST   | /algoy/apo/chat/room/{roomId}/invite-by-nickname |             |
| CHAT BOT  | solvedAC 기반 문제 추천 | GET    | /algoy/allen/solvedac                            |             |
|           | 챗봇 페이지            | GET    | /algoy/chatbot-demo                              |             |

## 10. ERD

[erd-cloud](https://www.erdcloud.com/d/n7JCsLE4y8JYZDBs2)
![algoy erd](assets/img/erd.png)

## 11. 시연 영상
![시연 GIF](assets/gif/demo.gif)

## 12. Trouble Shooting

### 김주영

로그인 트러블 슈팅

1. 페이지 무한 리디렉션
    - "리디렉션한 횟수가 너무 많습니다"라는 오류 메시지와 함께 로그인 페이지가 제대로 표시되지 않는 문제가 발생했습니다.
    - 이 문제는 Spring Security가 자동으로 POST 요청을 처리하기 때문에, 별도로 ```@PostMapping```을 Controller에 추가할 필요가 없다는 점을 간과하여 발생했습니다. 이를
      해결하기 위해 ```@PostMapping``` 어노테이션을 제거했습니다.
2. 화이트라벨 오류 발생
    - 설정 파일에서 지정된 로그인 URL이 잘못 입력되어 발생한 오류였습니다. Controller에 올바르게 매핑된 URL을 입력하여 문제를 해결했습니다.
3. Invalid email or password
    - 로그인 실패 시 "Invalid email or password" 메시지가 출력되도록 설정했으나, 문법적 오류는 발견되지 않았습니다.
    - 이 과정에서, Spring Security의 UserDetailsService가 이메일로 사용자 객체를 찾고, 비밀번호를 비교하여 URL을 반환하는 구조를 이해했습니다.
        - email을 입력 받으면 DetailService에서 객체를 찾고 객체를 Spring Security가 읽고 비교해서 url을 반환하는 구조라고 생각했다.
        - email로 객체를 찾았지만 password를 비교하는 로직이 없다고 생각하여 추가해야하나? 생각했지만, Spring Security는 자동으로 비밀번호 비교를 처리하기 때문에, 추가적인 로직이
          필요 없다는 것을 확인했습니다.
        - security관련 Debug를 보고 싶어서 yml 파일에 security: DEBUG를 추가하여 디버깅을 진행했으며, "Failed to find user"라는 로그가 출력되었습니다.
        - password가 security에 의해 암호화된 채 mysql에 저장 되도록 설정하고, 데이터를 읽을 때도 암호화하여 읽으므로 local MySQL에 저장된 password를
          BCryptPasswordEncoder 형식으로 바꾸어 저장했습니다.
        - 여전히 실행이 안되어서 다른 방향을 찾다가, 로그인 폼의 필드 이름이 Spring Security의 기본 요구 사항과 일치하는지!  확인을 해보았다.
        - 작성한 로그인 폼이 <input type=”email” name=”email” placeholder=”Email”> name이 email로 되어있던 것. name을 username으로 변경해주니
          로그인 문제를 해결할 수 있었습니다.

항상 오타에 주의하자

1. 캘린더 API를 로드하는 과정에서 302 및 404 오류가 발생했습니다.
    - 302 오류는 config에서 permitAll을 추가해 보았으나, 필요 없는 설정이었습니다.
2. 404 에러
    - 둘다 외부 파일로 분리해두었는데, 이렇게 써놨던 것… ```<script *th:src*="@{/calendar.global.js}"></script>``` ← js 디렉토리 설정을 안함(그래서 url
      주소로 인식한 것 같다.) ```<script *th:src*="@{/js/calendar/add-events.js}"></script>``` ← event로 해놓고 events라고 작성.
    - 올바르게 수정하니 ```<script *th:src*="@{/js/calendar/calendar.global.js}"></script>```
      ```<script *th:src*="@{/js/calendar/add-event.js}"></script>``` API를 무사히 가져올 수 있었습니다.

로그인시 탈퇴 유저인지 확인 후 복구 페이지로 리다이렉트

1. 복구를 물어보는 html에서는 post method를 사용하여 쉽게 이동이 가능했는데, 로그인 시 /login으로 이동하며 500에러가 발생했다.
    - authorize처럼 html에서 isDeleted를 처리할 수 있지 않을까? 했지만 오류가계속 발생했고 좋은 방법이 아닌 것 같았다.
    - 해결 방법을 찾아 보다가 로그인 관련은 security에서 처리가 되므로 Handler가 필요하다는 글을 보았다.
2. Handler를 작성하고 config에 주입하였으나 순환참조 오류가 발생했다.
    - 기존에 어노테이션을 통해 생성자 주입을 했었는데 이를 해결하기 위해 @Lazy 어노테이션을 사용하여 순환 참조 문제를 해결하였습니다.
        - 이 방법이 최선인지에 대한 의문은 남습니다.

탈퇴 요청 시 로그아웃이 안되는 현상

- 디버그 로그를 보니까 remeber-me 설정이 되어 있다는 로그가 있었다.
- 아무리 생각해도 순서가 반대인 것 같았다. 로그아웃이 되지 않아 remember-me가 유지되는 것 같았다.
- 검색해보니 탈퇴 처리 후 SecurityContextHolder.clearContext(); 이 코드를 꼭 작성해주어야 한다고 한다.
- controller, service 둘 다 추가해보았으나 여전히 로그아웃이 되지 않았다.
- 검색해보니 service에서 쿠키를 명시적으로 삭제하는 방법도 있다고 하여 이 방법을 사용해 보았다.
- 결과적으로 잘 처리 되었으나, 이 방법 역시 좋은 해결 방법인지는 모르겠어서 아쉬운 부분이다.

로그인과 회원가입 병합

1. 회원가입 에러 발생
    - 유석님이 작성한 코드와 제가 작성한 코드를 병합하는 과정에서 회원가입이 되지 않는 에러가 발생했습니다. 초기에는 토큰 변경을 시도했지만, 보안이 아직 중요하지 않은 상태였기 때문에 토큰을 사용하지 않는
      방향으로
      설정을 조정했습니다. 이 과정에서도 별다른 효과가 없었습니다.
2. 순환 참조 문제
    - 로그인 시 ```UserService```와 ```Config``` 파일 간의 순환 참조 문제로 인해 ```UserDetailsService``` 클래스를 별도로 작성하였습니다. 하지만,
      ```UserDetailsService```는 Spring
      Security에서 제공하는 인터페이스로, 이를 구현하기 위해 ```UserService```에서 ```UserDetailsService```를 구현하도록 수정하였습니다. 이 수정에도 불구하고 회원가입은
      여전히 실패했습니다.

3. 회원가입 시 비밀번호 오류
    - 회원가입 시 "Cannot invoke 'String.equals(Object)' because the return value of 'UserDto.getPassword()' is null"이라는 오류가
      발생하였습니다. ```UserDto```의 ```getPassword()``` 메서드가 ```null```을 반환하는 문제를 해결하기 위해 비밀번호가 ```null```인지 확인하는 로직을 추가했으나,
      여전히 비밀번호가 ```null```로 반환되었습니다.
      ```User```
      엔티티의 ```password``` 컬럼이 ```nullable=true```로 설정되어 있어도 객체 생성이 실패하는 문제가 발생했습니다. 이로 인해 객체 생성에 문제가 있는지 의심하였으나, 아무런 에러나
      오류 메시지가
      출력되지 않아
      디버깅에 어려움을 겪었습니다.
    - 필드 이름 충돌 확인
      로그인 폼의 ```name="username"```과 ```User``` 엔티티의 ```username``` 컬럼 간의 충돌을 의심하여, ```username```을 ```firstname```으로
      변경하였으나, 별다른 효과는 없었습니다.
    - 최종 해결책: 새로운 프로젝트와 코드 비교
      결국, 개발 브랜치를 다시 ```version control```을 통해 받아와 새 프로젝트를 열고, 제가 작성한 로그인 코드를 기존 코드와 비교하여 붙여보기로 했습니다. 이 과정에서 회원가입은 성공적으로
      작동했지만
      로그인에는 여전히 문제가 발생했습니다. 이를 해결하기 위해 ```Config``` 파일과 ```UserService``` 파일을 통합하고, 유석님과 협력하여 ```PasswordEncoder```를
      ```UserService```에서 생성자로
      주입받는
      방식으로 변경하였습니다. 또한, ```UserDetailsService``` 인터페이스에 대한 설명을 통해 로직을 명확히 정리하였습니다. 로직을 차근차근 검토한 결과, User에서 null 오류가
      해결되었습니다.

협업 및 팀 프로젝트 경험:

- 이 과정에서 유석님과의 긴밀한 협업이 큰 도움이 되었습니다. 서로의 코드와 설정을 면밀히 검토하고 조율하는 과정에서 문제를 명확히 파악하고 해결하는 데 유용한 통찰을 얻었습니다. 협업을 통해 문제를
  공유하고 해결 방안을 모색하는 경험은 팀 프로젝트에서의 중요성을 새삼 느끼게 했습니다.

### 조아라

![](assets/img/troubleshooting/1.png)

기존 기능

- AlAN AI에게 문제 추천 받는 기능이 비회원 접근도 가능했다.
- 기존 로직은 사용자가 백준에서 문제를 풀때만 추천문제 갱신이 되어 서비스 측면에서 효율성이 낮다고 생각했다.

![](assets/img/troubleshooting/2.png)

추가 개발

- 로그인 성공 시 Handler가 작동하며 자동으로 ALAN API 호출을 하여 다수의 문제를 수신한다.
- DB에 리스트로 저장하여 홈 화면 접속시 랜덤으로 한문제씩 화면에 보여준다.

트러블 슈팅

- Handler 충돌
- 기존 코드에서 개발 하는 중에 config 파일에 등록된 Handler에서 충돌 발생.
- 회원 탈퇴 기능을 구현하던 팀원님의 코드와 충돌이 났으나, 해당 기능을 알지 못해 새로운 handler를 적용하였다.
- 하지만 그 후에도 로그인 시 ALAN API를 받아오지 못하는 오류가 발생
- 추후 팀원들과 상의 후 Handler의 중복성 문제를 파악하고 코드를 합치면서 해당 문제 해결

깨닫게 된 점

- 코드를 통합할 시 코드 충돌날 수 있는 확률이 높기 때문에 각자 기능 개발할때 코드리뷰를 실행하며 해당 서비스 기능의 전체적인 로직을 파악하는 것이 중요하다는 사실을 깨달았습니다
- 코드 통합 시 발생하는 충돌을 예방하기 위해 본인 코드에 대한 명확한 전달과 소통방식이 중요하다고 생각합니다.

---

문제  
상황 : web-spring과 ai-spring을 각각 Github Actions로 CI/CD 자동 배포를 구현했다.  
문제 : 두 개의 애플리케이션이 하나의 pid로 배포 진행이 되고 있었다. (편집됨)

![](assets/img/troubleshooting/3.png)

첫 설정을 pid가 진행되고 있으면 삭제하고 새로운 pid 생성하게 코드를 짰는데 pid 이름 설정을 따로 하지 않아서 하나의 pid로 배포가 운영되고 있었다.
이렇게 된다면 각각의 애플리케이션 운영을 동시에 할 수 없다.

해결

- 각각 yml 파일에 pid이름을 따로 설정했다.
    - ai-application → ai-pid
    - web-application → web-pid

![](assets/img/troubleshooting/4.png)  
![](assets/img/troubleshooting/5.png)

---

상황
데모로 연동이 되었으니 Nginx 문제는 해결되었다고 생각하고 ai 레포와 web레포를 배포 후 Nginx를 통해 통신해 보았다.

- ai 레포는 접근 성공
- web 레포는 /login으로 자동 이동되면서 다시 에러가 발생하였다. (```/web/planner/``` 로 이동했는데)

![](assets/img/troubleshooting/6.png)

문제
원격저장소에 올라온 코드를 보니 Security 의존성이 추가되어 사용되고 있었다. 로그인 기능이 구현되지 않은 현재 상황에서 자동으로 로그인으로 페이지가 이동한다고 했다.
문제는 /web으로 접근해야지 Nginx에서 8081을 찾아갈 수 있는데 자동으로 /login을 호출하기 때문에 config 파일을 구현해야하는 상황이었다.
문제 해결방법으로 web 쪽 url은 root(/)를 사용했을때 들어가는 해결책을 팀원들과 회의를 통해 찾았다

![](assets/img/troubleshooting/7.png)

### 지승우

챗봇 마지막 응답을 받아오지 못함

- 이 문제는 SSE 방식으로 Alan AI API에게 응답을 받아올 경우 마지막 응답까지 계속해서 일정 주기 단위로 문장이 끊겨서 전송되고 마지막 응답이 들어오는 JSON 형태로 값을 전달 받기 때문에 발생하는
  문제입니다.
- 예를 들면, '안녕'이라는 응답이 {'type': 'continue', 'data': {'content': '안'}} {'type': 'continue', 'data': {'content': '녕'}}
  {'type': 'complete', 'data': {'content: '안녕'}}으로 들어옵니다.
- 해결 방식: DB에 모든 메시지 정보 저장하면서 클라이언트 단에서만 마지막 응답을 띄우는 방식 -> 파싱한 마지막 메시지만 DB에 담고 그 내용을 클라이언트에 띄움.

챗봇 XSS 취약점 발견

- 이 문제는 챗봇 출력에 마크 다운을 적용한 뒤, HTML 코드를 입력해보고 화면에 창이 뜨는 걸 확인하여 XSS에 취약점이 있다는 문제입니다.
- 해결 방식:
    - 클라이언트 - HTML에 헤더에 DOMPurify 라이브러리 추가 및 Java Script에 살균 코드 추가
    - 서버 - Jsoup과 apache.commons.text의 StringEscapeUtils를 이용하여 입력 살균 역할을 하는 InputSanitizer 클래스 추가 후 컨트롤러에 입력 살균 코드 추가

배포 환경에서 포트 간 통신이 안됨

- 로컬에서는 잘 되던 포트 간 SSE 통신이 배포 환경에서는 되지 않는 문제를 발견하였습니다. (500 에러)
- 해결 방식:
    - WebConfig 문제일 것으로 추정한 뒤 localhost:${Port}로 설정하였습니다.
    - 그러나 해결이 안 되어서 NginX나 EC2 방화벽 문제인 줄 알고 서버 소유자 분을 통해 해당 설정을 확인 하였습니다. -> 해결 X
    - 다시 코드 문제로 추측하고, 이번엔 클라이언트에 backendUrl을 ```http://${IP}:${서버 Port}```로 설정 하였습니다. -> (403 에러)
    - 상태 코드가 500에서 403으로 바뀐 것을 확인하고, WebConfig의 allowedOrigins를 ```http://${IP}```로 수정하여 문제를 최종적으로 해결 하였습니다.

새로고침 혹은 페이지 전환 시 챗봇에서 이전 메시지 내용을 출력하지 못하는 문제와 채팅에서 사용자 정보를 기억하지 못하는 문제 발견

- 해결 방식:
    - 이 문제들은 공통적으로 해당 세션에서 어떤 정보를 기억하지 못한다는 원인을 가지고 있습니다.
    - 따라서, 로컬 스토리지나 세션 스토리지 방식을 사용할 수 있었는데, 로컬 스토리지는 브라우저에서 영구적으로 데이터를 가지고 있는 것이므로 해당 상황에 적합하지 않았습니다.
    - 세션 스토리지 방식을 채택하여 이 문제를 해결하였습니다.

## 13. 프로젝트 회고

### 김주영

### 김창섭

### 성창용

### 안유석

### 조아라

### 지승우

## 14. 기타
[Code Convention](https://github.com/high-five-ormi/Algoy-Web/wiki/Code-Convention)  
[Git Branch Convention](https://github.com/high-five-ormi/Algoy-Web/wiki/Git-Branch-Convention)  
[Git Commit Convention](https://github.com/high-five-ormi/Algoy-Web/wiki/Git-Commit-Convention)