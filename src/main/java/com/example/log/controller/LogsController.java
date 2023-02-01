package com.example.log.controller;

import com.example.log.annotation.XLog;
import com.example.log.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Slf4j
public class LogsController {

    @GetMapping("/test")
    @XLog(operaModule = "测试模块",operaType = "测试")
    public ResultVo<String> test(){
        log.info("hello logs-record");
        log.error("error");
        return ResultVo.success(null);
    }

    @GetMapping("/exe")
    @XLog(operaModule = "测试模块",operaType = "异常测试")
    public String test1(){
        throw new RuntimeException("exception");
    }

}
