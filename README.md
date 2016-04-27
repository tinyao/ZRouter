# ZRouter

ZRouter 是一个简单 Android URL 路由框架，支持 Activity、Fragment 和 Action。

## 功能

- 支持 url 路由到 Activity
- 支持 url 路由到 Fragment
- 支持 url 路由到自定义 Action 操作
- 支持路由自定义拦截操作
- 支持从 json 配置文件中加载路由表
- 所有的 url 默认  scheme 使用为 http
- 解析请求 url 中的参数，传递给响应方

## 使用方式

### Install

没有没有提交在线仓库，只能本地引用 module 了

### 初始化配置

使用前需要先创建 ZRouter 对象，并初始化路由表。你可以将路由表定义在 json 文件中，路由表每一项的定义包含:

```
url:    可以是正则表达式
class:  响应的类名(完整带包名路径)，可以是 Activity/Fragment/RouteAction
tag:    [可选]路由标签
```

一个可用的 json 路由表示例如下：

```json
{
"routes": 
[
    {
      "tag": "login",
      "url": "/page/login",
      "class": "org.example.pashanhu.ui.activity.LoginActivity"
    },
     {
      "tag": "profile",
      "url": "/page/profile",
      "class": "org.example.pashanhu.ui.activity.ProfileFragment"
    },
    {
      "tag": "share",
      "url": "/lalala/share",
      "class": "org.example.pashanhu.route.ShareAction"
    }
]
}
```

配置参数，初始化 ZRouter，ZRouter 初始化后只保留一个单例。

```java
ZRouter.from(mContext)
    .assets("routes.json")      // 路由配置文件
    .scheme("http")             // 默认 scheme
    .domain("example.com")           // 默认域名
    .defaultRoute(new ZRoute("default", H5ContainerFragment.class))     // 默认路由
    .fragmentContainer(FragmentContainerActivity.class)  // Fragment 路由容器
    .debug(debug)
    .initialize(); 
```

### 使用 ZRouter

- 获取 ZRouter 实例，如果未初始化，则返回为 null

```java
ZRouter.getInstance(); 
```

- 添加 Activity 响应规则

```java
ZRoute route = new ZRoute("this is regex url", HomeActivity.class);
ZRouter.getInstance().add(route);
```

- 添加 Fragment 响应规则

```java
ZRoute route = new ZRoute("this is regex url", DemoFragment.class);
ZRouter.getInstance().add(route);
```

- 添加 ZRouteHandler 响应规则

```java
ZRoute route = new ZRoute("this is regex url", new ZRouteHandler() {
    public void handle(ZRequest request) {
        // 做你想做的
    }
});
ZRouter.getInstance().add(route);
```

- 添加拦截

```java
ZRouter.getInstance().addInterceptor(new Interceptor() {
    public void intercept(ZRequest request, ZRoute route) 
    {
        // TODO: 你可以在这里中根据需要修改 route
        return route; 
    }
});
```

### 路由运行示例

请求打开一个 URL

```java
// 打开登录网址，会跳转到 LoginActivity
Nav.open(context, "http://example.com/page/login");
// 跳转到 LoginActivity, 当 Router 配置了默认域名后，会在路由表中自动补全为 http://example.com/page/login
Nav.open(context, "/page/login");
// 请求 URL 带参数，会被携带到 LoginActivity 的 getIntent 中，目标 Activity 根据参数自行处理
Nav.open(context, "/page/login?redirect=http://example.com/page/profile");
Nav.open(context, "/page/notification#needLogin")
```

拦截路由处理

```java
ZRouter.getInstance().addInterceptor(new Interceptor() {
    public void intercept(ZRequest request, ZRoute route) 
    {
        // 请求需要先登录
        if ("needLogin".equals(route.getFragmentLabel())
           && !isUserLoggedin() ) {
            String url = ZRouter.getInstance().get("login").getURL() + "?redirect=" + request.getURL();
            Nav.open(context, url);       
        }
        return route; 
    }
});
```

## TODO

- 支持 Activity 启动模式定义
- 支持路由参数添加
- 支持 Activity 转场动画
- 支持 Activity 的 startForResult
- 支持路由表路径参数，http://example.com/user/:user/topics

## Feedback

有任何问题，欢迎给提 issues，或者邮件联系 cz.tinyao AT gmail.com

## Liscence

```
Copyright 2015 tinyao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
