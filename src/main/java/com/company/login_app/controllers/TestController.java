package com.company.login_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {


    @GetMapping(value = {"/","/welcome"})
    public String welcome(){
        return "welcome-page";
    }

}
