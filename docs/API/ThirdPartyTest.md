# 服务提供商名称标识定义
应携带提供商标识，传递参数时需在原始参数值后加上服务提供商标识，形如：原始参数值-cetc15。

标识约束定义如下：
- 15所：cetc15
- 超图:sm
- 国遥新天地:ev
- 星球时空:mc
- 四维图新:nav
- 庚图:gt
			
				
# 浏览器直接传递JSON格式参数到后台不识别，请求发不出去
## 分析
直接在浏览器GET请求中，传JSON参数报错，甚至发布出去请求。
因为HttpGet或者HttpPost都不能传包含“、“{”、“}”这样的参数，需要对特殊字符进行转义。

## 解决办法
1. 手动将特殊字符进行转义
    - 将引号“转成%22
    - 将{转成%7b
    - 将}转成%7d 
    
    - 将大于号转成%3E
    - 将英文逗号转成%2C
2. 使用专业的REST API测试工具发送请求进行测试。
    - Postman（推荐）

# 浏览器中的参数值含有SQL语句，请求发送格式若不对后台收不到
## 分析


## 解决办法
1. 前端Ajax发送请求时对整个URL进行编码，如
```
function getData() {
        /*var formData = new FormData();
        formData.append("file",$('#file')[0].files[0]);
        console.log(formData)*/
        $.ajax({
            url: encodeURI('http://192.168.56.104:8080/gis3wss/maps/services/gsquery/zhengqu/1000?format=json&search=NAME like \'%北京%\'&geo=4&points=288000000,216000000,288000000,72000000,432000000,72000000,432000000,216000000&srs=MGIS:4490&tolarence=0&relation=1&feature=524287&number=5'),       
            type:'get',
            success:function (res) {
                console.log(res.data)
            }
        })
    }
```
2. 使用专用的REST API测试工具发送请求进行测试。
    - Postman（推荐）


# 封装后服务的访问地址
## WMS
### CETC15
```
http://localhost:8999/wms?service=wms&request=GetCapabilities&version=1.3.0-cetc15
http://localhost:8999/wms?service=wms&request=GetMap&version=1.3.0-cetc15&layers=vec_1000:top-all&bbox=115,36,120,40&crs=MGIS:4490&styles=population&format=image/png&width=512&height=512

- 不需传递扩展参数auxParams
```

### sm(超图)
```
http://localhost:8999/wms?service=wms&request=GetCapabilities&version=1.3.0-sm&auxParams={
	"a":"map-china400",
	"b":"wms130",
	"c":"China"
}

http://localhost:8999/wms?service=wms&request=GetMap&version=1.3.0-sm&layers=China&bbox=10018754.17,9.3132257461548e-10,15028131.255,5009377.085&crs=EPSG:3857&styles=population&format=image/png&width=512&height=512&auxParams={
	"a":"map-china400",
	"b":"wms130",
	"c":"China"
}

- 需传递扩展参数auxParams，扩展参数auxParams格式传递如下
{
"a": "map-china400",
"b": "wms130",
"c": "China"
}
其中，a、b、c的值对应请求中的/map-china400/wms130/China，依次赋值。
```

下面是手动转移的请求地址，可直接拷贝纸浏览器测试，不推荐！
```
http://localhost:8999/wms?service=wms&request=GetCapabilities&version=1.3.0-sm&auxParams=%7b%22a%22:%22map-china400%22,%22b%22:%22wms130%22,%22c%22:%22China%22%7d
```

## WMTS
### CETC15
```
http://localhost:9000/gis-service-core/wmts?service=wmts&request=GetCapabilities&version=1.0.0-cetc15
http://localhost:9000/gis-service-core/wmts?service=wmts&request=GetTile&version=1.0.0-cetc15&format=image/png&layer=vec__1000__vec_1000__top-all&style=default&tilematrixset=4490&tilematrix=6&tilerow=82&tilecol=105

- 不需传递扩展参数auxParams
```


### sm（超图）
```
http://localhost:9000/gis-service-core/wmts?service=wmts&request=GetCapabilities&version=1.0.0-sm&auxParams={
	"a":"map-world",
	"b":"wmts100"
}


http://localhost:9000/gis-service-core/wmts?service=wmts&request=GetTile&version=1.0.0-sm&format=image/png&layer=World&style=default&tilematrixset=GlobalCRS84Scale_World&tilematrix=2&tilerow=0&tilecol=3&auxParams={
	"a":"map-world",
	"b":"wmts100"
}


- 需传递扩展参数auxParams，扩展参数auxParams格式传递如下
{
"a": "map-world",
"b": "wmts110"
}
其中，a、b的值对应请求中的/map-world/wmts110，依次赋值。

```



# 适配测试用的第三方服务原始访问地址

## WMS
### CETC15
+ GetCapabilities
```
http://192.168.56.113:8999/gis3wss/maps/wms?service=wms&version=1.3.0&request=GetCapabilities
```

+ GetMap
```
http://192.168.56.113:8999/gis3wss/maps/wms?service=wms&request=getmap&version=1.3.0&layers=vec_1000:top-all&bbox=115,36,120,40&srs=MGIS:4490&format=image/png&width=1024&height=512
```


### 超图
+ GetCapabilities
```
http://192.168.56.179:8090/iserver/services/map-china400/wms130/China
```

+ GetMap
```
http://192.168.56.179:8090/iserver/services/map-china400/wms130/China?LAYERS=China&VERSION=1.3.0&EXCEPTIONS=INIMAGE&SERVICE=WMS&REQUEST=GetMap&STYLES=&FORMAT=image%2Fpng&CRS=EPSG%3A3857&BBOX=10018754.17,9.3132257461548e-10,15028131.255,5009377.085&WIDTH=256&HEIGHT=256
```



## WMTS
### CETC15
+ GetCapabilities
```
http://192.168.56.113:8999/gis3wss/maps/wmts?service=WMTS&version=1.0.0&request=GetCapabilities
```

+ GetTile
```
http://192.168.56.113:8999/gis3wss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=vec__1000__vec_1000__top-all&style=default&format=image/png&tilematrixset=4490&tilematrix=6&tilerow=82&tilecol=105
```

### 超图
+ GetCapabilities
```
http://192.168.56.179:8090/iserver/services/map-world/wmts100
```

+ GetTile
```
http://192.168.56.179:8090/iserver/services/map-world/wmts100?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=World&STYLE=default&TILEMATRIXSET=GlobalCRS84Scale_World&TILEMATRIX=2&TILEROW=0&TILECOL=3&FORMAT=image%2Fpng
```

