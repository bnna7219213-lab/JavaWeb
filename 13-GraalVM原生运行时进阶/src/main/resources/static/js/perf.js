(function() {
    'use strict';
    var REFRESH_MS = 3000;

    function fetchMetrics() {
        fetch('/api/perf/metrics')
            .then(function(r) { return r.json(); })
            .then(renderMetrics)
            .catch(function(e) { console.warn('Metrics fetch failed:', e.message); });
    }

    function renderMetrics(data) {
        if (data.startup) {
            document.getElementById('startup-time').textContent =
                Math.round(data.startup.startupTimeMs);
            document.getElementById('uptime').textContent =
                Math.round(data.startup.uptimeMs / 1000);
            setBar('startup-bar', Math.min(data.startup.startupTimeMs / 80, 100));
            setBar('uptime-bar', Math.min(data.startup.uptimeMs / 10000 * 100, 100));
        }

        if (data.memory) {
            document.getElementById('heap-used').textContent =
                data.memory.heapUsedMB.toFixed(0);
            document.getElementById('total-mem').textContent =
                data.memory.totalMemoryMB.toFixed(0);
            setBar('heap-bar', (data.memory.heapUsedMB / 150) * 100);
            setBar('total-bar', (data.memory.totalMemoryMB / 400) * 100);
        }

        if (data.threads) {
            document.getElementById('thread-count').textContent =
                data.threads.threadCount;
            setBar('thread-bar', Math.min(data.threads.threadCount / 50 * 100, 100));
        }

        if (data.gc) {
            var totalGC = data.gc.reduce(function(s, g) { return s + g.collectionCount; }, 0);
            document.getElementById('gc-count').textContent = totalGC;
            setBar('gc-bar', Math.min(totalGC / 10 * 100, 100));
            renderGCGrid(data.gc);
        }

        if (data.memory && data.memory.pools) {
            renderPools(data.memory.pools);
        }

        if (data.runtime) {
            var banner = document.getElementById('mode-banner');
            var mode = document.getElementById('runtime-mode');
            var detail = document.getElementById('runtime-detail');
            if (data.runtime.isNativeImage) {
                banner.className = 'mode-banner mode-native';
                mode.textContent = 'Native Image (GraalVM)';
            } else {
                banner.className = 'mode-banner mode-jvm';
                mode.textContent = 'JVM HotSpot Mode';
            }
            detail.textContent = data.runtime.runtimeMode;
            document.getElementById('java-version').textContent =
                data.runtime.javaVersion;
            document.getElementById('processors').textContent =
                data.runtime.availableProcessors;
        }

        if (data.appStats) {
            document.getElementById('user-count').textContent = data.appStats.userCount;
            document.getElementById('order-count').textContent = data.appStats.orderCount;
        }
    }

    function renderGCGrid(gcList) {
        var grid = document.getElementById('gc-grid');
        if (!grid) return;
        grid.innerHTML = gcList.map(function(g) {
            return '<div class="gc-card">' +
                '<div class="gc-name">' + g.name + '</div>' +
                '<div class="gc-stat">Collections: <span>' + g.collectionCount + '</span></div>' +
                '<div class="gc-stat">Total Time: <span>' + g.collectionTimeMs + ' ms</span></div>' +
                '</div>';
        }).join('');
    }

    function renderPools(pools) {
        var body = document.getElementById('pools-body');
        if (!body) return;
        body.innerHTML = pools.map(function(p) {
            var pct = p.maxMB > 0 ? (p.usedMB / p.maxMB * 100).toFixed(1) : 0;
            return '<tr>' +
                '<td>' + p.name + '</td>' +
                '<td>' + p.type + '</td>' +
                '<td>' + p.usedMB.toFixed(1) + ' MB</td>' +
                '<td>' + (p.maxMB > 0 ? p.maxMB.toFixed(0) + ' MB' : 'unlimited') + '</td>' +
                '<td>' + pct + '%</td>' +
                '</tr>';
        }).join('');
    }

    function setBar(id, pct) {
        var bar = document.getElementById(id);
        if (bar) bar.style.width = Math.min(pct, 100) + '%';
    }

    fetchMetrics();
    setInterval(fetchMetrics, REFRESH_MS);
})();
