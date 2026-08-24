package com.example.userservice.controller;


import com.example.userservice.dto.LoginRequestDto;
import com.example.userservice.dto.identity.TokenExchangeResponse;
import com.example.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/public")
public class PublicController {
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    ResponseEntity<TokenExchangeResponse> login(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(userService.login(dto));
    }
}
