package com.example.algoyweb;

import com.example.algoyweb.model.dto.chatting.ChattingRoomDto;
import com.example.algoyweb.model.dto.chatting.MessageRequest;
import com.example.algoyweb.model.dto.study.StudyDto;
import com.example.algoyweb.model.dto.user.UserDto;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.example.algoyweb.model.entity.user.Role.ADMIN;
import static com.example.algoyweb.model.entity.user.Role.NORMAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private WebSocketStompClient settingWebSocket() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        return new WebSocketStompClient(sockJsClient);
    }

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
            .andExpect(jsonPath("$.participants", Matchers.hasSize(2)))
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
            .andExpect(jsonPath("$.participants", Matchers.hasSize(3)));

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