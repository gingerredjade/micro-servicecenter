## 【信服瓦片访问示例】
```
如：http://192.168.55.111:8989/mswss/maps/tiles/testa_MGIS102089/1000/testa_1000/top-all/3/1/1/1/new/8/147/206
```

## 【三期&信服TileURI形式对比】
- 三期WMTS的TileURI：YX_WORLD_2000/1000/YX_WORLD_2000_1000/top-all/jpg/level/row/col

- 信服WMTS的TileURI格式：svcAlias/svcVersion/tiledLayerNS/layerAlias/pieceDataType/gridCode/pieceFormat/appType/dataVersion/level/row/col
	dataVersion（内部固定new）、gridCode（内部固定1）、appType（内部固定1）、pieceFormat(需传递)、pieceDataType(需传递)

## 【三期WMTS请求示例-mswss中已支持】
```
http://192.168.56.102:8080/gis3wss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=YX_WORLD_2000__1000__YX_WORLD_2000_1000__top-all&format=image/jpg&style=default&tilematrixset=4490&tilematrix=4&tilerow=7&tilecol=5
```

## 【信服WMTS请求示例】
```
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=testa_MGIS102089__1000__testa_1000__top-all&format=image/jpg&style=default&tilematrixset=102089&tilematrix=8&tilerow=108&tilecol=206&dataVersion=new&pieceFormat=1&pieceDataType=3
（应在原三期WMTS请求参数基础之上增加dataVersion（内部固定new）、gridCode（内部固定1）、appType（内部固定1）、pieceFormat、pieceDataType参数）
```


## 【WMTS请求示例】
```
[GetCapabilities]http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&version=1.0.0&request=GetCapabilities
[GetTile]http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=testa_MGIS102089__1000__testa_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=8&tilerow=108&tilecol=206
```

【请求介绍】

- ---layer参数格式=服务别名__服务版本号__瓦片图层命名空间__图层别名；
- ---style=default固定
- ---tilematrixset图层的空间参考数值
- ---tilematrix级别
- ---tilerow瓦片行号
- ---tilecol瓦片列号

- 级别行列号要求

    比如下你给要请求tilematrix（level）/tilerow（row）/tilecol（col）的数据，需要再次计算一下瓦片的行号，用新计算出来的行号去访问。
	- 空间参考-3857/102089:	newRow=Math.pow(2,level)-1-row；
	- 空间参考-4326:	newRow=2*Math.pow(2,level-1)-1-row；
	- 空间参考-4214/4610/4490/4978:	newRow=2*Math.pow(2,level)-1-row；

	比如：若请求102089空间参考下的8/147/206的数据，访问时的级别行列号就应该是8/108/206。



## 访问实例

【实例-56.205上数据wmts访问-信服格式元数据,来自app/本地】
```
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=testa_MGIS102089__1000__testa_1000__top-all__pieceForamt__pieceDataType&format=image/jpg&style=default&tilematrixset=102089&tilematrix=8&tilerow=108&tilecol=206
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=testa_MGIS102089__1000__testa_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=8&tilerow=108&tilecol=206
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=nanbu0_MGIS102089__1000__nanbu0_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=9&tilerow=220&tilecol=421
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=test11_MGIS102089__1000__test11_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=10&tilerow=437&tilecol=848
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=test22_MGIS102089__1000__test22_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=10&tilerow=437&tilecol=851
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=test2_MGIS102089__1000__test2_1000__top-all__3__1&format=image/jpg&style=default&tilematrixset=102089&tilematrix=8&tilerow=108&tilecol=208
```

### 【实例-本地三期格式数据wmts访问-三期格式元数据,来自本地】
```
http://192.168.55.111:8989/mswss/maps/wmts?service=WMTS&request=GetTile&version=1.0.0&layer=5W_WORLD__1000__5W_WORLD_1000__top-all_jpg&format=image/jpg&style=default&tilematrixset=4490&tilematrix=2&tilerow=3&tilecol=8
```


### 【通过请求URL长度区分三期/信服请求方式，并进行不同URL解析及Path组建】
- 三期：/5W_WORLD/1000/5W_WORLD_1000/top-all/jpg/2/4/8（8个）
- 信服：/test2_MGIS102089/1000/test2_1000/top-all/3/1/1/1/new/8/146/208（12个）
