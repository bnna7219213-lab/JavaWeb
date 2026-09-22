/**
 * 进阶版入口文件
 *
 * 职责：
 * 1. 导入全局样式
 * 2. 初始化路由（getRouter 会自动注册路由表并监听 hashchange）
 */

// 导入路由初始化（单例模式，首次调用时自动注册所有路由）
import { getRouter } from './components/app-router.js';
import './pages/home-page.js';
import './pages/user-list-page.js';
import './pages/user-detail-page.js';
import './pages/user-form-page.js';
import './pages/not-found-page.js';

// 启动路由
getRouter();

// 全局错误捕获（模块加载失败时的友好提示）
window.addEventListener('error', (e) => {
    if (e.message && e.message.includes('Failed to fetch')) {
        console.error('[Lit] 模块加载失败，请检查网络连接和 CDN 访问');
    }
});
