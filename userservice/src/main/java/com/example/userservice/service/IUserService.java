package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequestDTO;
import com.example.userservice.dto.LoginRequestDto;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.dto.identity.TokenExchangeResponse;

import java.util.List;

public interface IUserService {
    UserResponseDTO createUser(CreateUserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, CreateUserRequestDTO dto);
    void deleteUser(Long id);

    TokenExchangeResponse login(LoginRequestDto dto);
}
