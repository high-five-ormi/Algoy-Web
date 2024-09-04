package com.example.algoyweb.controller.allen;

import com.example.algoyweb.service.allen.AllenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/algoy/allen")
public class AllenController {

    private final AllenService allenService;

    @Autowired
    public AllenController(AllenService allenService) {
        this.allenService = allenService;
    }

    @GetMapping("/solvedac")
    public ResponseEntity<String> solvedac(@PathVariable String userName) throws Exception {
        System.out.println("controller check");
        try{
            String response = allenService.sovledacCall(userName);
            System.out.println(response);
            return ResponseEntity.ok("solvedac");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("api 호출 중 에러가 발생하였습니다");
        }

    }

}
