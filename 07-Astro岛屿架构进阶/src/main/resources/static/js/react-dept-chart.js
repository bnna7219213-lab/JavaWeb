/**
 * React Department Distribution Chart Island
 * 用 SVG 绘制部门分布饼图
 */
(function() {
    const { useState, useEffect } = React;

    function DeptChartIsland() {
        const [deptData, setDeptData] = useState(null);

        useEffect(() => {
            fetch('/api/users/by-department')
                .then(r => r.json())
                .then(data => {
                    const entries = Object.entries(data);
                    const colors = ['#6366f1', '#22c55e', '#f59e0b', '#ef4444', '#77d3fc'];
                    const total = entries.reduce((s, [, users]) => s + users.length, 0);
                    setDeptData(entries.map(([dept, users], i) => ({
                        label: dept,
                        value: users.length,
                        pct: ((users.length / total) * 100),
                        color: colors[i % colors.length]
                    })));
                });
        }, []);

        if (!deptData) return React.createElement('p', null, 'Loading chart...');

        const radius = 80;
        const cx = 100;
        const cy = 100;
        let cumAngle = 0;
        const total = deptData.reduce((s, d) => s + d.value, 0);

        const slices = deptData.map(d => {
            const angle = (d.value / total) * 2 * Math.PI;
            const startAngle = cumAngle;
            cumAngle += angle;
            const endAngle = cumAngle;
            const largeArc = angle > Math.PI ? 1 : 0;
            const x1 = cx + radius * Math.cos(startAngle);
            const y1 = cy + radius * Math.sin(startAngle);
            const x2 = cx + radius * Math.cos(endAngle);
            const y2 = cy + radius * Math.sin(endAngle);
            const path = 'M ' + cx + ' ' + cy + ' L ' + x1 + ' ' + y1 + ' A ' + radius + ' ' + radius + ' 0 ' + largeArc + ' 1 ' + x2 + ' ' + y2 + ' Z';
            return { ...d, path };
        });

        return React.createElement('div', { className: 'dept-chart-island' },
            React.createElement('div', { className: 'pie-container' },
                React.createElement('svg', { width: 200, height: 200 },
                    slices.map(s =>
                        React.createElement('path', {
                            key: s.label,
                            d: s.path, fill: s.color, opacity: 0.85,
                            stroke: '#0f172a', strokeWidth: 2
                        })
                    )
                )
            ),
            React.createElement('div', { className: 'legend' },
                slices.map(s =>
                    React.createElement('div', { key: s.label, className: 'legend-item' },
                        React.createElement('span', { className: 'legend-dot', style: { background: s.color } }),
                        React.createElement('span', null, s.label + ' (' + s.value + ', ' + s.pct.toFixed(1) + '%)')
                    )
                )
            )
        );
    }

    function mountChart() {
        const root = document.getElementById('react-chart-root');
        if (root) {
            ReactDOM.createRoot(root).render(React.createElement(DeptChartIsland));
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mountChart);
    } else {
        mountChart();
    }
})();
