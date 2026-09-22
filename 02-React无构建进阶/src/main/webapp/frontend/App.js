import { useState, useEffect } from 'react';
import { html } from './utils/react-html.js';
import { HashRouter, Routes, Route, Link, useNavigate } from 'react-router-dom';
import Home from './pages/Home.js';
import UserList from './pages/UserList.js';
import UserDetail from './pages/UserDetail.js';
import UserCreate from './pages/UserCreate.js';

// 导航栏组件
function NavBar() {
    return html`
        <nav>
            <${Link} to="/">首页<//>
            <${Link} to="/users">用户列表<//>
            <${Link} to="/users/new">新增用户<//>
        </nav>
    `;
}

export default function App() {
    return html`
        <${HashRouter}>
            <${NavBar} />
            <div id="root">
                <${Routes}>
                    <${Route} path="/" element=${html`<${Home} />`} />
                    <${Route} path="/users" element=${html`<${UserList} />`} />
                    <${Route} path="/users/new" element=${html`<${UserCreate} />`} />
                    <${Route} path="/users/:id" element=${html`<${UserDetail} />`} />
                <//>
            </div>
        <//>
    `;
}
