package com.autentia.helloworld.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleCloudHelloWorldController {

    @GetMapping(value = "/")
    public String hello(){
        return "Hi from Google Cloud";
    }

}
