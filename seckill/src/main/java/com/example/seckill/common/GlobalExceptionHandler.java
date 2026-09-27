package com.example.seckill.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（面试 Q5 落地）
 * - 业务异常：给明确提示
 * - DuplicateKeyException：一人一单的唯一键兜底触发 → "每人限购一件"
 * - 兜底异常：返回"系统繁忙"，不泄露堆栈
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        // 事务已回滚（包括库存扣减），返回明确提示
        return Result.error(400, "每人限购一件");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        return Result.error(500, "系统繁忙");
    }
}
