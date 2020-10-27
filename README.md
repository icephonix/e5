# E5续订程序
此项目为该网址的源代码(后端) https://e5.qyi.io/user/login 
(前端) https://github.com/luoye663/E5_vue
## 说明
此项目为我的新手练手作，代码辣鸡，目前已经从3月份运行到至今。  
如果要自己搭建的话得自己研究下了，不提供技术支持(懒)，记得修改配置文件 
src/main/resources/application.properties 
```
user.admin.githubId  - 自己的github id  
数据库配置  
redis配置  
Rabbit配置  
github.client_id  
github.client_secret  
(这两个在https://github.com/settings/developers 申请一个apps就行了。)
```
## 注意事项
由于懒癌发作，在程序启动或者重启，是不会主动把数据库里面的用户加入队列，所以得手动处理。
1. 在每次启动程序前，先清空延迟队列  
rabbitmq-plugins disable rabbitmq_delayed_message_exchange
rabbitmq-plugins enable rabbitmq_delayed_message_exchange  
由于这个插件只能先禁用在启用，才能进行清空。
2. 在每次启动程序前，清空未完成的队列。
在rabbitmq web管理界面 - Queues - delay_queue1 - Purge - Purge Messages
3. 启动后清空redis
4. 登录后使用http访问工具访问  https://domain.com/admin/sendAll 这个链接，设置一个token头，为网站登录后的token，f12 看请求(需要设置的管理员github id访问才有能访问)。
##### 如果不按照以上的来，会出现莫名其妙的问题哦，所以不建议自行搭建~

## 用到技术或框架
### spring boot  

### rabbitMq  
需要安装rabbitmq_delayed_message_exchange插件  
同时新建一个用户来对接此程序，由于
### Redis
默认用1库，可自行在配置文件修改  

### Mysql
自行导入sql  
没有写清空日志功能，后面加上。  
按道理说日志因该存到MongoDB里，所以？
### Mybatis Plus

### Spring Security
权限配置由于就那么几个，所以就没写到mysql里面。
### log4j2
日志框架

## 鸣谢

> [IntelliJ IDEA](https://zh.wikipedia.org/zh-hans/IntelliJ_IDEA) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com/?from=) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=) 等 IDE 的授权  
[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/)
