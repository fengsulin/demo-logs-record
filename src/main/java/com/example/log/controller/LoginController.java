package com.example.log.controller;

import com.example.log.annotation.XLog;
import com.example.log.entity.LoginUserEntity;
import com.example.log.utils.RequestHolder;
import com.example.log.vo.ResultVo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
public class LoginController {

    @PostMapping("/login")
    @XLog(operaModule = "用户模块",operaType = "登录")
    public ResultVo login(@RequestBody @Valid LoginUserEntity userEntity){
        // 校验
        // 存入session
        RequestHolder.getHttpServletRequest().getSession().setAttribute("user",userEntity);
        return ResultVo.success();
    }

    @PutMapping("/logout")
    @XLog(operaModule = "用户模块",operaType = "注销")
    public ResultVo logout(){
        RequestHolder.getHttpServletRequest().getSession().invalidate();
        return ResultVo.success();
    }
}
