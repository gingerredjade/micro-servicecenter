# 软件相关文件
- ftp://192.168.56.6:10000/mmss/Web-Cluster/ServiceCenter/
- Maven依赖从Maven私有仓库拉取，使用方法参见http://192.168.56.31/lixiao/web-dev-env
- 项目文档说明参见gis-servicecenter/docs目录下各说明文档

# GIS Service Center
# 框架搭建环境
````
- 编译器：IDEA 2019.1
- Maven：3.6.0
- JDK：1.8.0_191（JDK需1.8+）
- 系统：Win 10
- 工具：Postman 7.5.0（Windows & Linux）
- 数据库：MySQL 8.0.17（Windows & Linux）
- 数据库可视化：Navicat 120/121（Windows & Linux）
````

# 开发框架
- Spring Boot 2.0（2.0.4、2.0.3、2.0.2、2.0.6均涉及）
- Spring Cloud Eureka
- Spring Cloud Zuul

# gis-servicecenter组件结构及[端口]
- gis-service-common   [无]

```
提取的公共模块，纯Java模块，非Web服务
```
- gis-register-center   [默认8761]

```
GIS服务注册中心模块
```
- gis-api-gateway       [默认9000]

```
GIS服务网关模块
```
- gis-service-core      [默认8997]

```
GIS服务中心模块
```
- gis-service-show      [默认8998]

```
GIS空间信息微服务管理平台模块
```
- docs      

```
各种文档
```
- config-repo  

```
用来存放配置文件(适用配置中心，配置中心上线用对应配置需用绝对路径)
```
- third-party-cetc15

```
第三方电科15所服务适配模块
```
- third-party-supermap

```
第三方超图服务适配模块
```
- third-party-mc

```
第三方星球时空服务适配模块
```
- third-party-nav

```
第三方四维图新服务适配模块
```
- third-party-ev

```
第三方国遥新天地服务适配模块
```


# 注意事项
1. 部署、访问、测试等内容参见docs里内容。
2. 各模块中的Dockerfile、build.sh不能用不使用。
