package cn.hawy.quick.modular.api.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hawy.quick.config.MqConfig;
import cn.hawy.quick.modular.api.service.TPayOrderService;
import cn.hutool.http.HttpRequest;

@Component
public class MqPayNotify {
	
	@Autowired
    private Queue payNotifyQueue;
	
	@Autowired
	TPayOrderService payOrderService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void send(String msg) {
		log.info("发送payNotifyQueue消息:msg={}", msg);
        this.jmsTemplate.convertAndSend(this.payNotifyQueue, msg);
    }
	
	/**
     * 发送延迟消息
     * @param msg
     * @param delay
     */
    public void send(String msg, long delay) {
    	log.info("发送payNotifyQueue延时消息:msg={},delay={}", msg, delay);
        jmsTemplate.send(this.payNotifyQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage tm = session.createTextMessage(msg);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1*1000);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 1);
                return tm;
            }
        });
    }
	
	@JmsListener(destination = MqConfig.PAY_NOTIFY_QUEUE_NAME)
    public void receive(String msg) {
		log.info("接收payNotifyQueue消息:msg={}", msg);
		JSONObject msgObj = JSON.parseObject(msg);
		String notify_url = msgObj.getString("notifyUrl");
		Long orderId = msgObj.getLong("orderId");
        int notifyCount = msgObj.getInteger("notifyCount");
        String notifyMsg = msgObj.getString("notifyMsg");
        int cnt = notifyCount+1;
        log.info("发送通知消息:notify_msg={}", notifyMsg);
        String result = HttpRequest.post(notify_url).body(notifyMsg).execute().body();
        log.info("接收通知消息:result={}", result);
        if("success".equals(result)) {
        	payOrderService.updateNotifyCount(orderId, cnt, "success");
        }else {
        	payOrderService.updateNotifyCount(orderId, cnt, "fail");
        	if (cnt > 5) {
        		return;
        	}
        	msgObj.put("notifyCount", cnt);
        	this.send(msgObj.toJSONString(), cnt * 60 * 1000);
        }
	}

}
