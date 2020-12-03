package com.adb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Book {
    //名称
    private String bookName;
    //标题
    private String title;
    //图片
    private String image;
    //价格
    private Double price;

    //生产日期
    private Date productionDate;
}
