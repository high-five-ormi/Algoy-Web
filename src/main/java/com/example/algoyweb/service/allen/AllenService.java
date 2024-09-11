package com.example.algoyweb.service.allen;

import com.example.algoyweb.model.dto.allen.SolvedACJsonResponse;
import com.example.algoyweb.model.entity.allen.SolvedACResponseEntity;
import com.example.algoyweb.model.entity.user.User;
import com.example.algoyweb.repository.allen.SolvedACResponseRepository;
import com.example.algoyweb.repository.user.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class AllenService {

    //사용자가 푼 문제 중 문제 수준이 높은 상위 100 문제를 가져오는 api
    @Value("${solvedac.url}")
    String solvedAcApi;

    @Value("${askallen.url}")
    String askAllenUrl;

    private final HttpURLConnectionEx httpEx;
    private final SolvedACResponseRepository solvedACResponseRepository;
    private final UserRepository userRepository;

    @Autowired
    public AllenService(HttpURLConnectionEx httpEx, SolvedACResponseRepository solvedACResponseRepository, UserRepository userRepository) {
        this.httpEx = httpEx;
        this.solvedACResponseRepository = solvedACResponseRepository;
        this.userRepository = userRepository;

    }

    /**
     * home 화면에 출력할 문제 리스트에서 추출
     *
     * @author 조아라
     * @return void
     * 추천받는 문제 5개(List)를 DB에 저장하는 메서드
     */
    @Transactional
    public void saveResponse(String username, List<String> responseList) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        SolvedACResponseEntity solvedACResponseEntity = solvedACResponseRepository.findByUserUsername(username)
                .orElse(SolvedACResponseEntity.builder()
                        .user(user)
                        .userEmail(user.getEmail())
                        .response(responseList)
                        .updatedAt(LocalDateTime.now())
                        .build());

        solvedACResponseEntity.updateResponse(responseList);
        solvedACResponseRepository.save(solvedACResponseEntity);
    }


    /**
     * SolvedAC username을 기반으로 앨런AI에게 문제 추천을 요청한다(5문제)
     *
     * @author 조아라
     * @return String
     * 앨런AI에게 질문하기 위해 username 정보를 RESTFUL API를 통해 전송한다
     * AI(8082) 애플리케이션에 API 통신한다
     */

    public ResponseEntity<String> sovledacCall(String algoyUserName, String solvedACUserName) throws Exception {

        String requestUrl = askAllenUrl + "/response?algoyusername=" + algoyUserName + "&solvedacusername=" + solvedACUserName;


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String allenResponse = ""; //앨런에게 바로 받은 답변
        String temp = ""; // 마크다운을 제거한 Json 형식 답변
        List<String> responseList= new ArrayList<>(); //Json에서 텍스트 형식으로 변환한 화면에 띄울 최종 답변
        try{
            //API 응답을 받는다(Json 형태)
            allenResponse = httpEx.get(requestUrl, headers);
            temp = extractJsonFromMarkdown(allenResponse);

            //Json을 파싱해서 문제 제목을 list에 넣고 반환하는 메서드 호출
            responseList = convertJsonToListString(temp);
            System.out.println(responseList);

            //변경사항을 저장한다.
            saveResponse(algoyUserName, responseList);

            return ResponseEntity.ok("성공");

        } catch (Exception e){
            throw new Exception("solvedAC 호출 실패", e); //예외 발생시 상위로 전달
        }
    }

    /**
     * (5문제)
     *
     * @author 조아라
     * @return String
     *
     */
    //호출한 정보를 리스트에 넣는다 (5가지 문제 추천)
    public List<String> convertJsonToListString(String jsonResponse){
        //Gson 객체 생성
        Gson gson = new Gson();

        // Json을 배열 형태로 파싱
        SolvedACJsonResponse[] responses = gson.fromJson(jsonResponse, SolvedACJsonResponse[].class);
        //결과를 담을 리스트 생성
        List<String> titlesList = new ArrayList<>();


        // 응답 배열에서 각 항목을 순회하며 제목을 추출하여 리스트에 추가
        for (SolvedACJsonResponse response : responses) {
            if (response.getTitle() != null) {
                String tmp = response.getSite() + " - " + response.getTitle() + " (" + response.getProblemNo() + ")\n" + response.getDetails();
                titlesList.add(tmp);
            }
        }
        return titlesList;
    }

    //질문을 allen API에 묻고 답변을 받아온다
    public String askAllen(String algoyUserName, String solvedACUserName) throws Exception {
        String askUrl = askAllenUrl + "?algoyusername=" + algoyUserName + "&solvedacusername=" + solvedACUserName;
        System.out.println(askUrl);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String allenResponse = ""; //앨런에게 바로 받은 답변
        String temp = ""; // 마크다운을 제거한 Json 형식 답변
        String responseToUser = ""; //Json에서 텍스트 형식으로 변환한 화면에 띄울 최종 답변
        try{
            allenResponse = httpEx.get(askUrl, headers);
            //System.out.println(allenResponse);
            temp = extractJsonFromMarkdown(allenResponse);
            responseToUser = convertJsonToFormattedString(temp);

        }catch (Exception e){
            throw new Exception("allen에게 답변 받기 실패", e);
        }
        return responseToUser;
    }

    //앨런에게 받은 답변에서 마크다운 제거하기
    public String extractJsonFromMarkdown(String markdown){
        //마트다운 블록 시작과 끝을 제거하여 순수한 Json을 추출
        String json = markdown.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        return json;
    }

    //Json 형식을 String(화면에 보여줄 형식)으로 변환
    public String convertJsonToFormattedString(String json){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        //필요한 형식으로 변환
        String formattedString = jsonObject.get("site").getAsString() + " - " +
                                jsonObject.get("title").getAsString() + " (" +
                                jsonObject.get("problemNo").getAsString() + ")\n" +
                                jsonObject.get("details").getAsString();
        return formattedString;
    }


}
