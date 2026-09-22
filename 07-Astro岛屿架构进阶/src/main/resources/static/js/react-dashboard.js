/**
 * React Dashboard Chart Island
 * 使用纯 SVG 绘制柱状图（无额外图表库依赖）
 */
(function() {
    const { useState, useEffect } = React;

    function DashboardIsland() {
        const [stats, setStats] = useState(null);
        const [chartData, setChartData] = useState(null);

        useEffect(() => {
            Promise.all([
                fetch('/api/stats').then(r => r.json()),
                fetch('/api/users/by-department').then(r => r.json())
            ]).then(([s, deptUsers]) => {
                setStats(s);
                const entries = Object.entries(deptUsers);
                setChartData(entries.map(([dept, users]) => ({
                    label: dept,
                    value: users.length,
                    active: users.filter(u => u.active).length
                })));
            });
        }, []);

        if (!stats || !chartData) return React.createElement('p', null, 'Loading dashboard...');

        const maxValue = Math.max(...chartData.map(d => d.value), 1);
        const barWidth = 80;
        const gap = 40;
        const chartHeight = 200;
        const svgWidth = chartData.length * (barWidth + gap) + gap;
        const svgHeight = chartHeight + 60;

        return React.createElement('div', { className: 'dashboard-island' },
            React.createElement('div', { className: 'chart-container' },
                React.createElement('h4', null, 'Department Distribution'),
                React.createElement('svg', {
                    width: svgWidth,
                    height: svgHeight,
                    className: 'bar-chart'
                },
                    chartData.map((d, i) => {
                        const x = gap + i * (barWidth + gap);
                        const h = (d.value / maxValue) * chartHeight;
                        const y = chartHeight - h;
                        return React.createElement('g', { key: d.label },
                            React.createElement('rect', {
                                x: x, y: y, width: barWidth, height: h,
                                fill: '#6366f1', rx: 4
                            }),
                            React.createElement('rect', {
                                x: x, y: y, width: barWidth, height: h * (d.active / d.value || 0),
                                fill: '#22c55e', rx: 4
                            }),
                            React.createElement('text', {
                                x: x + barWidth / 2, y: chartHeight + 20,
                                textAnchor: 'middle', fill: '#94a3b8', fontSize: '12'
                            }, d.label),
                            React.createElement('text', {
                                x: x + barWidth / 2, y: y - 6,
                                textAnchor: 'middle', fill: '#e2e8f0', fontSize: '14', fontWeight: '600'
                            }, String(d.value))
                        );
                    })
                )
            ),
            React.createElement('div', { className: 'stats-summary' },
                React.createElement('div', { className: 'stat-badge' },
                    React.createElement('span', { className: 'stat-num' }, stats.totalUsers),
                    React.createElement('span', { className: 'stat-name' }, 'Users')
                ),
                React.createElement('div', { className: 'stat-badge' },
                    React.createElement('span', { className: 'stat-num' }, stats.activeUsers),
                    React.createElement('span', { className: 'stat-name' }, 'Active')
                ),
                React.createElement('div', { className: 'stat-badge' },
                    React.createElement('span', { className: 'stat-num' }, stats.totalProducts),
                    React.createElement('span', { className: 'stat-name' }, 'Products')
                ),
                React.createElement('div', { className: 'stat-badge' },
                    React.createElement('span', { className: 'stat-num' }, '¥' + Number(stats.totalValue).toLocaleString()),
                    React.createElement('span', { className: 'stat-name' }, 'Value')
                )
            )
        );
    }

    function mountDashboard() {
        const root = document.getElementById('react-dashboard-root');
        if (root) {
            ReactDOM.createRoot(root).render(React.createElement(DashboardIsland));
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mountDashboard);
    } else {
        mountDashboard();
    }
})();
