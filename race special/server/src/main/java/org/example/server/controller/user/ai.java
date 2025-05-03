package org.example.server.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.result.Result;
import org.example.server.service.AiService;
import org.example.utils.AiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
@Api("用户端ai开发")
public class ai {
     @Autowired
     private AiUtil aiUtil;
     @Autowired
     private AiService aiService;
     @PostMapping("/questionSimple")
     @ApiOperation(value="简单文本输出",notes = "服务器会自动生成或获取SessionID，用于会话管理")
     public Result<Map<String,String>> questionSimple(HttpSession session, @RequestParam String question) throws Exception {
          String id = session.getId();
          log.info("这是sessionid,{} question,{}",session.getId(),question);
          Map<String,String> map = new HashMap<>();
          map.put("SessionId",id);
          //前端一定要保存下来sessionId，不保存也行其实
          String respond=aiService.question(id,question);
          map.put("Respond",respond);
          return Result.success(map);

     }






}
