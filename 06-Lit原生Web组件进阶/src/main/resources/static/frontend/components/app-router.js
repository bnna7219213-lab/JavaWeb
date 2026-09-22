/**
 * app-router.js - 前端路由管理器 + 组件事件总线
 *
 * Hash 路由：
 *   #/              -> <home-page>
 *   #/users         -> <user-list-page>
 *   #/users/new     -> <user-form-page> (create mode)
 *   #/users/:id     -> <user-detail-page>
 *   #/users/:id/edit -> <user-form-page> (edit mode)
 *
 * 组件间通信：
 *   - EventBus (发布/订阅模式)
 *   - dispatchEvent (DOM 自定义事件)
 */

import '../pages/home-page.js';
import '../pages/user-list-page.js';
import '../pages/user-detail-page.js';
import '../pages/user-form-page.js';
import '../pages/not-found-page.js';

// ==================== 全局事件总线 ====================

class EventBus {
    constructor() {
        this.listeners = {};
    }

    on(event, callback) {
        if (!this.listeners[event]) this.listeners[event] = [];
        this.listeners[event].push(callback);
        return () => this.off(event, callback);
    }

    off(event, callback) {
        if (!this.listeners[event]) return;
        this.listeners[event] = this.listeners[event].filter(cb => cb !== callback);
    }

    emit(event, data) {
        if (!this.listeners[event]) return;
        this.listeners[event].forEach(cb => {
            try { cb(data); } catch (e) { console.error(e); }
        });
    }
}

export const eventBus = new EventBus();

// 预定义事件名称常量
export const EVENTS = {
    USER_CREATED: 'user:created',
    USER_UPDATED: 'user:updated',
    USER_DELETED: 'user:deleted',
    NAVIGATE: 'navigate',
};

/**
 * 编程式导航
 */
export function navigate(path) {
    window.location.hash = path.startsWith('#') ? path : '#' + path;
}

/**
 * 获取当前路由信息
 */
export function getCurrentRoute() {
    return currentRouteInfo;
}

let currentRouteInfo = null;

// ==================== 路由管理器 ====================

class Router {
    constructor(container) {
        this.container = container;
        this.routes = [];
    }

    /**
     * 注册路由
     * @param {string} pattern - 路由模式，支持 :param 参数
     * @param {string} tagName - 组件标签名
     */
    addRoute(pattern, tagName) {
        const paramNames = [];
        const regexStr = pattern.replace(/:([^/]+)/g, (_, name) => {
            paramNames.push(name);
            return '([^/]+)';
        });
        this.routes.push({
            pattern,
            regex: new RegExp('^' + regexStr + '$'),
            paramNames,
            tagName
        });
        return this;  // 链式调用
    }

    /**
     * 解析当前 hash 并渲染
     */
    resolve() {
        const hash = window.location.hash || '#/';
        const path = hash.startsWith('#') ? hash.slice(1) : '/';

        for (const route of this.routes) {
            const match = path.match(route.regex);
            if (match) {
                const params = {};
                route.paramNames.forEach((name, i) => {
                    // 尝试将数字参数转为 Number
                    const val = match[i + 1];
                    params[name] = /^\d+$/.test(val) ? Number(val) : val;
                });
                this._render(route.tagName, params, path);
                currentRouteInfo = { tagName: route.tagName, params, path };
                return;
            }
        }

        // 404 兜底
        this._render('not-found-page', {}, path);
    }

    _render(tagName, params, path) {
        const el = document.createElement(tagName);
        Object.entries(params).forEach(([key, value]) => {
            el[key] = value;
        });
        el.routePath = path;
        el.routeParams = params;

        this.container.innerHTML = '';
        this.container.appendChild(el);
        this._updateNavActive(path);
    }

    _updateNavActive(currentPath) {
        document.querySelectorAll('.nav-links a').forEach(link => {
            const route = link.dataset.route;
            if (route === currentPath || (route !== '/' && currentPath.startsWith(route + '/'))) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });
    }
}

// ==================== 单例初始化 ====================

let routerInstance = null;

export function getRouter() {
    if (!routerInstance) {
        const container = document.getElementById('appContainer');
        routerInstance = new Router(container);

        routerInstance
            .addRoute('/', 'home-page')
            .addRoute('/users', 'user-list-page')
            .addRoute('/users/new', 'user-form-page')
            .addRoute('/users/:id/edit', 'user-form-page')
            .addRoute('/users/:id', 'user-detail-page');

        // 监听 hashchange
        window.addEventListener('hashchange', () => routerInstance.resolve());

        // 首次加载
        routerInstance.resolve();
    }
    return routerInstance;
}
