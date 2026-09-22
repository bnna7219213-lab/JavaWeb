package com.example.ssm.common;

/**
 * 统一响应结果（支持分页）
 */
public class Result<T> {

    private int code;
    private String msg;
    private T data;
    private Long total;     // 分页总数
    private Integer page;   // 当前页
    private Integer size;   // 每页数量

    public Result() {}

    public static <T> Result<T> ok(String msg, T data) {
        Result<T> r = new Result<>();
        r.code = 200; r.msg = msg; r.data = data;
        return r;
    }

    public static <T> Result<T> okPage(String msg, T data, long total, int page, int size) {
        Result<T> r = new Result<>();
        r.code = 200; r.msg = msg; r.data = data;
        r.total = total; r.page = page; r.size = size;
        return r;
    }

    public static <T> Result<T> err(int code, String msg) {
        Result<T> r = new Result<>();
        r.code = code; r.msg = msg;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
