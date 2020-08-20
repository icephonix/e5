package io.qyi.e5.service.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.outlook_log.service.IOutlookLogService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-16 16:53
 **/
@Service
public class TaskImpl implements ITask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IOutlookService outlookService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IOutlookLogService outlookLogService;

    @Override
    @Async
    public void sendTaskOutlookMQ(int github_id) {
        Outlook Outlook = outlookService.getOne(new QueryWrapper<Outlook>().eq("github_id", github_id));
        if (Outlook == null) {
            logger.warn("未找到此用户,github_id: {}", github_id);
            return;
        }
        /*根据用户设置生成随机数*/
        int Expiration = getRandom(Outlook.getCronTimeRandomStart(), Outlook.getCronTimeRandomEnd());
        /*将此用户信息加入redis，如果存在则代表在队列中，同时提前10秒过期*/
        if (!redisUtil.hasKey("user.mq:" + github_id)) {
            redisUtil.set("user.mq:" + github_id, 0, Expiration - 10);
            send(github_id, Expiration* 1000);
        }
    }

    @Override
    @Async
    public void sendTaskOutlookMQALL() {
        List<Outlook> all = outlookService.findAll();
        Iterator<Outlook> iterator = all.iterator();
        while (iterator.hasNext()) {
            Outlook next = iterator.next();
            /*根据用户设置生成随机数*/
            int Expiration = getRandom(next.getCronTimeRandomStart(), next.getCronTimeRandomEnd());
            /*将此用户信息加入redis，如果存在则代表在队列中，同时提前10秒过期*/
            if (!redisUtil.hasKey("user.mq:" + next.getGithubId())) {
                redisUtil.set("user.mq:" + next.getGithubId(), 0, Expiration - 10);
                send(next.getGithubId(), Expiration * 1000);
            }
        }
    }

    @Override
    public boolean executeE5(int github_id) {
        Outlook Outlook = outlookService.getOne(new QueryWrapper<Outlook>().eq("github_id", github_id));
        if (Outlook == null) {
            logger.warn("未找到此用户,github_id: {}", github_id);
            return false;
        }
        boolean mailList = outlookService.getMailList(Outlook);
        if (!mailList) {
            outlookLogService.addLog(github_id, "error", 0, "检测到错误，下次将不再自动调用，请修正错误后再授权开启续订。" );
        }
        return mailList;
    }

    /**
     * 发送消息到队列
     *
     * @param Expiration
     * @Description:
     * @param: * @param msg
     * @return: void
     * @Author: 落叶随风
     * @Date: 2020/4/16
     */
    public void send(Object msg, int Expiration) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("delay", "delay", msg, message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            // 设置这条消息的过期时间
//            messageProperties.setExpiration(Expiration);

            messageProperties.setHeader("x-delay", Expiration);
            return message;
        }, correlationData);
    }

    /**
     * 生成随机数
     *
     * @param end
     * @Description:
     * @param: * @param start
     * @return: java.lang.String
     * @Author: 落叶随风
     * @Date: 2020/4/16
     */
    public int getRandom(int start, int end) {
        Random r = new Random();
        int Expiration = (r.nextInt(end - start + 1) + start);
        return Expiration;
    }
}
