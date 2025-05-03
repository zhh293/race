package org.example.server.service;

import org.example.utils.AiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class Aiserviceimpl implements AiService{
    @Autowired
    private AiUtil aiUtil;
    @Override
    public String question(String SessionId, String question) throws Exception {
        return aiUtil.getString(question, SessionId);
    }
}
