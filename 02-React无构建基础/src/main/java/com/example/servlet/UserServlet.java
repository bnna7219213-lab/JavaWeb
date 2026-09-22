package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("{\"code\":200,\"msg\":\"查询成功\",\"data\":{\"id\":1,\"name\":\"张三\",\"age\":24}}");
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        StringBuilder body = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            body.append(line);
        }
        System.out.println("收到POST数据：" + body);

        PrintWriter out = response.getWriter();
        out.write("{\"code\":200,\"msg\":\"数据提交成功\"}");
        out.flush();
        out.close();
    }
}
