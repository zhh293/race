package org.example.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.DTO.UserLoginDTO;
import org.example.pojo.entity.user;

@Mapper
public interface AiMapper {
    user login(UserLoginDTO userLoginDTO);

    void insertNewUser(user user1);
}
