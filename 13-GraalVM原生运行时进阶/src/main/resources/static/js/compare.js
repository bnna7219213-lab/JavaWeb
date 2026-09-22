(function() {
    'use strict';

    function loadComparison() {
        fetch('/api/perf/comparison')
            .then(function(r) { return r.json(); })
            .then(function(data) {
                var section = document.getElementById('comparison-data');
                if (section) {
                    section.textContent = JSON.stringify(data, null, 2);
                }
            })
            .catch(function(e) { console.warn('Comparison fetch failed:', e.message); });
    }

    document.addEventListener('DOMContentLoaded', loadComparison);
})();
