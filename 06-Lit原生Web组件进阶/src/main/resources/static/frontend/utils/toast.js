/**
 * Toast 通知工具
 * 全局轻量消息提示，无需额外依赖
 */

let container = null;

function getContainer() {
    if (!container) {
        container = document.getElementById('toast-container');
    }
    return container;
}

/**
 * 显示一条 Toast 通知
 * @param {string} message - 消息内容
 * @param {'success'|'error'|'info'} type - 通知类型
 * @param {number} duration - 显示时长（毫秒）
 */
export function showToast(message, type = 'info', duration = 3000) {
    const el = document.createElement('div');
    const colors = {
        success: { bg: '#e6f4ea', color: '#137333', border: '#34a853' },
        error:   { bg: '#fce8e6', color: '#c5221f', border: '#ea4335' },
        info:    { bg: '#e8f0fe', color: '#1a73e8', border: '#4285f4' }
    };
    const c = colors[type] || colors.info;

    el.textContent = message;
    el.style.cssText = `
        padding: 12px 20px;
        background: ${c.bg};
        color: ${c.color};
        border-left: 4px solid ${c.border};
        border-radius: 6px;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        animation: toast-slide-in 0.3s ease;
        max-width: 360px;
        pointer-events: auto;
    `;

    getContainer().appendChild(el);

    setTimeout(() => {
        el.style.opacity = '0';
        el.style.transform = 'translateX(20px)';
        el.style.transition = 'all 0.3s ease';
        setTimeout(() => el.remove(), 300);
    }, duration);
}

// 注入动画样式
const style = document.createElement('style');
style.textContent = `
    @keyframes toast-slide-in {
        from { opacity: 0; transform: translateX(20px); }
        to { opacity: 1; transform: translateX(0); }
    }
`;
document.head.appendChild(style);
