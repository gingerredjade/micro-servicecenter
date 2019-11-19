# GIS服务中心
本应用基于Spring Boot框架，通过调用原RESTful API封装GIS服务。
- 地名检索服务：支持cetc15。
- 网络分析服务：支持cetc15。
- 几何计算服务：支持cetc15。
- 地形分析服务:支持cetc15。
- 地图瓦片服务
    - MapTile资源：支持cetc15、mc。
    - OGC-WMTS：支持cetc15、sm。
- 地图显示服务
    - Map资源：支持cetc15。
    - OGC-WMS：支持cetc15、sm。


## 日志位置
```
日志产生位置在系统的/var/log/gis-service-core_log目录下。
```

## 服务模块（JAR）动态装载、卸载

### 自动化
在该应用启动时，会自动装载“”目录下的所有模块jar包。此时加载的模块，支持在服务运行态进行卸载。 

### 手动触发
#### 接口说明
    参见该应用swagger-ui.html中的“JAR动态管理服务-recommend”.
    支持模块的上传、加载、卸载、删除操作。
    - 上传（逐一上传jar）
    - 加载（逐一加载、完全加载已上传的jar）
    - 卸载（逐一卸载、完全卸载已上传的jar）
    - 删除（逐一删除、批量删除、快速删除已上传的jar）

#### 使用步骤
    配置application.yml中的3rd.upload-path为3rdlibs所在目录;
    






