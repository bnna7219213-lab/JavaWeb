/**
 * Alpine.js Multi-Filter Island
 * 多条件过滤 + 组合搜索
 */
document.addEventListener('alpine:init', () => {
    Alpine.data('filterIsland', () => ({
        allUsers: [],
        filters: { keyword: '', department: '', status: '' },

        get filteredUsers() {
            return this.allUsers.filter(u => {
                const k = this.filters.keyword.toLowerCase();
                const matchKey = !k || u.name.toLowerCase().includes(k) || u.email.toLowerCase().includes(k);
                const matchDept = !this.filters.department || u.department === this.filters.department;
                const matchStatus = !this.filters.status ||
                    (this.filters.status === 'active' ? u.active : !u.active);
                return matchKey && matchDept && matchStatus;
            });
        },

        init() {
            console.log('%c[Alpine Island] Filter island hydrated', 'color: #77d3fc');
            fetch('/api/users')
                .then(r => r.json())
                .then(data => { this.allUsers = data; })
                .catch(err => console.error('Failed to load users:', err));
        }
    }));
});
