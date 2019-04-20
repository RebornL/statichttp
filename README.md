# statichttp

## 介绍

使用Java实现一个静态资源HTTP服务器，可以读取静态文件，缓存静态资源，使用gzip压缩资源，MIME类型支持，多线程处理响应。可以根据配置，设置静态目录和服务器ip端口号。

## 工具

- Java（1.11）
- Maven
- IDEA

## 说明

- Main.java：主程序运行类
- HttpServer.java：服务器类
- Request.java：Request响应封装
- Response.java：Response响应封装
- Handler.java：处理类
- MIME.java：MIME类型封装
- MD5Util.java：参考别人的md5生成类

## 截图

**第一次请求资源**

![1555750573878](static/1555750573878.png)

**再次请求资源**

![1555750608392](static/1555750608392.png)

**请求不存在的资源**

![1555750680631](static/1555750680631.png)



## 更新说明：

2019-04-19：完成基本功能

2019-04-20：重构代码，完成Request和Respon解耦，精简代码