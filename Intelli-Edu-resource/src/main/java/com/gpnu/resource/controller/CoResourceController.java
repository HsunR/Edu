package com.gpnu.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coresource")
public class CoResourceController {

    @GetMapping("/health")
    public String healthCheck() {
        return "CoResourceController is healthy";
    }
}
