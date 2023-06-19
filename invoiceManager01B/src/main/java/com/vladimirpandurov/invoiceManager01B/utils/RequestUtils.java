package com.vladimirpandurov.invoiceManager01B.utils;

import com.vladimirpandurov.invoiceManager01B.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class RequestUtils {
    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = "Unknown IP";
        if(request != null){
            ipAddress = request.getHeader("X_FORWARDED-FOR");
            if(ipAddress == null || "".equals(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest request) {
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
        UserAgent agent = userAgentAnalyzer.parse(request.getHeader("user-agent"));
        System.out.println(agent);
        return agent.getValue(UserAgent.AGENT_NAME) + " - " + agent.getValue(UserAgent.DEVICE_NAME);
    }


}
