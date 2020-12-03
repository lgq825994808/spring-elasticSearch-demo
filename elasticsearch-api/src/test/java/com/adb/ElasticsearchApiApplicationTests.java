package com.adb;

import com.adb.entity.Book;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MetaDataIndexTemplateService;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ElasticsearchApiApplicationTests {

	@Autowired
	@Qualifier(value = "restHighLevelClient")
	private RestHighLevelClient restHighLevelClient;

	public static final String index = "java-api-index";

	/**
	 * 创建索引
	 *
	 * @throws IOException
	 */
	@Test
	public void testCreatIndex() throws IOException {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
		CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
		System.out.println("创建索引结果-----" + JSON.toJSONString(createIndexResponse));
	}

	/**
	 * 查询索引是否存在
	 *
	 * @throws IOException
	 */
	@Test
	public void testExistsIndex() throws IOException {
		GetIndexRequest getIndexRequest = new GetIndexRequest(index);
		boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
		System.out.println("查询索引是否存在-----" + exists);
	}

	/**
	 * 测试删除索引
	 *
	 * @throws IOException
	 */
	@Test
	public void testDelIndex() throws IOException {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
		AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
		System.out.println("删除索引是否成功-------" + delete.isAcknowledged());
	}

	/**
	 * 添加文档
	 *
	 * @throws IOException
	 */
	@Test
	public void testAddDocument() throws IOException {
		IndexRequest indexRequest = new IndexRequest(index);
		indexRequest.id("1");
		indexRequest.timeout(TimeValue.timeValueSeconds(10));

		Book book = new Book();
		book.setBookName("java精通");
		book.setImage("http://wwww.baidu.com");
		book.setTitle("java一点就通");
		book.setPrice(23.4);
		indexRequest.source(JSON.toJSONString(book), XContentType.JSON);

		IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		System.out.println("添加文档成功-------" + JSON.toJSONString(index));
	}

	/**
	 * 批量添加文档
	 *
	 * @throws IOException
	 */
	@Test
	public void testBulkRequest() throws IOException, ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Book> list = new ArrayList<>();
		list.add(new Book("php精通", "http://wwww.baidu.com", "php一点就通", 23.1,simpleDateFormat.parse("2020-10-11 15:26:00")));
		list.add(new Book("c++精通", "http://wwww.baidu.com", "c++一点就通", 23.2,simpleDateFormat.parse("2020-10-12 15:26:00")));
		list.add(new Book("net精通", "http://wwww.baidu.com", "net一点就通", 23.3,simpleDateFormat.parse("2020-10-13 15:26:00")));
		list.add(new Book("go精通", "http://wwww.baidu.com", "go一点就通", 23.4,simpleDateFormat.parse("2020-10-14 15:26:00")));
		list.add(new Book("javascript精通", "http://wwww.baidu.com", "javascript一点就通", 23.5,simpleDateFormat.parse("2020-10-15 15:26:00")));

		BulkRequest bulkRequest = new BulkRequest(index);
		bulkRequest.timeout(TimeValue.timeValueSeconds(10));

		for (Book book : list) {
			bulkRequest.add(new IndexRequest(index).source(JSON.toJSONString(book), XContentType.JSON));
		}

		BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println("批量添加文档-----" + JSON.toJSONString(bulk));
	}

	/**
	 * 判断文档是否存在
	 *
	 * @throws IOException
	 */
	@Test
	public void testExistsDocument() throws IOException {
		GetRequest getRequest = new GetRequest(index, "1");
		boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println("判断文档是否存在-------" + JSON.toJSONString(exists));
	}

	/**
	 * 更新文档信息(只更新实体里面有的数据的字段，如果实体里面字段没有的字段，ES数据库里面的该字段保持原有不变)
	 *
	 * @throws IOException
	 */
	@Test
	public void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest(index, "1");

		Book book = new Book();
		book.setBookName("java精通");
		book.setImage("http://wwww.baidu.com");
		book.setTitle("java一点就通123");
		//book.setPrice(23.4);
		updateRequest.doc(JSON.toJSONString(book), XContentType.JSON);

		UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println("更新文档信息-------" + JSON.toJSONString(update));
	}

	/**
	 * 删除文档信息
	 *
	 * @throws IOException
	 */
	@Test
	public void testDelDocument() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest(index, "1");
		DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println("删除文档信息-------" + JSON.toJSONString(delete));
	}

	/**
	 * 查询文档信息
	 *
	 * @throws IOException
	 */
	@Test
	public void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest(index, "1");
		GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		System.out.println("查询文档信息-------" + JSON.toJSONString(documentFields));
	}

	/**
	 * 多条件查询
	 *
	 * @throws IOException
	 */
	@Test
	public void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest(index);
		//构建搜索条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//精确查找
		/*TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("bookName", "java精通4");
		searchSourceBuilder.query(termQueryBuilder);*/
		//模糊查找
		/*MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("bookName", "java精通");
		searchSourceBuilder.query(matchQueryBuilder);*/
		//全模糊查找
		/*MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
		searchSourceBuilder.query(matchAllQueryBuilder);*/

		//多条件查询
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				//.must(QueryBuilders.matchQuery("bookName", "go")); //and 条件
				//.should(QueryBuilders.matchQuery("bookName", "2")) //or条件
				.filter(QueryBuilders
						.rangeQuery("price")
						.from(23.2).to(23.5));
		searchSourceBuilder.query(query);
		searchSourceBuilder.from(0);  //分页  显示第几页
		searchSourceBuilder.size(10);  //分页  每页显示几条

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		System.out.println("多条件查询结果-----"+JSON.toJSONString(search.getHits()));
	}
}