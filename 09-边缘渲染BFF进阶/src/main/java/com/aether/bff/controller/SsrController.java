package com.aether.bff.controller;

import com.aether.bff.service.DashboardService;
import com.aether.bff.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SSR（服务端渲染）控制器 - 模拟边缘渲染
 *
 * <p>在边缘层直接输出已渲染好的HTML片段，前端无需JS即可看到首屏内容。
 * 这是"边缘渲染"在BFF层的典型实现：</p>
 *
 * <pre>
 *   客户端请求 → BFF层（SSR模式）→ 服务端聚合数据 + 渲染HTML → 返回完整HTML
 *                                                       → 客户端直接展示（白屏时间≈0）
 *
 *   对比纯CSR：
 *   客户端请求 → 返回空HTML → 下载JS → 执行JS → 请求数据 → 渲染 → 展示
 *                                                       （白屏时间较长）
 * </pre>
 *
 * <p>在真实架构中，SSR通常由 React Next.js / Nuxt.js 或模板引擎（Thymeleaf/Freemarker）实现。
 * 这里用纯字符串拼接模拟服务端片段渲染的概念。</p>
 *
 * <p>路由前缀: /api/ssr</p>
 */
@Controller
@RequestMapping("/api/ssr")
public class SsrController {

    private static final Logger log = LoggerFactory.getLogger(SsrController.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    /**
     * 渲染首页HTML片段（服务端渲染模式）
     *
     * <p>GET /api/ssr/render/home</p>
     *
     * <p>返回完整的可直接展示的HTML片段。
     * 前端可以直接将其插入DOM，无需等待JS加载执行。</p>
     */
    @GetMapping(value = "/render/home", produces = "text/html;charset=UTF-8")
    @org.springframework.web.bind.annotationResponseBody
    public String renderHome() {
        log.info("[SSR] 收到首页SSR渲染请求");

        long start = System.currentTimeMillis();

        // 聚合数据
        var user = userService.getCurrent();

        // 服务端渲染为HTML字符串
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"ssr-home\">");
        html.append("  <div class=\"ssr-header\">");
        html.append("    <h1>Hi，").append(user.getUsername()).append("！</h1>");
        html.append("    <span class=\"ssr-badge\">").append(user.getMemberLevel()).append("</span>");
        html.append("  </div>");
        html.append("  <div class=\"ssr-stats\">");
        html.append("    <div class=\"ssr-stat-item\"><span class=\"ssr-stat-num\">").append(user.getPoints()).append("</span><span>积分</span></div>");
        html.append("    <div class=\"ssr-stat-item\"><span class=\"ssr-stat-num\">5</span><span>优惠券</span></div>");
        html.append("    <div class=\"ssr-stat-item\"><span class=\"ssr-stat-num\">3</span><span>未读消息</span></div>");
        html.append("  </div>");
        html.append("</div>");

        // 内联基础样式
        html.append("<style>");
        html.append(".ssr-home{padding:20px;font-family:sans-serif}");
        html.append(".ssr-header{display:flex;align-items:center;gap:12px;margin-bottom:16px}");
        html.append(".ssr-badge{background:#f59e0b;color:#fff;padding:2px 8px;border-radius:4px;font-size:12px}");
        html.append(".ssr-stats{display:flex;gap:20px}");
        html.append(".ssr-stat-item{display:flex;flex-direction:column;align-items:center}");
        html.append(".ssr-stat-num{font-size:24px;font-weight:700;color:#3b82f6}");
        html.append("</style>");

        long elapsed = System.currentTimeMillis() - start;
        log.info("[SSR] 首页HTML渲染完成，耗时 {}ms", elapsed);

        return html.toString();
    }

    /**
     * 渲染用户卡片HTML片段
     *
     * <p>GET /api/ssr/render/user-card</p>
     */
    @GetMapping(value = "/render/user-card", produces = "text/html;charset=UTF-8")
    @org.springframework.web.bind.annotationResponseBody
    public String renderUserCard() {
        log.info("[SSR] 收到用户卡片渲染请求");

        var user = userService.getCurrent();

        return "<div class=\"ssr-user-card\" style=\"padding:16px;background:#f8fafc;border-radius:12px;margin:8px\">"
                + "<div style=\"display:flex;align-items:center;gap:12px\">"
                + "<div style=\"width:48px;height:48px;border-radius:50%;background:linear-gradient(135deg,#3b82f6,#8b5cf6);display:flex;align-items:center;justify-content:center;font-size:20px\">"
                + (user.getAvatar() != null && !user.getAvatar().isEmpty() ? "<img src=\"" + user.getAvatar() + "\" style=\"width:100%;height:100%;border-radius:50%\">" : "👤")
                + "</div>"
                + "<div>"
                + "<div style=\"font-weight:600;font-size:16px\">" + user.getUsername() + "</div>"
                + "<div style=\"color:#94a3b8;font-size:12px;margin-top:2px\">" + user.getMemberLevel() + " · 积分 " + user.getPoints() + "</div>"
                + "</div>"
                + "</div>"
                + "</div>";
    }
}
