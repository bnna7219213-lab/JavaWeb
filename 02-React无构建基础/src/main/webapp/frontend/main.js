import { createRoot } from 'react-dom/client';
import { html } from './utils/react-html.js';
import App from './App.js';

const root = createRoot(document.getElementById('root'));
root.render(html`<${App} />`);
