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

/**
 * 用户列表Collection接口Servlet
 * 路径: /api/user/list
 * 
 * GET /api/user/list         - 查询全部用户
 * GET /api/user/list?search=张 - 按姓名搜索
 */
public class UserListServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String search = request.getParameter("search");
        List<User> users;

        if (search != null && !search.isEmpty()) {
            users = userService.searchByName(search);
        } else {
            users = userService.findAll();
        }

        out.write(JsonUtil.toJson(JsonResult.success("查询成功，共 " + users.size() + " 条记录", users)));
        out.flush();
        out.close();
    }
}
