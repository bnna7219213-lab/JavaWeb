/**
 * Event Bus - 微前端事件总线
 * 提供子应用之间解耦通信的机制
 * 基于发布-订阅模式，支持命名空间和通配符
 */
export class EventBus {
  constructor() {
    this._listeners = new Map();
    this._logEnabled = true;
  }

  /**
   * 订阅事件
   * @param {string} eventName - 事件名称 (如 "user:selected", "product:created")
   * @param {Function} callback - 回调函数
   * @param {string} subscriber - 订阅者标识 (子应用名)
   * @returns {Function} 取消订阅函数
   */
  on(eventName, callback, subscriber) {
    if (!subscriber) subscriber = 'anonymous';
    if (!this._listeners.has(eventName)) {
      this._listeners.set(eventName, []);
    }

    var handler = { callback: callback, subscriber: subscriber, eventName: eventName };
    this._listeners.get(eventName).push(handler);

    this._log('SUBSCRIBE', subscriber + ' -> ' + eventName);

    // 返回取消订阅函数
    var self = this;
    return function() { self.off(eventName, callback); };
  }

  /**
   * 一次性订阅 - 事件触发后自动移除
   */
  once(eventName, callback, subscriber) {
    if (!subscriber) subscriber = 'anonymous';
    var self = this;
    var unsubscribe = this.on(eventName, function(data) {
      unsubscribe();
      callback(data);
    }, subscriber);
    return unsubscribe;
  }

  /**
   * 取消订阅
   */
  off(eventName, callback, subscriber) {
    if (!subscriber) subscriber = null;
    var handlers = this._listeners.get(eventName);
    if (!handlers) return;

    var filtered = [];
    for (var i = 0; i < handlers.length; i++) {
      var h = handlers[i];
      if (callback && h.callback !== callback) { filtered.push(h); continue; }
      if (subscriber && h.subscriber !== subscriber) { filtered.push(h); continue; }
    }

    if (filtered.length === 0) {
      this._listeners.delete(eventName);
    } else {
      this._listeners.set(eventName, filtered);
    }
  }

  /**
   * 取消某个订阅者的所有订阅
   */
  offAll(subscriber) {
    var events = [];
    for (var key of this._listeners.keys()) {
      events.push(key);
    }
    for (var i = 0; i < events.length; i++) {
      this.off(events[i], null, subscriber);
    }
    this._log('OFF_ALL', 'Unsubscribed all for ' + subscriber);
  }

  /**
   * 发布事件
   */
  emit(eventName, data, publisher) {
    if (!publisher) publisher = 'system';
    var handlers = this._listeners.get(eventName) || [];

    this._log('EMIT', publisher + ' -> ' + eventName, data);

    for (var i = 0; i < handlers.length; i++) {
      try {
        handlers[i].callback(data, { eventName: eventName, publisher: publisher, subscriber: handlers[i].subscriber });
      } catch (err) {
        console.error('[EventBus] Handler error for ' + eventName + ' (sub: ' + handlers[i].subscriber + '):', err);
      }
    }
  }

  /**
   * 获取当前注册的监听器状态
   */
  getStatus() {
    var status = {};
    for (var entry of this._listeners) {
      var subs = [];
      for (var i = 0; i < entry[1].length; i++) {
        subs.push(entry[1][i].subscriber);
      }
      status[entry[0]] = subs;
    }
    return status;
  }

  _log(action, msg, data) {
    if (!this._logEnabled) return;
    var time = new Date().toLocaleTimeString();
    console.log('[EventBus ' + time + '] ' + action + ': ' + msg, data || '');

    // 通知 Host App 的事件日志面板
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new CustomEvent('eventbus-log', {
        detail: { time: time, action: action, msg: msg, data: data }
      }));
    }
  }

  clear() {
    this._listeners.clear();
    this._log('CLEAR', 'All listeners removed');
  }
}

// 全局单例 - 所有子应用共享同一个事件总线
export var eventBus = new EventBus();
