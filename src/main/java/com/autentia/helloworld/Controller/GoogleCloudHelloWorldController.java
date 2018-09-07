package com.autentia.helloworld.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class GoogleCloudHelloWorldController {

    @GetMapping(value = "/hello")
    public String hello(){
        return "Hi from Google Cloud";
    }

}
