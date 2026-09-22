/**
 * Alpine.js Sort/Filter Island
 * 产品排序与筛选
 */
document.addEventListener('alpine:init', () => {
    Alpine.data('sortIsland', () => ({
        products: [],
        sortBy: 'name',
        categoryFilter: '',

        get sortedProducts() {
            let result = this.categoryFilter
                ? this.products.filter(p => p.category === this.categoryFilter)
                : [...this.products];
            switch(this.sortBy) {
                case 'price-low':  return result.sort((a, b) => Number(a.price) - Number(b.price));
                case 'price-high': return result.sort((a, b) => Number(b.price) - Number(a.price));
                case 'rating':      return result.sort((a, b) => Number(b.rating) - Number(a.rating));
                case 'stock':       return result.sort((a, b) => b.stock - a.stock);
                default:            return result.sort((a, b) => a.name.localeCompare(b.name));
            }
        },

        init() {
            console.log('%c[Alpine Island] Sort island hydrated', 'color: #77d3fc');
            fetch('/api/products')
                .then(r => r.json())
                .then(data => { this.products = data; })
                .catch(err => console.error('Failed to load products:', err));
        }
    }));
});
