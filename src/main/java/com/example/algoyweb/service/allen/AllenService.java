package com.example.algoyweb.service.allen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AllenService {
    private final HttpURLConnectionEx httpEx;

    @Autowired
    public AllenService(HttpURLConnectionEx httpEx) {
        this.httpEx = httpEx;
    }

    //사용자가 푼 문제 중 문제 수준이 높은 상위 100 문제를 가져오는 api
    @Value("https://solved.ac/api/v3/user/top_100?handle=")
    String solvedAcApi;

    //userName을 이용하여 sovledAC API를 통해 푼 문제 정보를 호출한다.
    public String sovledacCall(String userName) throws Exception {

        String requestUrl = solvedAcApi + userName;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        try{
            //API 응답을 받는다(Json 형태)
            String result = httpEx.get(requestUrl, headers);
            System.out.println(result);

            //Json을 파싱한다

            //문제 정보를 list에 넣는다
            return result;
        } catch (Exception e){
            throw new Exception("solvedAC 호출 실패", e); //예외 발생시 상위로 전달
        }
    }

    //호출한 정보를 리스트에 넣는다

    //질문을 allen API에 던진다.
}
