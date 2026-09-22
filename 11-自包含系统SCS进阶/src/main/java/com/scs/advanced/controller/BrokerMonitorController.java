package com.scs.advanced.controller;

import com.scs.advanced.messaging.ScsMessageBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 消息中间件监控页
 */
@Controller
@RequestMapping("/broker")
public class BrokerMonitorController {

    @Autowired
    private ScsMessageBroker broker;

    @GetMapping("/")
    public String brokerMonitor(Model model) {
        model.addAttribute("brokerInfo", broker.getBrokerInfo());
        model.addAttribute("topics", broker.getAllTopics());
        model.addAttribute("recentMessages", broker.getRecentMessages(30));
        return "broker/monitor";
    }
}
