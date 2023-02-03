package com.example.log.service.Impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.example.log.annotation.XLog;
import com.example.log.entity.XtLogEntity;
import com.example.log.service.XLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

@Slf4j
@Service
public class XLogServiceImpl implements XLogService {
    private final ObjectMapper objectMapper;
    private final RestHighLevelClient elasticsearchClient;

    public XLogServiceImpl(ObjectMapper objectMapper, RestHighLevelClient elasticsearchClient) {
        this.objectMapper = objectMapper;
        this.elasticsearchClient = elasticsearchClient;
    }

    /**
     * 日志存储到es
     * @param joinPoint 切面签名
     * @param xtLogEntity 日志实体
     */
    @Override
    @Async("asyncServiceExecutor")
    public void save(ProceedingJoinPoint joinPoint, XtLogEntity xtLogEntity) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        XLog aopLog = method.getAnnotation(XLog.class);
        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName()+"."+signature.getName();
        StringBuilder params = new StringBuilder("{");
        // 参数值
        Object[] argValues = joinPoint.getArgs();
        // 参数名称
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        if (argValues != null){
            for (int i = 0; i < argValues.length; i++){
                params.append(" ").append(argNames[i]).append(":").append(argValues[i]);
            }
        }
        params.append("}");
        if (xtLogEntity != null){
            xtLogEntity.setOperaModule(aopLog.operaModule())
                    .setOperaType(aopLog.operaType());
        }
        String username = null;
        assert xtLogEntity != null;
        // 判断是否是登录操作
        String loginPath = "login";
        if (loginPath.equalsIgnoreCase(signature.getName())){
            try {
                assert argValues != null;
                username = new JSONObject(argValues[0]).get("username").toString();
            }catch (Exception e){
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        xtLogEntity
                .setMethod(methodName)
                .setParams(params.toString())
                .setId(IdUtil.randomUUID())
                .setCreateTime(new Date());
        if (username != null) xtLogEntity.setUsername(username);
        IndexRequest indexRequest = new IndexRequest("demo-logs-record");
        indexRequest.id(xtLogEntity.getId());
        IndexResponse index = null;
        try {
            indexRequest.source(objectMapper.writeValueAsString(xtLogEntity), XContentType.JSON);
            index = elasticsearchClient.index(indexRequest, RequestOptions.DEFAULT);

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        if (index != null){
            log.info(index.toString());
        }
    }
}
