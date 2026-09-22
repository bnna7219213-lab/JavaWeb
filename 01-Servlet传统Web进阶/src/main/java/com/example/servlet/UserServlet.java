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
import java.util.Map;

/**
 * 用户单资源接口Servlet
 * 路径: /api/user
 * 
 * GET /api/user?id=1  - 查询单个用户
 * POST /api/user      - 新增用户
 */
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            out.write(JsonUtil.toJson(JsonResult.error(400, "缺少参数：id")));
            out.flush();
            return;
        }

        try {
            Integer id = Integer.parseInt(idParam);
            User user = userService.findById(id);
            if (user != null) {
                out.write(JsonUtil.toJson(JsonResult.success("查询成功", user)));
            } else {
                out.write(JsonUtil.toJson(JsonResult.error(404, "用户不存在")));
            }
        } catch (NumberFormatException e) {
            out.write(JsonUtil.toJson(JsonResult.error(400, "参数格式错误：id必须为整数")));
        }
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 读取JSON请求体
        StringBuilder body = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        try {
            User user = JsonUtil.fromJson(body.toString(), User.class);
            User saved = userService.save(user);
            out.write(JsonUtil.toJson(JsonResult.success("用户创建成功", saved)));
        } catch (Exception e) {
            out.write(JsonUtil.toJson(JsonResult.error(500, "数据解析失败：" + e.getMessage())));
        }
        out.flush();
        out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null) {
            out.write(JsonUtil.toJson(JsonResult.error(400, "缺少参数：id")));
            out.flush();
            return;
        }

        try {
            Integer id = Integer.parseInt(idParam);
            boolean deleted = userService.delete(id);
            if (deleted) {
                out.write(JsonUtil.toJson(JsonResult.success("删除成功", null)));
            } else {
                out.write(JsonUtil.toJson(JsonResult.error(404, "用户不存在")));
            }
        } catch (NumberFormatException e) {
            out.write(JsonUtil.toJson(JsonResult.error(400, "参数格式错误")));
        }
        out.flush();
        out.close();
    }
}
