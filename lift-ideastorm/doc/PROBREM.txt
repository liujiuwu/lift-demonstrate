基于cookie的WEB自动登陆功能设计和账户安全性
===========================================

*目标*

设计一个基于cookie的安全的自动登陆功能。
简单说就是不在cookie中保存密码，且token值在每次使用后都会更新 ( 类似lift那样 )


*计划*

服务端，每个账户对应一个token值 ( 随机数 )，将token存入DB中账户对应的记录中并将token和username存入cookie。用户登陆系统时，服务端解析cookie，并进行验证。

(注一: 存入cookie时可对username进行编码，读取时再解码 )
(注二: 可对cookie设置httpOnly, maxAge, path, domain的选项提高安全性 )


问题
------

1. 服务端一个用户对应一个cookie
  
    只有一个浏览器可保存有效token并实现自动登陆功能。

2. 服务端同时保存浏览器信息 userAgent -> token :: userAgent -> token :: .... :: Nil

    可实现不同浏览器保存各自的token。但是: 浏览器区分了，电脑信息怎样区分? 
    IP? 机器码? 


解决方案
------------------

在保存token列表时多加一个id参数，用以确定这个cookie是哪个浏览器提交的。

1. cookie中保存以下3个值

    id:                       标识这是哪台浏览器 ( 可使用4字节的整形数字从0开始自增计数 )
    username            标识用户名
    token                  随机值，每次自动登陆 ( 通过cookie的方式 ) 后重设 ( 登陆失败也要重设 )

2. cookie使用流程

    *第一次登陆 ( 无cookie )* 服务端新建cookie并设置
    *浏览器已存有cookie*      服务端从cookie中获取id值，并验证username和token，再重设token

结论
------------

工作量比较大，且实现后效果不好估计。参考qq mail的cookie方案，他们的token值只有一份。

*暂不使用此方案*
