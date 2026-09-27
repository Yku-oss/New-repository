package com.demo.controller;

import com.demo.server.shortlinkserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException; //如果出错，抛出异常管理

@RestController
public class short_urlcontroller {
    @Autowired
    private shortlinkserver shortLinkService;

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        // 1. 调用 Service 获取长链接
        String longUrl = shortLinkService.getLongUrl(shortCode);
        
        // 2. 如果短码不存在，返回 404
        if (longUrl == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
            return;
        }
        
        // 3. 302 重定向
        response.sendRedirect(longUrl);
    }
}