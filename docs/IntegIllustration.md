# 集成封装使用说明
1. 引用gis-service-common-1.0-SNAPSHOT.jar。
2. 实现DataProcFunction接口。
3. 在接口的数据处理方法中处理自己的数据，并返回相应格式的结果数据。
```
T  processData(HttpServletRequest request, AppParams __appParams, AuxParams __auxParams)
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







