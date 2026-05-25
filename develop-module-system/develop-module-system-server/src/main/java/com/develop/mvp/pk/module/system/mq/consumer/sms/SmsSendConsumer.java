package com.develop.mvp.pk.module.system.mq.consumer.sms;

import com.develop.mvp.pk.module.system.application.sms.service.SmsApplicationService;
import com.develop.mvp.pk.module.system.mq.message.sms.SmsSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 针对 {@link SmsSendMessage} 的消费者
 *
 * @author David
 */
@Component
@Slf4j
public class SmsSendConsumer {

    @Resource
    private SmsApplicationService smsSendService;

    @EventListener
    @Async // Spring Event 默认在 Producer 发送的线程，通过 @Async 实现异步
    public void onMessage(SmsSendMessage message) {
        log.info("[onMessage][消息内容({})]", message);
        smsSendService.doSendSms(message);
    }

}
