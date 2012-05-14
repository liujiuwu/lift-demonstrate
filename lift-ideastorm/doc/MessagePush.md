基于Comet的消息推送
======================

*使用lift comet实现从服务端实时推送消息到客户端*


目标
----

 - 监控系统：后台硬件热插拔、LED、温度、电压发生变化；
 - 即时通信系统：其它用户登录、发送信息；
 - 即时报价系统：后台数据库内容发生变化；


方案
----




实现
----

*** 使用lift comet做前端消息推送，akka 2做后端服务 ***

具体到当前应用，案例情况如下。客户需要实时看到3个数值:
 
 1. 当前未读消息数 (消息)
 2. 当前未办理事项数 (待办)
 3. 当前未处理紧急事项数 (催办)


**步骤**

 - 可在template-hidden/default.html 为统一设置一个 lift:comet?type=xxx，将负责与服务器建立comet连接并等待服务器推送数据。
 - 基于Akka 2 建立一个系统服务ContextSystem，统一处理消息的收，发。
 - 使用CometActor的localSetup和localShutdown方法注册/取消对ContextSystem的监听
 - 当ContextSystem收到新的消息时选择路由到对应的CometActor
 - 由CometActor将消息推送到客户端

