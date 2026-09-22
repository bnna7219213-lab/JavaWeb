import { html } from './utils/react-html.js';
import { HashRouter, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home.js';
import UserList from './pages/UserList.js';
import UserDetail from './pages/UserDetail.js';
import UserForm from './pages/UserForm.js';

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
            <div id="app">
                <${Routes}>
                    <${Route} path="/" element=${html`<${Home} />`} />
                    <${Route} path="/users" element=${html`<${UserList} />`} />
                    <${Route} path="/users/new" element=${html`<${UserForm} />`} />
                    <${Route} path="/users/:id/edit" element=${html`<${UserForm} />`} />
                    <${Route} path="/users/:id" element=${html`<${UserDetail} />`} />
                <//>
            </div>
        <//>
    `;
}
