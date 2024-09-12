package com.example.algoyweb;

import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.dto.chatting.MessageRequest;
import com.example.algoyweb.model.dto.comment.CommentDto;
import com.example.algoyweb.model.dto.planner.PlannerDto;
import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.dto.user.UserDto;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.model.entity.planner.Planner;
import com.example.algoyweb.model.entity.study.Study;
import com.example.algoyweb.model.entity.user.Role;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.repository.allen.SolvedACResponseRepository;
import com.example.algoyweb.repository.chatting.ChattingRepository;
import com.example.algoyweb.repository.chatting.ChattingRoomRepository;
import com.example.algoyweb.repository.planner.PlannerRepository;
import com.example.algoyweb.repository.study.CommentRepository;
import com.example.algoyweb.repository.study.ParticipantRepository;
import com.example.algoyweb.repository.study.StudyRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.example.algoyweb.util.user.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.algoyweb.model.entity.user.Role.ADMIN;
import static com.example.algoyweb.model.entity.user.Role.NORMAL;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SolvedACResponseRepository solvedACResponseRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private ChattingRepository chattingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlannerRepository plannerRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private WrongAnswerNoteRepository wrongAnswerNoteRepository;

    @BeforeEach
    void setUp() {
        studyRepository.deleteAll();
        userRepository.deleteAll();
        plannerRepository.deleteAll();
        commentRepository.deleteAll();
        wrongAnswerNoteRepository.deleteAll();
        participantRepository.deleteAll();
        chattingRepository.deleteAll();
        chattingRoomRepository.deleteAll();
    }

    private WebSocketStompClient settingWebSocket() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        return new WebSocketStompClient(sockJsClient);
    }

    // 0. 회원가입 및 유저 초기화 메서드
    private User signUpAndLogin(String i) throws Exception {
        mockMvc.perform(post("/algoy/sign")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser" + i)
                        .param("nickname", "testNickname" + i)
                        .param("email", "test" + i + "@example.com")
                        .param("password", "password" + i)
                        .param("solvedacUserName", "lenac115"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/algoy/login"));

        User savedUser = userRepository.findOptionalByEmail("test" + i + "@example.com").orElseThrow();
        assertThat(savedUser.getUsername()).isEqualTo("testuser" + i);
        assertThat(savedUser.getNickname()).isEqualTo("testNickname" + i);

        mockMvc.perform(formLogin("/algoy/login")
                        .user("email", "test" + i + "@example.com")
                        .password("password" + i))
                .andExpect(status().is3xxRedirection());

        return savedUser;
    }

    // 0. 스터디 생성 메서드
    private Study createStudy(User savedUser) throws Exception {

        StudyDto studyDto = StudyDto.builder()
                .title("Test Study")
                .content("This is a test study")
                .language("Java")
                .status(Study.Status.ING)
                .maxParticipant(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/algoy/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studyDto))
                        .with(user(new CustomUserDetails(savedUser))))
                .andExpect(status().isCreated());

        Study savedStudy = studyRepository.findByTitle("Test Study").orElseThrow();
        assertThat(savedStudy.getTitle()).isEqualTo("Test Study");
        assertThat(savedStudy.getLanguage()).isEqualTo("Java");

        return savedStudy;
    }

    /**
     * 테스트용 MessageRequest 객체를 생성합니다.
     */
    private MessageRequest createMessageRequest(String roomId, String content) throws Exception {
        MessageRequest request = new MessageRequest();
        Field roomIdField = MessageRequest.class.getDeclaredField("roomId");
        Field contentField = MessageRequest.class.getDeclaredField("content");
        roomIdField.setAccessible(true);
        contentField.setAccessible(true);
        roomIdField.set(request, roomId);
        contentField.set(request, content);
        return request;
    }



    @Test
    public void userManagementTest() throws Exception {
        // 1. 회원가입 요청
        mockMvc.perform(post("/algoy/sign")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("nickname", "testNickname")
                        .param("email", "test@example.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/algoy/login"));

        // 회원가입 후 유저가 DB에 저장되었는지 확인
        User savedUser = userRepository.findOptionalByEmail("test@example.com").orElseThrow();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getNickname()).isEqualTo("testNickname");

        // 2. 로그인 요청
        mockMvc.perform(formLogin("/algoy/login")
                        .user("email", "test@example.com")
                        .password("password"))
                .andExpect(status().is3xxRedirection());

        // 3. 유저 정보 조회 (마이페이지 - 닉네임 조회)
        CustomUserDetails savedUserDetails = new CustomUserDetails(savedUser);

        mockMvc.perform(get("/algoy/user/nickname")
                        .with(user(savedUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("testNickname"));

        // 4. 유저 정보 수정
        UserDto userDto = UserDto.builder()
                .solvedacUserName("lenac115")
                .email("test@example.com")
                .build();

        mockMvc.perform(post("/algoy/user/update")
                        .with(user(savedUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User information updated successfully."));

        // 5. 유저 정보가 업데이트되었는지 확인
        User updatedUser = userRepository.findOptionalByEmail("test@example.com").orElseThrow();
        assertThat(updatedUser.getSolvedacUserName()).isEqualTo("lenac115");
    }

    @Test
    public void studyManagementTest() throws Exception {
        // 로그인 후 CustomUserDetails 생성
        User savedUser = signUpAndLogin("1");
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // 1. 스터디 생성
        StudyDto studyDto = StudyDto.builder()
                .title("Test Study")
                .content("This is a test study")
                .language("Java")
                .status(Study.Status.ING)
                .maxParticipant(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        String studyJson = objectMapper.writeValueAsString(studyDto);

        MvcResult result = mockMvc.perform(post("/algoy/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studyJson)
                        .with(user(userDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Study"))
                .andReturn();

        StudyDto createdStudy = objectMapper.readValue(result.getResponse().getContentAsString(), StudyDto.class);

        // 2. 스터디 조회
        mockMvc.perform(get("/algoy/study/get/" + createdStudy.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Study"));

        // 3. 스터디 수정
        StudyDto updatedStudyDto = StudyDto.builder()
                .id(createdStudy.getId())
                .title("Updated Test Study")
                .content(createdStudy.getContent())
                .language(createdStudy.getLanguage())
                .status(createdStudy.getStatus())
                .maxParticipant(createdStudy.getMaxParticipant())
                .createdAt(createdStudy.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/algoy/study/update/" + createdStudy.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudyDto))
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Study"));

        // 4. 스터디 검색
        mockMvc.perform(get("/algoy/study/gets")
                        .param("page", "0")
                        .param("keyword", "Updated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Updated Test Study"));

        // 5. 스터디 삭제
        mockMvc.perform(post("/algoy/study/delete/" + createdStudy.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료"));
    }

    @Test
    public void commentManagementTest() throws Exception {
        // 로그인 후 CustomUserDetails 생성
        User savedUser = signUpAndLogin("1");
        Study savedStudy = createStudy(savedUser);
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // 1. 댓글 생성
        CommentDto commentDto = CommentDto.builder()
                .content("This is a test comment")
                .secret(false)
                .build();

        String commentJson = objectMapper.writeValueAsString(commentDto);

        MvcResult createResult = mockMvc.perform(post("/algoy/comment/non-reply")
                        .with(user(userDetails))
                        .param("studyId", savedStudy.getId().toString()) // 스터디 ID는 미리 세팅되어 있어야 함
                        .content(commentJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CommentDto createdComment = objectMapper.readValue(createResult.getResponse().getContentAsString(), CommentDto.class);

        // 2. 대댓글 생성
        CommentDto replyDto = CommentDto.builder()
                .content("This is a reply")
                .secret(false)
                .build();

        String replyJson = objectMapper.writeValueAsString(replyDto);

        MvcResult replyResult = mockMvc.perform(post("/algoy/comment/reply")
                        .with(user(userDetails))
                        .content(replyJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyId", savedStudy.getId().toString()) // 스터디 ID는 미리 세팅되어 있어야 함
                        .param("commentId", createdComment.getId().toString()))
                .andExpect(status().isCreated())
                .andReturn();

        CommentDto createdReply = objectMapper.readValue(replyResult.getResponse().getContentAsString(), CommentDto.class);

        // 3. 댓글 수정
        CommentDto updatedCommentDto = CommentDto.builder()
                .content("Updated comment")
                .secret(false)
                .build();

        String updatedCommentJson = objectMapper.writeValueAsString(updatedCommentDto);

        mockMvc.perform(post("/algoy/comment/update")
                        .with(user(userDetails))
                        .content(updatedCommentJson)
                        .param("commentId", createdComment.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 스터디 조인
        mockMvc.perform(post("/algoy/comment/join")
                        .with(user(userDetails))
                        .content(updatedCommentJson)
                        .param("commentId", createdComment.getId().toString())
                        .param("studyId", savedStudy.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 조인 확인
        MvcResult joinResult = mockMvc.perform(get("/algoy/comment/find-part")
                        .with(user(userDetails))
                        .param("commentId", createdComment.getId().toString())
                        .param("studyId", savedStudy.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Boolean convertJoin = objectMapper.readValue(joinResult.getResponse().getContentAsString(), Boolean.class);
        assertTrue(convertJoin);

        // 스터디 탈퇴
        mockMvc.perform(post("/algoy/comment/out")
                        .with(user(userDetails))
                        .content(updatedCommentJson)
                        .param("commentId", createdComment.getId().toString())
                        .param("studyId", savedStudy.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 탈퇴 확인
        MvcResult outResult = mockMvc.perform(get("/algoy/comment/find-part")
                        .with(user(userDetails))
                        .param("commentId", createdComment.getId().toString())
                        .param("studyId", savedStudy.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Boolean convertOut = objectMapper.readValue(outResult.getResponse().getContentAsString(), Boolean.class);
        assertTrue(convertOut);

        // 4. 댓글 삭제
        mockMvc.perform(post("/algoy/comment/delete")
                        .with(user(userDetails))
                        .param("commentId", createdComment.getId().toString()))
                .andExpect(status().isOk());
    }

    // 플래너 관리 테스트
    @Test
    public void plannerManagementTest() throws Exception {
        User savedUser = signUpAndLogin("1");
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // 1. 플래너 생성
        PlannerDto plannerDto = PlannerDto.builder()
                .title("Test Plan")
                .content("This is a test plan")
                .startAt(LocalDate.now())
                .endAt(LocalDate.now().plusDays(1))
                .link("http://example.com")
                .questionName("Sample Question")
                .status(Planner.Status.TODO)
                .siteName(Planner.SiteName.BOJ)
                .build();

        String plannerJson = objectMapper.writeValueAsString(plannerDto);
        String responsePlanner = mockMvc.perform(post("/algoy/planner/save")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(plannerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Plan"))
                .andReturn().getResponse().getContentAsString();

        PlannerDto createdPlanner = objectMapper.readValue(responsePlanner, PlannerDto.class);

        // 2. 플래너 조회
        mockMvc.perform(get("/algoy/planner/" + createdPlanner.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Plan"));

        // 3. 플래너 수정
        PlannerDto updatedPlannerDto = PlannerDto.builder()
                .id(createdPlanner.getId())
                .title("Updated Test Plan")
                .content(createdPlanner.getContent())
                .startAt(createdPlanner.getStartAt())
                .endAt(createdPlanner.getEndAt())
                .link(createdPlanner.getLink())
                .questionName(createdPlanner.getQuestionName())
                .status(createdPlanner.getStatus())
                .siteName(createdPlanner.getSiteName())
                .build();

        mockMvc.perform(post("/algoy/planner/edit/" + createdPlanner.getId())
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPlannerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Plan"));

        // 4. 플래너 검색
        mockMvc.perform(get("/algoy/planner/search")
                        .param("keyword", "Updated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Updated Test Plan"));

        // 5. 플래너 삭제
        mockMvc.perform(post("/algoy/planner/delete/" + createdPlanner.getId())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().string("삭제 완료"));

        // 6. 삭제 후 플래너 조회 시도
        mockMvc.perform(get("/algoy/planner/" + createdPlanner.getId())
                        .with(user(userDetails)))
                .andExpect(status().isNotFound());
    }

    // 오답노트 관리 테스트
    @Test
    public void wrongAnswerNoteManagementTest() throws Exception {
        User savedUser = signUpAndLogin("1");
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // 1. 오답노트 생성 및 저장
        WrongAnswerNote testNote = new WrongAnswerNote();
        testNote.setTitle("Lifecycle Test Note");
        testNote.setLink("https://example.com/lifecycle");
        testNote.setQuizSite("Lifecycle Quiz Site");
        testNote.setQuizType("Multiple Choice");
        testNote.setQuizLevel("Medium");
        testNote.setContent("Lifecycle test content.");
        testNote.setIsSolved(false);
        testNote.setCreatedAt(LocalDateTime.now());
        testNote.setUser(savedUser);
        wrongAnswerNoteRepository.save(testNote);

        // 2. 생성된 오답노트 조회 및 검증
        Optional<WrongAnswerNote> createdNote = wrongAnswerNoteRepository.findById(testNote.getId());
        assertThat(createdNote).isPresent();
        assertThat(createdNote.get().getTitle()).isEqualTo("Lifecycle Test Note");
        assertThat(createdNote.get().getUser().getEmail()).isEqualTo(savedUser.getEmail());

        // 3. 오답노트 수정
        createdNote.get().setTitle("Updated Lifecycle Test Note");
        createdNote.get().setIsSolved(true);
        wrongAnswerNoteRepository.save(createdNote.get());

        // 4. 수정된 오답노트 조회 및 검증
        Optional<WrongAnswerNote> updatedNote = wrongAnswerNoteRepository.findById(testNote.getId());
        assertThat(updatedNote).isPresent();
        assertThat(updatedNote.get().getTitle()).isEqualTo("Updated Lifecycle Test Note");
        assertThat(updatedNote.get().getIsSolved()).isTrue();

        // 5. 오답노트 삭제
        wrongAnswerNoteRepository.delete(updatedNote.get());

        // 6. 삭제된 오답노트가 존재하지 않는지 확인
        Optional<WrongAnswerNote> deletedNote = wrongAnswerNoteRepository.findById(testNote.getId());
        assertThat(deletedNote).isNotPresent();
    }

    @Test
    public void allenManagementTest() throws Exception {
        // 0. 회원가입 및 유저 초기화
        User savedUser = signUpAndLogin("1");
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // 1. `allen` API 호출
        MvcResult result = mockMvc.perform(get("/algoy/allen/solvedac")
                        .param("solvedacusername", savedUser.getSolvedacUserName())
                        .with(user(userDetails)))
                .andExpect(status().isOk())  // API 호출 성공 확인
                .andReturn(); // 결과를 저장

        // 2. 응답 내용 검증 (응답 내용이 빈 값이 아닌지 확인)
        String responseContent = result.getResponse().getContentAsString();

        // 응답이 빈 문자열이 아닌지 확인
        assertThat(responseContent).isNotBlank();
    }

    @Test
    public void chattingRoomManagementTest() throws Exception {
        // 0. 회원가입 및 유저 초기화
        User savedUser1 = signUpAndLogin("1");
        User savedUser2 = signUpAndLogin("2");
        User savedUser3 = signUpAndLogin("3");
        CustomUserDetails userDetailsOne = new CustomUserDetails(savedUser1);
        CustomUserDetails userDetailsTwo = new CustomUserDetails(savedUser2);
        CustomUserDetails userDetailsThree = new CustomUserDetails(savedUser3);

        // 2. 채팅방 생성
        String createRoomRequest = """
            {
                "name": "Test Room",
                "invitees": ["testNickname2"]
            }
            """;

        String createdRoomResponse = mockMvc.perform(post("/algoy/api/chat/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRoomRequest)
                        .with(user(userDetailsOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Room"))
                .andExpect(jsonPath("$.participants").value(Matchers.containsInAnyOrder(savedUser1.getUserId().intValue(), savedUser2.getUserId().intValue())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ChattingRoomDto createdRoom = objectMapper.readValue(createdRoomResponse, ChattingRoomDto.class);
        String roomId = createdRoom.getRoomId();

        // 3. 채팅방 참여
        mockMvc.perform(post("/algoy/api/chat/room/" + roomId + "/join")
                        .with(user(userDetailsThree)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(roomId))
                .andExpect(jsonPath("$.participants.length()").value(3));

        // SockJSClient 사용
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // 로그인 요청 및 세션 가져오기
        MvcResult result = mockMvc.perform(formLogin("/login")
                        .user("email", "test1@example.com")
                        .password("password1"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        // 세션 가져오기
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();

        // WebSocket 연결 시 세션 ID를 포함
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Cookie", "JSESSIONID=" + session.getId());

        // WebSocket URL 및 헤더를 사용한 연결 설정
        String url = "ws://15.165.12.111:8081/algoy/chat-websocket";

        // 해당 부분 200 반환 받으나 웹 소켓 업그레이드 에러를 해결하지 못함
        StompSession stompSession = stompClient.connectAsync(url, headers, new StompSessionHandlerAdapter() {}).get(20, TimeUnit.SECONDS);

        // 4. 메시지 전송
        MessageRequest messageRequest = createMessageRequest(roomId,"Hello, World!");
        stompSession.send("/chat/sendMessage", messageRequest);

        // 5. 채팅방 메시지 목록 조회
        mockMvc.perform(get("/algoy/api/chat/room/" + roomId + "/messages")
                        .with(user(userDetailsOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello, World!"))
                .andExpect(jsonPath("$[0].roomId").value(roomId));

        // 6. 채팅방 나가기
        mockMvc.perform(post("/algoy/api/chat/room/" + roomId + "/leave")
                        .with(user(userDetailsOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(roomId))
                .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    public void testAdminFunctions() throws Exception {
        // 1. 관리자 계정 생성
        User adminUser =  User.builder()
                .username("test User")
                .nickname("test nickname")
                .email("admin@example.com")
                .password("password")
                .role(ADMIN)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(adminUser);
        CustomUserDetails adminUserDetails = new CustomUserDetails(adminUser);

        // 2. 일반 사용자 계정 생성
        User testUser = User.builder()
                .username("test User")
                .nickname("test nickname")
                .email("test@example.com")
                .password("password")
                .role(NORMAL)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User testUser2 = User.builder()
                .username("test User 2")
                .nickname("test nickname 2")
                .email("test2@example.com")
                .password("password")
                .role(NORMAL)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(testUser);
        userRepository.save(testUser2);
        CustomUserDetails normalUserDetails = new CustomUserDetails(testUser);


        // 3. 관리자 로그인
        MvcResult result = mockMvc.perform(formLogin("/login")
                        .user("email", "admin@example.com")
                        .password("password"))
                .andExpect(status().is3xxRedirection())
                .andReturn();


        // 4. 관리자 페이지 접근 테스트
        mockMvc.perform(get("/algoy/admin")
                        .with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("user/admin"))
                .andExpect(model().attributeExists("users"));

        // 5. 사용자 관리자 승격 테스트
        mockMvc.perform(post("/algoy/admin/role-control")
                        .param("userId", testUser.getId().toString())
                        .param("action", "admin")
                        .with(user(adminUserDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/algoy/admin"));

        // 사용자 역할이 ADMIN으로 변경되었는지 확인
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(Role.ADMIN, updatedUser.getRole());

        // 6. 사용자 밴 테스트
        mockMvc.perform(post("/algoy/admin/role-control")
                        .param("userId", testUser2.getId().toString())
                        .param("action", "ban")
                        .param("banReason", "Violation of terms")
                        .with(user(adminUserDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/algoy/admin"));

        // 사용자 역할이 BANNED로 변경되었는지 확인
        updatedUser = userRepository.findById(testUser2.getId()).orElseThrow();
        assertEquals(Role.BANNED, updatedUser.getRole());

        // 7. 사용자 밴 해제 테스트
        mockMvc.perform(post("/algoy/admin/role-control")
                        .param("userId", testUser2.getId().toString())
                        .param("action", "lift")
                        .with(user(adminUserDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/algoy/admin"));

        // 사용자 역할이 NORMAL로 변경되었는지 확인
        updatedUser = userRepository.findById(testUser2.getId()).orElseThrow();
        assertEquals(Role.NORMAL, updatedUser.getRole());
    }

}