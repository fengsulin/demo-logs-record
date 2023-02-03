package com.example.log.service.Impl;

import com.example.log.entity.XtLogEntity;
import com.example.log.entity.vo.XtLogEntityVo;
import com.example.log.service.XLogService;
import com.example.log.vo.PageVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class XLogServiceImplTest {

    @Resource
    private XLogService xLogService;

    @Test
    void save() {
    }

    @Test
    void listByUsername() {
        PageVo<List<XtLogEntity>> pageVo = xLogService.listByPage(new XtLogEntityVo());
        System.out.println("~~~~~~~~~~~~~~~"+pageVo.getTotal());
        pageVo.getData().forEach(xtLogEntity -> System.out.println(xtLogEntity.toString()));
    }
}