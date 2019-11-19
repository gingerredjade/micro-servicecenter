# 集成封装使用说明
## 一、封装流程
1. 下载`gis-service-common-1.0-SNAPSHOT.jar`包、提供的封装可能需要的`依赖包`、使用说明（网页）。
2. 创建普通**Java工程**，引用`gis-service-common-1.0-SNAPSHOT.jar`、所需`依赖包`。
3. 实现gis-service-common-1.0-SNAPSHOT.jar中的**DataProcFunction**接口。

   - 在接口的数据处理方法中处理自己的数据，并返回相应格式的结果数据。
   - 接口参数及格式等参照二次开发接口网页。
   
  ```
   T  processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams)
  ```
   
  ```
   注：
   - WMS服务
     - GetCapabilities操作需响应XML格式数据。
     - GetMap操作需响应byte[]格式数据。
       
   - WMTS服务
     - GetCapabilities操作需响应XML格式数据。
     - GetTile操作需响应byte[]格式数据。
     
   - 地图瓦片服务：需响应byte[]格式数据。
   - 地图显示服务：需响应byte[]格式数据。
   
   - 其他服务：需响应字符串型数据。 
   
  ```
  
## 二、打包使用流程
1. **打成普通jar包**。（注：打出的jar包中不应有内嵌jar，不提供jar中jar的加载！）
2. 使用“战场环境保障信息统一管理系统”加载封装的jar包。

  ```
    使用顺序为：
        1. 上传jar文件
        2. 加载jar文件
        3. 编辑application-gisapp.yml文件，新增handler配置
        4. 通过服务接口测试服务正确性
  ```
   
   如果封装有问题，可通过如下顺序修改：
   
  ```
        1. 卸载已加载的jar文件
        2. 上传新的服务封装模块
        3. 加载新的服务封装模块
        4. 通过服务接口测试服务正确性
  ```
   







