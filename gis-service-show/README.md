# 空间信息微服务管理平台模块

# 一、组件功能支持说明
## 1. 处理http/json 请求
## 2. 静态资源存储访问
前端开发时，静态资源存储需遵循以下规则：
```
Recommended！
1. 静态资源文件、页面文件均放在放在src/main/resources/static目录下；

【注意事项】
开发时，放上述src/main/resources/static目录；
打包后将src/main/resources/static目录下的左右文件放于gis-service-show-0.0.1-SNAPSHOT.jar同级目录的show/visual目录下（注意不包含static目录）。
目录结构如：
- gis-service-show
    - application.yml
    - application-mysql.yml
    - gis-service-show-0.0.1-SNAPSHOT.jar
    - start-gis-service-show.sh
    - show/visual/所有静态资源
    
其中，所有静态资源即“开发工程中src/main/resources/static目录下的静态文件”。
可动态修改其中的静态文件，不重启服务即可生效。
```

```
Deprecated！
1. 静态资源文件放在src/main/resources/static目录下；
2. 页面文件放在src/main/resources/resources目录下；
```

## 3. 视图模版引擎
一般把页面放在templates中，静态资源放在static中。页面引用这些静态资源时,注意相对路径，因为//*，所以不需要引用到static那级。


