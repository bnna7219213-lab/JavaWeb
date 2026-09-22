package com.example.servlet;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.JsonResult;
import com.example.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class UserListServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String search = req.getParameter("search");
        List<User> users = (search != null && !search.isEmpty()) ? userService.search(search) : userService.findAll();
        PrintWriter out = resp.getWriter();
        out.write(JsonUtil.toJson(JsonResult.success("共"+users.size()+"条", users)));
        out.flush(); out.close();
    }
}
