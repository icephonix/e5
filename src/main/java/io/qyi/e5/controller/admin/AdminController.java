package io.qyi.e5.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import io.qyi.e5.outlook.entity.Outlook;
import io.qyi.e5.outlook.service.IOutlookService;
import io.qyi.e5.service.task.ITask;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-03-16 01:01
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    IOutlookService outlookService;

    @Autowired
    ITask Task;

    @Autowired
    RedisUtil redisUtil;

    @GetMapping("/send")
    public void send() {
        Task.sendTaskOutlookMQ(19658189);
    }

    @GetMapping("/sendAll")
    public String sendAll() {
        Task.sendTaskOutlookMQALL();
        return "ok";
    }

    @GetMapping("/emptyRedis")
    public String emptyRedis() {
        redisUtil.deleteALL();
        return "ok";
    }

    @GetMapping("/test")
    public String test() {
        return "ok";
    }



}
