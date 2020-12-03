package com.adb;

import com.adb.conmon.Conmon;
import com.adb.entity.Book;
import com.adb.util.HtmlParseUtil;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ElasticsearchServerApplicationTests {



    @Autowired
    @Qualifier(value = "restHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void addData() throws Exception {
        List<Book> list = HtmlParseUtil.parseJDUtil("java");
        if(list==null || list.size()==0){
            return;
        }
        BulkRequest bulkRequest = new BulkRequest(Conmon.index);
        bulkRequest.timeout(TimeValue.timeValueSeconds(10));

        for (Book book : list) {
            bulkRequest.add(new IndexRequest(Conmon.index).source(JSON.toJSONString(book), XContentType.JSON));
        }
        //保存数据
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

}
