# 可用部署环境
192.168.56.221环境。
/home/gis/service-center/package/
vncserver :1

## 注册中心
```
注册中心主页：http://192.168.56.221:8761
注册中心元数据访问：http://192.168.56.221:8761/eureka/apps
```

## 服务中心
```
http://192.168.56.221:9000/gis-service-core/swagger-ui.html
http://192.168.56.221:8997/swagger-ui.html
```

## 管理系统工程

```
http://192.168.56.221:9000/gis-service-show/swagger-ui.html
http://192.168.56.221:8998/swagger-ui.html

http://192.168.56.221:8998/gis-service-show/swagger-ui.html
http://192.168.56.221:8998/visual/mapOnline.html
```



## 健康状态检测
给客户端发送执行健康状态检测的命令：
```
- 使用Postman发送GET请求：http://localhost:8080/actuator/health 
- 使用终端发送GET请求：curl -X GET http://localhost:8080/actuator/health 
```





