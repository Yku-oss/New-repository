package com.demo.server;


public interface shortlinkserver {
     /**
     * 根据短码获取长链接
     * @param shortCode 短码
     * @return 原始长链接
     */

    String getLongUrl(String shortCode);
    
} 
