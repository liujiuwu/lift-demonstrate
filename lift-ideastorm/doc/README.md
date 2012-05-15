使用说明
========

简介
----

记录我学习liftweb + akka的过程和其中的一些想法。


可以在sbt console中手动添加用户
-------------------------------

    > console
    [info] Starting scala interpreter...
    [info] 
     Welcome to Scala version 2.9.1.final (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_31).
     Type in expressions to have them evaluated.
     Type :help for more information.
    
     scala> import learn.model._
     import learn.model._
    
     scala> new bootstrap.liftweb.Boot().boot
    
     scala> val account = AccountRecord.createRecord
     account: learn.model.AccountRecord = class learn.model.AccountRecord={updatedAt=Sat May 05 11:51:19 CST 2012, email=, username=, _id=4fa4a3b7744e8ea72f739de6, age=0,locale=zh_CN, token=, createdAt=Sat May 05 11:51:19 CST 2012, timeZone=Asia/Shanghai, password=Password(,)}
    
     scala> import net.liftweb.mongodb.record.field.Password
     import net.liftweb.mongodb.record.field.Password
    
     scala> account.username("yangjing").password(Password("yangjing")).age(26).email("yangbajing@gmail.com").save
     res2: learn.model.AccountRecord = class learn.model.AccountRecord={updatedAt=Sat May 05 11:52:14 CST 2012, email=yangbajing@gmail.com, username=yangjing,_id=4fa4a3b7744e8ea72f739de6, age=26, locale=zh_CN, token=, createdAt=Sat May 05 11:51:19 CST 2012, timeZone=Asia/Shanghai,password=Password(sMaE6UJG0av+KlTz2EsMJn1Pqjc=,W1KQ2S2YXRDEGCW4)}

     scala> val account = AccountRecord.find("4fa4a3b7744e8ea72f739de6").open_!
     account: learn.model.AccountRecord = class learn.model.AccountRecord={updatedAt=Sat May 05 11:52:14 CST 2012, email=yangbajing@gmail.com, username=yangjing,_id=4fa4a3b7744e8ea72f739de6, age=26, locale=zh_CN, token=, createdAt=Sat May 05 11:51:19 CST 2012, timeZone=Asia/Shanghai,password=Password(sMaE6UJG0av+KlTz2EsMJn1Pqjc=,W1KQ2S2YXRDEGCW4)}

     scala> account.password.isMatch("yangjinga")
     res3: Boolean = false
    
     scala> account.password.isMatch("yangjing")
     res4: Boolean = true

    
AccountRecord.find 时那个字符串形式的ObjectID咋来的? 如下:
----------------------------------------------------------

     MongoDB shell version: 2.0.4
     connecting to: test
    > use learn
     switched to db learn
    > db.account.count()
     1
    > db.account.findOne()
    {
      "_id" : ObjectId("4fa4a3b7744e8ea72f739de6"),
      "updatedAt" : ISODate("2012-05-05T03:52:14.014Z"),
      "email" : "yangbajing@gmail.com",
      "username" : "yangjing",
      "age" : 26,
      "locale" : "zh_CN",
      "token" : "",
      "createdAt" : ISODate("2012-05-05T03:51:19.551Z"),
      "timeZone" : "Asia/Shanghai",
      "password" : {
        "pwd" : "sMaE6UJG0av+KlTz2EsMJn1Pqjc=",
        "salt" : "W1KQ2S2YXRDEGCW4"
      }
    }
    > 
