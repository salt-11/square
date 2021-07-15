package cn.hawy.quick.partner.config;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description:
 * @author dingzhiwei jmdhappy@126.com
 * @date 2017-07-05
 * @version V1.0
 * @Copyright: www.xxpay.org
 */
@Configuration
public class MqConfig {

    public static final String PAY_NOTIFY_QUEUE_NAME = "quick.pay.notify.queue";

    public static final String MCH_CASH_NOTIFY_QUEUE_NAME = "quick.mch.cash.notify.queue";

    public static final String BIND_CARD_NOTIFY_QUEUE_NAME = "quick.bind.card.notify.queue";


    @Bean
    public Queue payNotifyQueue() {
        return new ActiveMQQueue(PAY_NOTIFY_QUEUE_NAME);
    }

    @Bean
    public Queue mchCashNotifyQueue() {
        return new ActiveMQQueue(MCH_CASH_NOTIFY_QUEUE_NAME);
    }

    @Bean
    public Queue bindCardNotifyQueue() {
        return new ActiveMQQueue(BIND_CARD_NOTIFY_QUEUE_NAME);
    }


}
