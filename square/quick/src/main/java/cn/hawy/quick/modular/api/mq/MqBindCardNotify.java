package cn.hawy.quick.modular.api.mq;

import cn.hawy.quick.config.MqConfig;
import cn.hawy.quick.modular.api.service.TMchCardChannelService;
import cn.hawy.quick.modular.api.service.TMchCashFlowService;
import cn.hawy.quick.modular.api.service.TMchInfoChannelService;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class MqBindCardNotify {

	@Autowired
    private Queue bindCardNotifyQueue;

    @Autowired
    TMchCardChannelService mchCardChannelService;

	@Autowired
	private JmsTemplate jmsTemplate;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void send(String msg) {
		log.info("发送bindCardNotifyQueue消息:msg={}", msg);
        this.jmsTemplate.convertAndSend(this.bindCardNotifyQueue, msg);
    }

	/**
     * 发送延迟消息
     * @param msg
     * @param delay
     */
    public void send(String msg, long delay) {
    	log.info("发送bindCardNotifyQueue消息:msg={},delay={}", msg, delay);
        jmsTemplate.send(this.bindCardNotifyQueue, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                TextMessage tm = session.createTextMessage(msg);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1*1000);
                tm.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 1);
                return tm;
            }
        });
    }

	@JmsListener(destination = MqConfig.BIND_CARD_NOTIFY_QUEUE_NAME)
    public void receive(String msg) {
		log.info("接收bindCardNotifyQueue消息:msg={}", msg);
		JSONObject msgObj = JSON.parseObject(msg);
		String notify_url = msgObj.getString("notifyUrl");
		Integer id = msgObj.getInteger("id");
        int notifyCount = msgObj.getInteger("notifyCount");
        String notifyMsg = msgObj.getString("notifyMsg");
        int cnt = notifyCount+1;
        log.info("发送通知消息:notify_msg={}", notifyMsg);
        String result = HttpRequest.post(notify_url).body(notifyMsg).execute().body();
        log.info("接收通知消息:result={}", result);
        if("success".equals(result)) {
            mchCardChannelService.updateNotifyCount(id, cnt, "success");
        }else {
            mchCardChannelService.updateNotifyCount(id, cnt, "fail");
        	if (cnt > 5) {
        		return;
        	}
        	msgObj.put("notifyCount", cnt);
        	this.send(msgObj.toJSONString(), cnt * 60 * 1000);
        }
	}

}
