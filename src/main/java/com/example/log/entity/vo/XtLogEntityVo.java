package com.example.log.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class XtLogEntityVo implements Serializable {
    private static final long serialVersionUID = 34L;

    /**
     * 操作模块
     */
    private String operaModule;
    /**
     * 日志类型
     */
    private String logType;
    /**
     * 操作类型
     */
    private String operaType;
    /**
     * 操作方法
     */
    private String method;
    /**
     * 请求ip
     */
    private String requestIp;
    /**
     * 请求url
     */
    private String requestUrl;
    /**
     * 操作员名称
     */
    private String username;
    /**
     * 地址
     */
    private String address;
    /**
     * 开始时间
     */
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh",timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh",timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Integer pageNum;
    private Integer pageSize;
}
