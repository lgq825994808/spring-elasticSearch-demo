package com.adb.controller;

import com.adb.conmon.Conmon;
import com.adb.entity.Book;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {

    @Autowired
    @Qualifier(value = "restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;


    /*@GetMapping("/")
    public String index(){
        return "测试";
    }*/

    @GetMapping("/search/{keyword}")
    public List<Book> searePage(@PathVariable("keyword") String keyword,
                          @RequestParam(value="page",defaultValue = "1",required = false) Integer page,
                          @RequestParam(value = "pageSize",defaultValue = "10",required = false) Integer pageSize) throws IOException {
        List<Book> arrayList = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(Conmon.index);
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //多条件查询
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("bookName",keyword));
                /*.filter(QueryBuilders
                        .rangeQuery("price")
                        .from(50.1).to(65.1));*/
        searchSourceBuilder.query(query);
        searchSourceBuilder.from(0);  //分页  显示第几页
        searchSourceBuilder.size(10);  //分页  每页显示几条

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Book book = JSON.parseObject(JSON.toJSONString(sourceAsMap), Book.class);
            arrayList.add(book);
        }
        return arrayList;
    }


}
