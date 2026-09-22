package com.example.ssm.common;

/**
 * 分页查询参数
 */
public class PageQuery {

    private Integer page = 1;
    private Integer size = 10;
    private String keyword;

    public int getOffset() {
        return (page - 1) * size;
    }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
