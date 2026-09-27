package org.example.demo777.Controller;

import org.springframework.boot.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class textController {
    @GetMapping("/text")
    public String text(){
        return "项目启动成功";
    }




}
