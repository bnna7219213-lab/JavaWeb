import React from 'react';
import htm from 'htm';

// 将 htm 模板引擎绑定到 React 的 createElement，等价于 JSX 的编译效果
export const html = htm.bind(React.createElement);
