/**
 * Alpine.js Search Island
 * 客户端搜索岛屿，调取 /api/users API
 */
document.addEventListener('alpine:init', () => {
    Alpine.data('searchIsland', () => ({
        keyword: '',
        results: [],
        loading: false,

        init() {
            console.log('%c[Alpine Island] Search island hydrated', 'color: #77d3fc');
        },

        search() {
            if (!this.keyword.trim()) { this.results = []; return; }
            this.loading = true;
            fetch('/api/users/search?keyword=' + encodeURIComponent(this.keyword))
                .then(r => r.json())
                .then(data => { this.results = data; this.loading = false; })
                .catch(() => { this.loading = false; });
        }
    }));
});
