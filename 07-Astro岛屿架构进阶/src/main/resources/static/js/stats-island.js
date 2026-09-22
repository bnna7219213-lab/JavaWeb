/**
 * Alpine.js Live Stats Refresh Island
 * 实时统计数据的按钮刷新
 */
document.addEventListener('alpine:init', () => {
    Alpine.data('statsIsland', () => ({
        stats: null,
        loading: false,

        init() {
            console.log('%c[Alpine Island] Stats island hydrated', 'color: #77d3fc');
            this.refreshStats();
        },

        refreshStats() {
            this.loading = true;
            fetch('/api/stats')
                .then(r => r.json())
                .then(data => { this.stats = data; this.loading = false; })
                .catch(() => { this.loading = false; });
        }
    }));
});
