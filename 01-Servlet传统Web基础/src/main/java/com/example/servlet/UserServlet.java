package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户接口Servlet - 基础版
 * 接口路径: /api/user
 * 支持GET查询和POST提交
 */
public class UserServlet extends HttpServlet {

    /**
     * GET请求：查询用户信息
     * 响应格式：{"code":200,"msg":"成功","data":{"id":1,"name":"张三","age":24}}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String jsonResult = "{\"code\":200,\"msg\":\"查询成功\",\"data\":{\"id\":1,\"name\":\"张三\",\"age\":24}}";

        PrintWriter out = response.getWriter();
        out.write(jsonResult);
        out.flush();
        out.close();
    }

    /**
     * POST请求：接收前端提交的JSON数据
     * 读取请求体，解析参数（简化版直接原始读取）
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        // 读取前端fetch传来的JSON请求体
        StringBuilder requestBody = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        System.out.println("收到前端POST数据：" + requestBody.toString());

        // 返回响应
        PrintWriter out = response.getWriter();
        out.write("{\"code\":200,\"msg\":\"数据提交成功，收到数据：" + requestBody.toString().replace("\"", "\\\"") + "\"}");
        out.flush();
        out.close();
    }
}
