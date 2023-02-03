package com.example.log.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageVo<T> implements Serializable {
    private static final long serialVersionUID = 2384913L;
    /**页码*/
    private Long pageTotal;
    /**每页数*/
    private Long total;
    /**数据实体*/
    private T data;

}
