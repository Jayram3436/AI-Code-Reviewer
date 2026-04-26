package controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import model.CodeRequest;
import service.CodeService;

@RestController
@RequestMapping("/api")
public class CodeController {

    @Autowired
    private CodeService service;

    @PostMapping("/review")
    public Object review(@RequestBody CodeRequest request) {
        return service.reviewCode(request);
    }
    
    @GetMapping("/")
    public String home() {
        return "Code Reviewer API is running 🚀";
    }
}
