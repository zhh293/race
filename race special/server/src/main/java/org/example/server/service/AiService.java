package org.example.server.service;

import javax.servlet.http.HttpSession;

public interface AiService {
    String question(String SessionId, String question) throws Exception;
}
