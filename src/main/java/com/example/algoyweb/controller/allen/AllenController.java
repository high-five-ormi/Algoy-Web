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

    @GetMapping("/solvedac")
    public ResponseEntity<String> solvedac(@RequestParam String username, @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        System.out.println("controller check");
        try{
            List<String> titlesList = allenService.sovledacCall(username);

            System.out.println(titlesList);
            return ResponseEntity.ok("solvedac");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다");
        }

    }

}
