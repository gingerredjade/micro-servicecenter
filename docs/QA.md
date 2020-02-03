# 常见问题汇总


## 1.micro-servicecenter项目的sc-service-core模块不能正常引入该项目中的sc-service-common模块
出现原因
+ 可能是因为sc-service-common模块还没有打包成功，sc-service-core模块不能找不到该依赖


解决办法
+ 1.先对sc-service-common模块打包,在该模块下进行maven打包
```
mvn clean install -Dmaven.test.skip=true
或者
mvn clean install -DskipTests
```
+ 2.再进入gis-service-core模块，再用命令再进行打包
```
mvn clean package -Dmaven.test.skip=true
```

## 2. 测试或访问时出现网关超时
如出现类似下面情况，属正常情况，常发生于调试模式。

等一会儿，或者测试时直接访问应用，不经过网关。

该现象与服务本身的响应时间有关，比如网络分析响应慢就总会超时。

需等待网关同步元数据信息，亦可重启网关和应用尝试。
```
{
    "timestamp": "2019-09-27T01:46:45.284+0000",
    "status": 504,
    "error": "Gateway Timeout",
    "message": "com.netflix.zuul.exception.ZuulException: Hystrix Readed time out"
}
或者
{
    "timestamp": "2019-09-27T08:53:48.310+0000",
    "status": 404,
    "error": "Not Found",
    "message": "No message available",
    "path": "/gis-service-core/wmts"
}
```


## 3. Windows环境启动后报错无法正常使用
一般是网络问题、编码问题导致。
有如下错误：
```
java.lang.IllegalStateException: No instances available for GIS-SERVICE-CORE
和其他未记录错误信息提示
```

解决方法：
+ 1.需要关闭防火墙、关闭VNET网卡；
+ 2.执行jar包时指定编码格式
```
java -jar -Dfile.encoding=utf-8 xxx.jar
```


## 4. 无法下载SpringBoot 2.0.0.M3和SpringCloud Finchley.M2

解决方法：
+ 在pom.xml文件里加上如下代码（可参考product的pom.xml）：

```
<repositories>
	<repository>
		<id>spring-snapshots</id>
		<name>Spring Snapshots</name>
		<url>https://repo.spring.io/snapshot</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</repository>
	<repository>
		<id>spring-milestones</id>
		<name>Spring Milestones</name>
		<url>https://repo.spring.io/milestone</url>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</repository>
</repositories>

<pluginRepositories>
	<pluginRepository>
		<id>spring-snapshots</id>
		<name>Spring Snapshots</name>
		<url>https://repo.spring.io/snapshot</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</pluginRepository>
	<pluginRepository>
		<id>spring-milestones</id>
		<name>Spring Milestones</name>
		<url>https://repo.spring.io/milestone</url>
		<snapshots>
			<enabled>false</enabled>
		</snapshots>
	</pluginRepository>
</pluginRepositories>
```

+ 若在自己配置了国内maven库镜像后无法下载以上版本，则请将镜像注释掉，用maven默认的中央仓库下载（如果觉得太慢，就用科学上网）


## 5. 应用镜像快速制作
TODO。





