package com.example.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 璁㈠崟瓒呮椂鑷姩鍙栨秷 鈥斺€?MQ 閰嶇疆锛堝欢杩熼槦鍒楁柟妗堬級
 *
 * ========== 杩欎釜绫绘槸骞插槢鐨?==========
 *   鐢ㄦ埛涓嬪崟鍚?30 鍒嗛挓鏈敮浠?鈫?鑷姩鍙栨秷璁㈠崟 + 鍥炶ˉ搴撳瓨
 *
 * ========== 鏍稿績鎬濊矾锛歍TL + 姝讳俊闃熷垪 = 寤惰繜闃熷垪 ==========
 *   娑堟伅涓嶆槸"娌℃秷璐硅€?灏变竴鐩寸暀鐫€鍚楋紵瀵癸紝鎴戜滑灏卞埄鐢ㄨ繖涓€鐐癸細
 *   璁╁畠鍦ㄤ竴涓€愭病鏈夋秷璐硅€呫€戠殑闃熷垪閲岃汉 30 鍒嗛挓锛岃汉鍒?TTL 杩囨湡锛?
 *   鑷姩鍙樻垚姝讳俊锛岃璺敱鍒扮湡姝ｅ共娲荤殑闃熷垪銆?
 *
 * ========== 娑堟伅娴佽浆 ==========
 *
 *   鐢熶骇鑰咃紙涓嬪崟鎴愬姛鏃讹級
 *     鈫?鍙戝埌 seckill.delay.exchange锛宺outingKey = delay.routingkey
 *   seckill.order.delay.queue锛堝欢杩熼槦鍒楋紝TTL=30min锛屻€愭棤娑堣垂鑰呫€戯級
 *     鈫?韬烘弧 30 鍒嗛挓锛屾秷鎭繃鏈?鈫?鍙樻淇?
 *   seckill.dlx锛堟淇′氦鎹㈡満锛屻€愬鐢?RabbitMQConfig 閲岀殑閭ｄ釜銆戯級
 *     鈫?routingKey = timeout.routingkey
 *   seckill.order.timeout.queue锛堝鐞嗛槦鍒楋紝銆愭湁娑堣垂鑰呫€戯級
 *     鈫?
 *   OrderTimeoutConsumer锛氭煡璁㈠崟 鈫?鏈敮浠樺垯鍙栨秷 + 鍥炶ˉ搴撳瓨
 *
 * ========== 澶嶇敤瑙勫垯锛堝叧閿級 ==========
 *   鉁?seckill.dlx 澶嶇敤 RabbitMQConfig 鐨勶細浜ゆ崲鏈烘槸"鍒嗘嫞涓績"锛屽ぉ鐢熷叕鐢?
 *   鉂?闃熷垪涓嶅鐢細姣忎釜闃熷垪鏄竴涓粓鐐癸紝鐢ㄩ€斾笉鍚屽繀椤诲垎寮€
 *      路 seckill.order.dlq           = 娑堣垂澶辫触鐨勬秷鎭紙RabbitMQConfig锛?
 *      路 seckill.order.timeout.queue = 瓒呮椂杩囨湡鐨勬秷鎭紙鏈被锛?
 *
 * ========== 涓轰粈涔堝崟鐙竴涓被锛?==========
 *   "璁㈠崟鍓婂嘲 + 澶辫触姝讳俊"锛圧abbitMQConfig锛夊拰"瓒呮椂鑷姩鍙栨秷"锛堟湰绫伙級
 *   鏄袱鏉＄嫭绔嬬殑涓氬姟閾捐矾锛屽垎寮€鏇存竻鏅帮紝閬垮厤涓€涓厤缃枃浠惰秺鍫嗚秺涔便€?
 */
@Configuration
public class OrderTimeoutMQConfig {

    // ==================== 闃熷垪鍚?====================

    /** 寤惰繜闃熷垪锛氭秷鎭湪杩欓噷韬?30 鍒嗛挓绛夎繃鏈燂紝銆愭病鏈夋秷璐硅€呫€?*/
    public static final String SECKILL_ORDER_DELAY_QUEUE = "seckill.order.delay.queue";

    /** 瓒呮椂澶勭悊闃熷垪锛氭敹鍒拌繃鏈熸秷鎭紝銆愭湁娑堣垂鑰呫€戣礋璐ｅ彇娑堣鍗?*/
    public static final String SECKILL_ORDER_TIMEOUT_QUEUE = "seckill.order.timeout.queue";

    // ==================== 浜ゆ崲鏈哄悕 ====================

    /**
     * 寤惰繜娑堟伅鍏ュ彛浜ゆ崲鏈?
     * 涓轰粈涔堜笉鑳藉鐢?seckill.dlx锛?
     *   鍥犱负鍏ュ彛涓嶅悓锛氱敓浜ц€呭彂寤惰繜娑堟伅鏃讹紝涓嶈兘鍙戝埌"姝讳俊浜ゆ崲鏈?锛堣涔夐敊涔憋級
     */
    public static final String SECKILL_DELAY_EXCHANGE = "seckill.delay.exchange";

    // ==================== routingKey ====================

    /** 寤惰繜闃熷垪鍏ュ彛鐨勮矾鐢遍敭锛堢敓浜ц€呯敤锛?*/
    public static final String SECKILL_ORDER_DELAY_ROUTING_KEY = "seckill.order.delay.routingkey";

    /** 瓒呮椂姝讳俊鐨勮矾鐢遍敭锛堝欢杩熼槦鍒楄繃鏈熷悗锛岃矾鐢卞埌澶勭悊闃熷垪鐢級 */
    public static final String SECKILL_ORDER_TIMEOUT_ROUTING_KEY = "seckill.order.timeout.routingkey";

    /** 璁㈠崟瓒呮椂鏃堕棿锛?0 鍒嗛挓锛堟绉掞級 */
    public static final int ORDER_TIMEOUT_MS = 30 * 60 * 1000;

    // ==================== 浜ゆ崲鏈?====================

    /**
     * 寤惰繜娑堟伅鍏ュ彛浜ゆ崲鏈猴紙DirectExchange锛?
     * 鐢熶骇鑰呮妸寤惰繜娑堟伅鍙戝埌杩欓噷锛宺outingKey = delay.routingkey
     */
    @Bean
    public DirectExchange seckillDelayExchange() {
        return new DirectExchange(SECKILL_DELAY_EXCHANGE, true, false);
    }

    // ==================== 闃熷垪 ====================

    /**
     * 猸?寤惰繜闃熷垪锛堝畾鏃堕槦鍒楋級鈥斺€?鏈柟妗堢殑鏍稿績
     *
     * 涓変釜鍏抽敭鍙傛暟锛?
     *   1. x-message-ttl             = 1800000ms锛?0鍒嗛挓锛夛紝娑堟伅韬哄涔呰繃鏈?
     *   2. x-dead-letter-exchange    = seckill.dlx锛堣繃鏈熷悗鍘绘淇′氦鎹㈡満锛屻€愬鐢ㄣ€戯級
     *   3. x-dead-letter-routing-key = timeout.routingkey锛堢敤杩欎釜鍦板潃璺敱鍒板鐞嗛槦鍒楋級
     *
     * 鈿狅笍 杩欎釜闃熷垪銆愭病鏈夋秷璐硅€呫€戯紒瀹冨氨鏄敤鏉?鎼佺疆娑堟伅 30 鍒嗛挓"鐨?
     */
    @Bean
    public Queue seckillOrderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", ORDER_TIMEOUT_MS);
        args.put("x-dead-letter-exchange", RabbitMQConfig.SECKILL_DLX);
        args.put("x-dead-letter-routing-key", SECKILL_ORDER_TIMEOUT_ROUTING_KEY);
        return new Queue(SECKILL_ORDER_DELAY_QUEUE, true, false, false, args);
    }

    /**
     * 瓒呮椂澶勭悊闃熷垪 鈥斺€?杩囨湡娑堟伅鐨勭粓鐐癸紝銆愭湁娑堣垂鑰呫€?
     * 鏅€氶槦鍒楋紝涓嶉渶瑕佹淇″弬鏁帮紙瀹冧笉鍋氬畾鏃讹紝鍙礋璐ｅ鐞嗭級
     */
    @Bean
    public Queue seckillOrderTimeoutQueue() {
        return new Queue(SECKILL_ORDER_TIMEOUT_QUEUE, true);
    }

    // ==================== 缁戝畾 ====================
    // 缁戝畾 = "浜ゆ崲鏈烘敹鍒?routingKey 涓?X 鐨勬秷鎭紝灏辨姇缁欏摢涓槦鍒?

    /**
     * 猸?寤惰繜闃熷垪 鈫?寤惰繜浜ゆ崲鏈?
     * 鐢熶骇鑰呭彂娑堟伅鍒?seckill.delay.exchange锛宺outingKey = delay.routingkey
     * 鈫?璺敱杩?seckill.order.delay.queue
     */
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(seckillOrderDelayQueue())
                .to(seckillDelayExchange())
                .with(SECKILL_ORDER_DELAY_ROUTING_KEY);
    }

    /**
     * 猸?瓒呮椂澶勭悊闃熷垪 鈫?seckill.dlx锛堛€愬鐢ㄣ€慠abbitMQConfig 閲岀殑姝讳俊浜ゆ崲鏈猴級
     *
     * 寤惰繜闃熷垪杩囨湡鍚?鈫?seckill.dlx锛宺outingKey = timeout.routingkey
     * 鈫?璺敱杩?seckill.order.timeout.queue
     *
     * 瀵规瘮 RabbitMQConfig.dlqBinding锛氬悓鏍锋槸 seckill.dlx锛屼絾鍥犱负 routingKey 涓嶅悓锛?
     * 娑堟伅琚垎鍙戝埌涓嶅悓闃熷垪 鈥斺€?杩欏氨鏄?浜ゆ崲鏈轰竴瀵瑰"鐨勫疄鐜版柟寮?
     *
     * 娉ㄦ剰锛歴eckillDlx 浣滀负銆愭柟娉曞弬鏁般€戠敱 Spring 娉ㄥ叆锛堝鍣ㄩ噷宸叉湁涓€涓級锛?
     *       涓嶈兘鍦ㄦ湰绫诲啀 new 涓€涓?鈥斺€?鍚﹀垯鍚屼竴涓氦鎹㈡満琚０鏄庝袱娆′細鍐茬獊
     */
    @Bean
    public Binding timeoutBinding(DirectExchange seckillDlx) {
        return BindingBuilder.bind(seckillOrderTimeoutQueue())
                .to(seckillDlx)
                .with(SECKILL_ORDER_TIMEOUT_ROUTING_KEY);
    }
}
