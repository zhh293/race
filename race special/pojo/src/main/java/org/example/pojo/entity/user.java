package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class user implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String password;
    private String username;
    private Long count;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private LocalDateTime updateAt;
}
