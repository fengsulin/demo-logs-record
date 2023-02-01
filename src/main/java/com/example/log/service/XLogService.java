package com.example.log.service;

import com.example.log.entity.XtLogEntity;
import org.aspectj.lang.ProceedingJoinPoint;

public interface XLogService {
    void save(ProceedingJoinPoint joinPoint, XtLogEntity xtLogEntity);
}
