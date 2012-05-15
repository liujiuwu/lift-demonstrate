基于Comet的消息推送 (1.0-SNAPSHOT)
====================================

***使用lift comet实现从服务端实时推送消息到客户端***


需求
----

 - 监控系统：后台硬件热插拔、LED、温度、电压发生变化；
 - 即时通信系统：其它用户登录、发送信息；
 - 即时报价系统：后台数据库内容发生变化；


方案
----

<<<<<<< HEAD
***使用B/S架构的服务端推送功能***
=======
**使用B/S架构的服务端推送功能**
>>>>>>> 72fc2110adb1f788505675a2e60a212c98379ab2

 - 基于liftweb comet提供服务端推送功能
 - 基于akka 2实现后台服务的消息路由，广播功能


实现
----

***使用lift comet做前端消息推送，akka 2做后端服务***

具体到当前应用，案例情况如下。客户需要实时看到3个数值:
 
 1. 当前未读消息数 (消息)
 2. 当前未办理事项数 (待办)
 3. 当前未处理紧急事项数 (催办)


**步骤**

 - 可在template-hidden/default.html 为统一设置一个 lift:comet?type=xxx，将负责与服务器建立comet连接并等待服务器推送数据
 - 基于Akka 2 建立一个系统服务ContextSystem，统一处理消息的收，发
 - 使用CometActor的localSetup和localShutdown方法订阅/取消对ContextSystem的关注
 - 当ContextSystem收到新的消息时选择路由到对应的CometActor或广播到所有订阅者
 - 由CometActor将消息推送到客户端

