/**
 * React User Table Island
 * 带分页、排序的详细表格组件
 */
(function() {
    const { useState, useEffect, useMemo } = React;

    function UserTableIsland() {
        const [users, setUsers] = useState([]);
        const [loading, setLoading] = useState(true);
        const [page, setPage] = useState(1);
        const [sortField, setSortField] = useState('id');
        const [sortDir, setSortDir] = useState('asc');
        const [filterRole, setFilterRole] = useState('');
        const perPage = 5;

        useEffect(() => {
            fetch('/api/users')
                .then(r => r.json())
                .then(data => { setUsers(data); setLoading(false); })
                .catch(() => setLoading(false));
        }, []);

        const roles = [...new Set(users.map(u => u.role))];

        const processed = useMemo(() => {
            let result = filterRole
                ? users.filter(u => u.role === filterRole)
                : [...users];
            result.sort((a, b) => {
                const va = a[sortField] || '';
                const vb = b[sortField] || '';
                const cmp = String(va).localeCompare(String(vb));
                return sortDir === 'asc' ? cmp : -cmp;
            });
            return result;
        }, [users, sortField, sortDir, filterRole]);

        const totalPages = Math.ceil(processed.length / perPage);
        const pageData = processed.slice((page - 1) * perPage, page * perPage);

        const handleSort = (field) => {
            if (sortField === field) {
                setSortDir(d => d === 'asc' ? 'desc' : 'asc');
            } else {
                setSortField(field);
                setSortDir('asc');
            }
        };

        if (loading) return React.createElement('p', null, 'Loading users...');

        return React.createElement('div', { className: 'react-table-island' },
            React.createElement('div', { className: 'table-controls' },
                React.createElement('label', null, 'Role: '),
                React.createElement('select', {
                    value: filterRole,
                    onChange: e => { setFilterRole(e.target.value); setPage(1); }
                },
                    React.createElement('option', { value: '' }, 'All roles'),
                    roles.map(r => React.createElement('option', { key: r, value: r }, r))
                )
            ),
            React.createElement('table', { className: 'react-data-table' },
                React.createElement('thead', null,
                    React.createElement('tr', null,
                        ['ID', 'Name', 'Email', 'Department', 'Role'].map(h =>
                            React.createElement('th', {
                                key: h,
                                onClick: () => handleSort(h.toLowerCase()),
                                className: 'sortable-header'
                            }, h + (sortField === h.toLowerCase() ? (sortDir === 'asc' ? ' ' +  String(0x2191) : ' ' + String(0x2193)) : ''))
                        )
                    )
                ),
                React.createElement('tbody', null,
                    pageData.map(u =>
                        React.createElement('tr', { key: u.id },
                            React.createElement('td', null, u.id),
                            React.createElement('td', null, u.name),
                            React.createElement('td', null, u.email),
                            React.createElement('td', null, u.department || '-'),
                            React.createElement('td', null, u.role)
                        )
                    )
                )
            ),
            React.createElement('div', { className: 'pagination' },
                React.createElement('button', {
                    disabled: page <= 1,
                    onClick: () => setPage(p => Math.max(1, p - 1))
                }, 'Prev'),
                React.createElement('span', null, ' Page ' + page + ' / ' + totalPages + ' '),
                React.createElement('button', {
                    disabled: page >= totalPages,
                    onClick: () => setPage(p => Math.min(totalPages, p + 1))
                }, 'Next')
            )
        );
    }

    function mountUserTable() {
        const root = document.getElementById('react-user-table-root');
        if (root) {
            ReactDOM.createRoot(root).render(React.createElement(UserTableIsland));
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mountUserTable);
    } else {
        mountUserTable();
    }

    window.UserTableIsland = UserTableIsland;
})();
