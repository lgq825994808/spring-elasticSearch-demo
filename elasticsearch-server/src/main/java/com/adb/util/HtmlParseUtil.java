package com.adb.util;

import com.adb.entity.Book;
import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
        List<Book> php = parseJDUtil("java");
        System.out.println(JSON.toJSONString(php));
    }

    /**
     * 爬虫 京东搜索页面
     * @param keyword
     * @return
     * @throws Exception
     */
    public static List<Book> parseJDUtil(String keyword) throws Exception {
        String url="https://search.jd.com/Search?keyword="+keyword;
        Document parse = Jsoup.parse(new URL(url),30000);

        List<Book> bookList = new ArrayList<>();
        // 根据id获取元素
        Element goodsList = parse.getElementById("J_goodsList");
        //获取元素里面的标签
        Elements li =   goodsList.getElementsByTag("li");
        for (Element element : li) {
            //获取名称
            String name = element.getElementsByClass("p-name").get(0).getElementsByTag("em").text();
            //标题
            String title = element.getElementsByClass("promo-words").text();
            //String title = element.getElementsByClass("p-name").get(0).getElementsByTag("a").attr("title");

            //获取元素里面的“img”标签，里面的“data-lazy-img”属性的值
            String img = element.getElementsByTag("img").attr("data-lazy-img");
            //获取价格
            String price = element.getElementsByClass("p-price").get(0).getElementsByTag("i").text();
            //String price = element.getElementsByClass("p-price").eq(0).text();

            Book book = new Book(name, title, img, Double.parseDouble(price), new Date());
            bookList.add(book);
        }
        return bookList;
    }
}
