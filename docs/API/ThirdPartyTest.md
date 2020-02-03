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
            url: encodeURI('http://*****'),
            type:'get',
            success:function (res) {
                console.log(res.data)
            }
        })
    }
```
2. 使用专用的REST API测试工具发送请求进行测试。
    - Postman（推荐）
