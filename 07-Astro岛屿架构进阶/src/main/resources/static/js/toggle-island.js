/**
 * Alpine.js User Toggle Island
 * 用户状态批量切换岛屿
 */
document.addEventListener('alpine:init', () => {
    Alpine.data('userToggleIsland', () => ({
        allUsers: [],
        activeCount: 0,

        init() {
            console.log('%c[Alpine Island] Toggle island hydrated', 'color: #77d3fc');
            fetch('/api/users')
                .then(r => r.json())
                .then(data => {
                    this.allUsers = data;
                    this.activeCount = data.filter(u => u.active).length;
                })
                .catch(err => console.error('Failed to load users:', err));
        },

        toggleUser(id) {
            const user = this.allUsers.find(u => u.id === id);
            if (user) {
                user.active = !user.active;
                this.activeCount = this.allUsers.filter(u => u.active).length;
            }
        },

        toggleAll() {
            const allActive = this.activeCount === this.allUsers.length;
            this.allUsers.forEach(u => { u.active = !allActive; });
            this.activeCount = allActive ? 0 : this.allUsers.length;
        }
    }));
});
