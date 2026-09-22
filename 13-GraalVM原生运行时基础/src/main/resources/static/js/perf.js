(function() {
    'use strict';
    var REFRESH_INTERVAL = 5000;

    function fetchMetrics() {
        fetch('/api/perf/metrics')
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (data.memory) {
                    document.getElementById('heapUsed').textContent =
                        data.memory.heapUsedMB.toFixed(1);
                    document.getElementById('totalMem').textContent =
                        data.memory.totalMemoryMB.toFixed(1);
                }
                if (data.startup) {
                    document.getElementById('startupTime').textContent =
                        data.startup.startupTimeMs;
                    document.getElementById('uptime').textContent =
                        data.startup.uptimeMs;
                }
            })
            .catch(function(e) { console.warn('Fetch failed:', e.message); });
    }

    fetchMetrics();
    setInterval(fetchMetrics, REFRESH_INTERVAL);
})();
