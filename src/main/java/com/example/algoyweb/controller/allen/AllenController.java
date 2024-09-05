package com.example.algoyweb.controller.allen;

import com.example.algoyweb.service.allen.AllenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/algoy/allen")
public class AllenController {

    private final AllenService allenService;

    @Autowired
    public AllenController(AllenService allenService) {
        this.allenService = allenService;
    }

    //solvedAC 기반으로 문제 추천 받기
    @GetMapping("/solvedac")
    public ResponseEntity<String> solvedac(@RequestParam String solvedacusername, String algoyusername) throws Exception {
        System.out.println("controller check"); //@AuthenticationPrincipal UserDetails userDetails
        try{
            //사용자가 푼 문제중 상위 100문제 추출하기
            //List<String> titlesList = allenService.sovledacCall(username);
            //System.out.println(titlesList);
            //userID와 (문제제목이 담긴)리스트를 앨런AI에게 요청보냄

            //userID와 solvedAC의 username을 8082로 보낸다.
            //String allenResponse = allenService.askAllen(userDetails.getUsername(), username);
            String allenResponse = allenService.askAllen(algoyusername, solvedacusername);

            return ResponseEntity.ok(allenResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다");
        }

    }

}
