package com.example.servlet;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.JsonResult;
import com.example.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id");
        if (id == null) { out.write(JsonUtil.toJson(JsonResult.error(400,"缺少id"))); out.flush(); return; }
        User u = userService.findById(Integer.parseInt(id));
        out.write(u != null ? JsonUtil.toJson(JsonResult.success("ok", u)) : JsonUtil.toJson(JsonResult.error(404,"不存在")));
        out.flush(); out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder sb = new StringBuilder();
        String line; while ((line = req.getReader().readLine()) != null) sb.append(line);
        User u = JsonUtil.fromJson(sb.toString(), User.class);
        User saved = userService.save(u);
        PrintWriter out = resp.getWriter();
        out.write(JsonUtil.toJson(JsonResult.success("创建成功", saved)));
        out.flush(); out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String id = req.getParameter("id");
        if (id == null) { out.write(JsonUtil.toJson(JsonResult.error(400,"缺少id"))); out.flush(); return; }
        boolean ok = userService.delete(Integer.parseInt(id));
        out.write(JsonUtil.toJson(ok ? JsonResult.success("删除成功", null) : JsonResult.error(404,"不存在")));
        out.flush(); out.close();
    }
}
