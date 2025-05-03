package org.example.server.service;

import org.example.pojo.DTO.UserLoginDTO;
import org.example.pojo.entity.user;

import javax.servlet.http.HttpSession;

public interface AiService {
    String question(String SessionId, String question) throws Exception;

    user login(UserLoginDTO userLoginDTO);
}
