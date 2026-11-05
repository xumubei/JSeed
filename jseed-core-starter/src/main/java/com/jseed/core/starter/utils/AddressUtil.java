package com.jseed.core.starter.utils;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jseed.core.starter.okhttp.OkBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Address lookup utility
 *
 * @author cyrus
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtil {

    // IP address lookup
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    /**
     * Unknown address
     */
    public static final String UNKNOWN = "XX XX";

    /**
     * Looks up the physical location of an IP address.
     *
     * @param ip ip
     * @return
     */
    public static String getRealAddressByIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return UNKNOWN;
        }
        // Skip lookups for private IP addresses.
        ip = "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip.replaceAll("(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)", "");
        if (NetUtil.isInnerIP(ip)) {
            return "内网IP";
        }
        try {
            String rspStr = OkBuilder.url(IP_URL)
                    .params("ip", ip)
                    .params("json", true)
                    .disableLog()
                    .execute();
            if (StringUtils.isEmpty(rspStr)) {
                return UNKNOWN;
            }
            JSONObject obj = JSON.parseObject(rspStr);
            String region = obj.getString("pro");
            String city = obj.getString("city");
            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return UNKNOWN;
    }
}
