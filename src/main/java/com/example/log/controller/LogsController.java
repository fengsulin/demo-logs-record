package com.example.log.controller;

import com.example.log.annotation.XLog;
import com.example.log.entity.XtLogEntity;
import com.example.log.entity.vo.XtLogEntityVo;
import com.example.log.service.XLogService;
import com.example.log.vo.PageVo;
import com.example.log.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/logs")
@Slf4j
public class LogsController {

    @Resource
    private XLogService xLogService;
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

    @PostMapping("/list")
    @XLog(operaModule = "日志管理",operaType = "查询")
    public ResultVo listLogsPage(@RequestBody XtLogEntityVo xtLogEntityVo){
        PageVo pageVo = xLogService.listByPage(xtLogEntityVo);
        return ResultVo.success(pageVo);
    }

}
