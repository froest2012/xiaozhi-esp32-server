package xiaozhi.modules.xin.config;

/**
 * Redis Key 常量类
 * Copyright (c) 人人开源 All rights reserved.
 * Website: https://www.renren.io
 */
public class XinRedisKeys {

    /**
     * 获取风险词列表缓存key
     */
    public static String getRiskKeywordsKey() {
        return "risk:list";
    }

}
