package com.mmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

    public Category(){}

    public Category(String categoryName,Integer parentId,Boolean status){
        this.name = categoryName;
        this.parentId = parentId;
        this.status = status;
    }
}