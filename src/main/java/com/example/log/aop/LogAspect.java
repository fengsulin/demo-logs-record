package com.example.log.aop;

import com.example.log.entity.LoginUserEntity;
import com.example.log.entity.XtLogEntity;
import com.example.log.service.XLogService;
import com.example.log.utils.FileUtil;
import com.example.log.utils.IpUtils;
import com.example.log.utils.RequestHolder;
import com.example.log.utils.ThrowableUtil;
import com.example.log.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 日志记录切面类
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final XLogService xLogService;
    private long currentTime = 0L;

    public LogAspect(XLogService xLogService) {
        this.xLogService = xLogService;
    }

    @Pointcut("@annotation(com.example.log.annotation.XLog)")
    public void logPointcut(){}

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        currentTime = System.currentTimeMillis();
        // 在proceed之前读取session
        String username = getUsername();
        result = joinPoint.proceed();
        XtLogEntity logEntity = new XtLogEntity("INFO", System.currentTimeMillis() - currentTime);
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logEntity.setRequestIp(IpUtils.getIpAddress(request))
                .setBrowser(IpUtils.getBrowser(request))
                .setUsername(username)
                .setRequestUrl(request.getRequestURI())
                .setAddress(IpUtils.getIpWithAllCache(FileUtil.getPath("data/ip2region.xdb"),IpUtils.getIpAddress(request)));
        xLogService.save((ProceedingJoinPoint)joinPoint,logEntity);
        ResultVo resultVo = (ResultVo) result;
        return resultVo;
    }

    @AfterThrowing(value = "logPointcut()",throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint,Throwable e){
        XtLogEntity logEntity = new XtLogEntity("ERROR", System.currentTimeMillis() - currentTime);
        logEntity.setExceptionDetail(ThrowableUtil.stackTraceToString(e.getClass().getName(),e.getMessage(),e.getStackTrace()));
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logEntity.setRequestIp(IpUtils.getIpAddress(request))
                .setBrowser(IpUtils.getBrowser(request))
                .setUsername(getUsername())
                .setRequestUrl(request.getRequestURI())
                .setAddress(IpUtils.getIpWithAllCache(FileUtil.getPath("data/ip2region.xdb"),IpUtils.getIpAddress(request)));
        xLogService.save((ProceedingJoinPoint)joinPoint,logEntity);
    }

    private String getUsername(){
        HttpServletRequest httpServletRequest = RequestHolder.getHttpServletRequest();
        HttpSession session = httpServletRequest.getSession();
        LoginUserEntity user = (LoginUserEntity) session.getAttribute("user");
        if (user == null) return "";
        return user.getUsername();
    }
}
