-- 秒杀系统初始化脚本
-- 模块1：库存

CREATE DATABASE IF NOT EXISTS seckill DEFAULT CHARACTER SET utf8mb4;
USE seckill;

-- ============================================================
-- 秒杀商品表
-- 设计决策（面试要讲得出"为什么"）：
--   1. version 字段 = 乐观锁预留：DB 兜底防超卖的第二层（条件更新 + 版本校验）
--   2. 库存用 INT，扣减用"条件更新"（stock > 0），见 SeckillGoodsMapper.deductStock
-- ============================================================
CREATE TABLE IF NOT EXISTS seckill_goods (
     id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    goods_name  VARCHAR(64)  NOT NULL COMMENT '商品名称',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '库存数量',
    version     INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    start_time  DATETIME     NOT NULL COMMENT '秒杀开始时间',
    end_time    DATETIME     NOT NULL COMMENT '秒杀结束时间',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_seckill_time (start_time, end_time)
    ) ENGINE = InnoDB COMMENT = '秒杀商品表';

-- ============================================================
-- 秒杀订单表
-- 设计决策：
--   1. uk_user_goods (user_id, goods_id) 唯一键 = 数据库层面"一人一单"兜底
--      （即使业务层 Redis 预减/分布式锁全失效，DB 唯一键也能挡住重复下单）
--   2. order_no 唯一键 = 幂等（消息重试不会产生重复订单）
--   3. status：0待支付 1已支付 2已取消 3已关闭（超时自动关闭用）
-- ============================================================
CREATE TABLE IF NOT EXISTS seckill_order (
     id          BIGINT      NOT NULL AUTO_INCREMENT,
    order_no    VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    goods_id    BIGINT      NOT NULL COMMENT '商品ID',
    status      TINYINT     NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消 3已关闭',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pay_time    DATETIME    DEFAULT NULL,
    close_time  DATETIME    DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_goods (user_id, goods_id),
    UNIQUE KEY uk_order_no (order_no)
    ) ENGINE = InnoDB COMMENT = '秒杀订单表';

-- 测试数据（秒杀窗口设为长期有效，方便开发调试）
INSERT INTO seckill_goods (goods_name, stock, start_time, end_time)
VALUES ('测试商品A', 100, '2026-08-02 00:00:00', '2026-12-31 23:59:59');
