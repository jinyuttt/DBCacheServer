﻿服务端dbServer
1. 启动服务端输出，已经在部署中
2. 根据自己需要，配置SQL或者传递SQL,配置SQL模板已经有样例
3. 用户信息，中英文转换以及数据权限，已经配置设计，你可以修改表
4. 服务端配置文件config.properties

客户端dbclient
1.配置类中设置服务端地址端口
2.使用请求类传递SQL语句或者配置ID以及参数化时的数据
默认配置目录
1.config目录放置服务软件配置及日志配置
2.dbconfig目录放置数据库配置。XXX_dbpool.properties则是配置一个数据库文件，配置数据库的连接及用户信息，其余不用修改
XXX就是程序中使用的数据库类型名称
3.SQLConfig目录放置配置的SQL语句文件。其中dbbase.xml配置的就是基本的用户信息，中英文转换以及数据权限，可以按照使用，也可以修改。

数据库查询 DBAcess
   1.JDBC访问数据库
   2.数据库连接池

日志封装log
1.slf4写日志

网络通信mqnet
1.网络通信

集群部署方案etcd
 1.操作etcd集群

访问代理proxy
 1.服务etcd获取服务信息
 2.提供客户端获取服务信息返回服务地址

公共使用Client
1.提供公共model
  
  其它
 1.目录中的SQL脚本文件是现在的基础设计（用户，数据权限，中英文转换，数据字典的样例数据库表，可以按照做也可以自己修改）
 2.注意配置jdk，服务端需要动态类，编译java文件
 3.数据库支持多类型多实例。主要是2种方式:1.默认数据库，就是在config.properties配置的dbType；2.是客户端指定，设置dbType字段名称
 3是配置dbType.properties，支持每个module配置一个数据库
  说明：
    数据库的配置是按照名称来的，例如默认的psql，
	则服务会在配置的dbconfig目录中找psql_dbpool.properties配置文件，然后按照配置加载获取连接
	
	
	以后的所有升级内容将会放到update.txt文件中，本文件只会列出升级时间
	
	2018-08-14
	1.添加redis客户端及配置，运行扩展内存缓存。
	2.提取样例SQL脚本，实现用户，中英文转换以及数据权限，可以直接使用。
	3.测试了数据权限及转换问题。完善了SQL配置功能。
	4.统一UTF-8编码
	5.升级了新的jdk1.8验证
	6.修改了SQL语句分析，满足SQL参数化配置；
	7.修改公共Client,解决数据类型传递问题，以实现JDBC能够准确参数化。
	
	升级时间
	2018-08-14

                2018-08-21 





	
	
	
	
	
	
	