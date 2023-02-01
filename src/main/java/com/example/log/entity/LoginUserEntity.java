package com.example.log.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginUserEntity implements Serializable {
    private static final long serialVersionUID = 33L;

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
