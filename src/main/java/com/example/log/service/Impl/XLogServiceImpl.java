package com.example.log.service.Impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import com.example.log.annotation.XLog;
import com.example.log.entity.XtLogEntity;
import com.example.log.entity.vo.XtLogEntityVo;
import com.example.log.service.XLogService;
import com.example.log.vo.PageVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

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

    @Override
    public PageVo listByPage(XtLogEntityVo xtLogEntityVo) {
        long total = 0;
        long pageTotal = 0;
        // 用于封装检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 用于封装查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 用户构建查询条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (xtLogEntityVo.getUsername() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("username",xtLogEntityVo.getUsername()));
        }
        if (xtLogEntityVo.getLogType() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("logType",xtLogEntityVo.getLogType()));
        }
        if (xtLogEntityVo.getAddress() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("address",xtLogEntityVo.getAddress()));
        }
        if (xtLogEntityVo.getOperaModule() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("operaModule",xtLogEntityVo.getOperaModule()));
        }
        if (xtLogEntityVo.getRequestIp() !=null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("requestIp",xtLogEntityVo.getRequestIp()));
        }
        if (xtLogEntityVo.getOperaType() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("operaType",xtLogEntityVo.getOperaType()));
        }
        if (xtLogEntityVo.getMethod() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("method",xtLogEntityVo.getMethod()));
        }
        if (xtLogEntityVo.getRequestUrl() != null){
            boolQueryBuilder.must(QueryBuilders.matchQuery("requestUrl",xtLogEntityVo.getRequestUrl()));
        }
        if (xtLogEntityVo.getStartTime() != null){
            boolQueryBuilder.must(QueryBuilders.rangeQuery("createTime")
                    .format("yyyy-MM-dd HH:mm:ss")
                    .timeZone("GMT+8")
                    .gte(xtLogEntityVo.getStartTime())
                    .includeLower(true).includeUpper(true)
            );
        }
        if (xtLogEntityVo.getEndTime() != null){
            boolQueryBuilder.must(QueryBuilders.rangeQuery("createTime")
                    .format("yyyy-MM-dd HH:mm:ss")
                    .timeZone("GMT+8")
                    .lte(xtLogEntityVo.getEndTime())
                    .includeLower(true).includeUpper(true)
            );
        }
        // 默认pageSize=10，pageNUm=1
        int pageSize = xtLogEntityVo.getPageSize() != null && xtLogEntityVo.getPageSize() > 0 ?  xtLogEntityVo.getPageSize() : 10;
        int pageNum = xtLogEntityVo.getPageNum() != null && xtLogEntityVo.getPageNum() > 0 ? (xtLogEntityVo.getPageNum() - 1) * xtLogEntityVo.getPageSize() : 1;

        // 高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field field = new HighlightBuilder.Field("logType");
        highlightBuilder.field(field);
        highlightBuilder.preTags("<label style='color:green'>");
        highlightBuilder.postTags("</label>");
        builder.highlighter(highlightBuilder);
        builder.query(boolQueryBuilder);

        // 排序
        builder.sort("createTime", SortOrder.DESC);

        // 分页
        builder.from(pageNum).size(pageSize);
        // 搜索
        searchRequest.indices("demo-logs-record");
        searchRequest.source(builder);
        // 执行请求
        SearchResponse search = null;
        try {
            search = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("日志分页查询失败",e);
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(search.toString());
        SearchHits hits = search.getHits();
        // 封装数据
        List<XtLogEntity> logEntities = new ArrayList<>();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            String sourceAsString = searchHit.getSourceAsString();
            XtLogEntity xtLogEntity = null;
            try {
                xtLogEntity = objectMapper.readValue(sourceAsString, XtLogEntity.class);
            } catch (JsonProcessingException e) {
                log.error("字符串转换为[XtLogEntity]类失败",e);
                throw new RuntimeException(e.getMessage());
            }
            // 处理高亮
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField logType = highlightFields.get("logType");
            if (logType != null){
                String highlight = Arrays.toString(logType.getFragments());
                xtLogEntity.setLogType(highlight);
            }
            logEntities.add(xtLogEntity);
        }

        total = hits.getTotalHits().value;
        pageTotal = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
        PageVo<List<XtLogEntity>> objectPageVo = new PageVo<>();
        objectPageVo.setPageTotal(pageTotal);
        objectPageVo.setTotal(total);
        objectPageVo.setData(logEntities);
        return objectPageVo;
    }
}
