package org.example.server.service;

import org.example.pojo.DTO.UserLoginDTO;
import org.example.pojo.entity.user;
import org.example.server.mapper.AiMapper;
import org.example.utils.AiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class Aiserviceimpl implements AiService{
    @Autowired
    private AiUtil aiUtil;
    @Autowired
    private AiMapper aiMapper;
    @Override
    public String question(String SessionId, String question) throws Exception {
        return aiUtil.getString(question, SessionId);
    }

    @Override
    public user login(UserLoginDTO userLoginDTO) {
        user user = aiMapper.login(userLoginDTO);
        if(user==null) {
            user user1 = new user();
            user1.setUsername(userLoginDTO.getUsername());
            user1.setPassword(userLoginDTO.getPassword());
            user1.setEmail(userLoginDTO.getEmail());
            user1.setLoginTime(LocalDateTime.now());
            user1.setCount(0L);
            user1.setUpdateAt(LocalDateTime.now());
            aiMapper.insertNewUser(user1);
            return user1;
        }
        return user;
    }
}
