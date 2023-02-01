package com.example.log.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class XtLogEntity implements Serializable {
    private static final long serialVersionUID = 32L;

    private String id;
    /**
     * 操作模块
     */
    @Excel(name = "操作模块")
    private String operaModule;
    /**
     * 异常详情
     */
    @Excel(name = "异常详情")
    private String exceptionDetail;
    /**
     * 日志类型
     */
    @Excel(name = "日志类型")
    private String logType;
    /**
     * 操作类型
     */
    @Excel(name = "操作类型")
    private String operaType;
    /**
     * 操作方法
     */
    @Excel(name = "操作方法")
    private String method;
    /**
     * 参数
     */
    @Excel(name = "参数")
    private String params;
    /**
     * 请求ip
     */
    @Excel(name = "请求ip")
    private String requestIp;
    /**
     * 请求url
     */
    @Excel(name = "请求url")
    private String requestUrl;
    /**
     * 操作员名称
     */
    @Excel(name = "操作员名称")
    private String username;
    /**
     * 地址
     */
    @Excel(name = "地址")
    private String address;
    /**
     * 浏览器
     */
    @Excel(name = "浏览器")
    private String browser;
    /**
     * 请求耗时
     */
    @Excel(name = "请求耗时")
    private Long time;
    /**
     * 操作时间
     */
    @Excel(name = "操作时间",format = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    public XtLogEntity(){
        super();
    }

    public XtLogEntity(String logType,Long time){
        this.logType = logType;
        this.time = time;
    }
}
