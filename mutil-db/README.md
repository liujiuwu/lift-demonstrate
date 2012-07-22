Lift record-mongodb 动态连接多个数据库！
===================================================

演示lift-record-mongodb 连接多个数据库，并在运行时可选连接。

（注：其它连接方式原理应该一致，可能有些API不同。）


Test
------

    git clone git://github.com/yangbajing/lift-demonstrate.git
    cd lift-demonstrate/mutil-db
    ./sbt
    > test:compile
    > test-only learn.model.RecordTest

**测试前记得先启动MongoDB！**
