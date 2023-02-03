package com.example.log.service;

import com.example.log.entity.XtLogEntity;
import com.example.log.entity.vo.XtLogEntityVo;
import com.example.log.vo.PageVo;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;

public interface XLogService {
    /**
     * 日志保存
     * @param joinPoint
     * @param xtLogEntity
     */
    void save(ProceedingJoinPoint joinPoint, XtLogEntity xtLogEntity);
    PageVo listByPage(XtLogEntityVo xtLogEntityVo);
}
