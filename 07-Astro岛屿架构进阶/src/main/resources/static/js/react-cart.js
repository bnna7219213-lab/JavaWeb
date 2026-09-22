/**
 * React Shopping Cart Island
 * 复杂的交互状态管理，使用 React 实现
 */
(function() {
    const { useState, useEffect, useCallback } = React;

    const PRODUCTS = [
        { id: 1, name: 'MacBook Pro 14', price: 16999 },
        { id: 2, name: 'iPhone 15 Pro', price: 8999 },
        { id: 3, name: 'AirPods Pro 2', price: 1899 },
        { id: 5, name: 'Dell U2723QE', price: 3899 },
    ];

    function CartIsland() {
        const [items, setItems] = useState([]);
        const [promoCode, setPromoCode] = useState('');
        const [discount, setDiscount] = useState(0);

        const addToCart = useCallback((product) => {
            setItems(prev => {
                const existing = prev.find(i => i.id === product.id);
                if (existing) {
                    return prev.map(i =>
                        i.id === product.id ? { ...i, qty: i.qty + 1 } : i
                    );
                }
                return [...prev, { ...product, qty: 1 }];
            });
        }, []);

        const removeFromCart = useCallback((id) => {
            setItems(prev => prev.filter(i => i.id !== id));
        }, []);

        const updateQty = useCallback((id, qty) => {
            if (qty <= 0) {
                setItems(prev => prev.filter(i => i.id !== id));
            } else {
                setItems(prev => prev.map(i => i.id === id ? { ...i, qty } : i));
            }
        }, []);

        const applyPromo = useCallback(() => {
            if (promoCode.toUpperCase() === 'ASTRO10') {
                setDiscount(0.1);
                alert('Promo applied: 10% off');
            } else if (promoCode.toUpperCase() === 'ISLANDS20') {
                setDiscount(0.2);
                alert('Promo applied: 20% off');
            } else {
                setDiscount(0);
                alert('Invalid promo code');
            }
        }, [promoCode]);

        const subtotal = items.reduce((sum, i) => sum + i.price * i.qty, 0);
        const total = subtotal * (1 - discount);

        return React.createElement('div', { className: 'cart-island' },
            React.createElement('div', { className: 'cart-products' },
                React.createElement('h4', null, 'Available Products'),
                PRODUCTS.map(p =>
                    React.createElement('div', { key: p.id, className: 'cart-product-row' },
                        React.createElement('span', null, p.name),
                        React.createElement('span', { className: 'price' }, '¥' + p.price),
                        React.createElement('button', {
                            onClick: () => addToCart(p),
                            className: 'btn-sm'
                        }, 'Add to Cart')
                    )
                )
            ),
            React.createElement('div', { className: 'cart-items' },
                React.createElement('h4', null, 'Cart (' + items.length + ' items)'),
                items.length === 0
                    ? React.createElement('p', { className: 'empty-cart' }, 'Cart is empty')
                    : items.map(i =>
                        React.createElement('div', { key: i.id, className: 'cart-item' },
                            React.createElement('span', null, i.name),
                            React.createElement('div', { className: 'qty-control' },
                                React.createElement('button', {
                                    onClick: () => updateQty(i.id, i.qty - 1),
                                    className: 'btn-sm'
                                }, '-'),
                                React.createElement('span', { className: 'qty-value' }, i.qty),
                                React.createElement('button', {
                                    onClick: () => updateQty(i.id, i.qty + 1),
                                    className: 'btn-sm'
                                }, '+')
                            ),
                            React.createElement('span', { className: 'price' }, '¥' + (i.price * i.qty)),
                            React.createElement('button', {
                                onClick: () => removeFromCart(i.id),
                                className: 'btn-sm btn-danger'
                            }, 'Remove')
                        )
                    )
            ),
            React.createElement('div', { className: 'cart-summary' },
                React.createElement('div', { className: 'promo-row' },
                    React.createElement('input', {
                        type: 'text',
                        placeholder: 'Promo code (try ASTRO10)',
                        value: promoCode,
                        onChange: e => setPromoCode(e.target.value),
                        className: 'promo-input'
                    }),
                    React.createElement('button', { onClick: applyPromo, className: 'btn-sm' }, 'Apply')
                ),
                React.createElement('div', { className: 'summary-row' },
                    React.createElement('span', null, 'Subtotal:'),
                    React.createElement('span', null, '¥' + subtotal.toFixed(2))
                ),
                discount > 0 && React.createElement('div', { className: 'summary-row discount' },
                    React.createElement('span', null, 'Discount (' + (discount * 100) + '%):'),
                    React.createElement('span', null, '-' + (subtotal * discount).toFixed(2))
                ),
                React.createElement('div', { className: 'summary-row total' },
                    React.createElement('span', null, 'Total:'),
                    React.createElement('span', { className: 'total-price' }, '¥' + total.toFixed(2))
                )
            )
        );
    }

    // Mount when DOM is ready
    function mountCart() {
        const root = document.getElementById('react-cart-root');
        if (root) {
            const rootEl = ReactDOM.createRoot(root);
            rootEl.render(React.createElement(CartIsland));

            // Also mount in shopping cart page root
            const shopRoot = document.getElementById('react-shopping-cart-root');
            if (shopRoot) {
                const shopRootEl = ReactDOM.createRoot(shopRoot);
                shopRootEl.render(React.createElement(CartIsland));
            }
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', mountCart);
    } else {
        mountCart();
    }

    // Expose globally for Thymeleaf inline usage
    window.CartIsland = CartIsland;
})();
