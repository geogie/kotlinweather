# kotlin weather

说明：  
1. Gson、retrofit、rxJava+RxAndroid
2. 简单的天气练习项目：横竖屏适配(layout-land)、FragmentDialog等
3. 手机适配（hdpi、xdpi、xxhdpi等）[理解](http://blog.csdn.net/lincyang/article/details/44174997)

注意：
在com.georgeren.kotlin_weather.consts文件夹下创建 Secrets 文件 内容如下：
```kotlin
object Secrets {
    val API_KEY = "your_api_key"
}
```
以为天气的信息是从网络获取的，Secrets.API_KEY="you api key",
[点击Weather API申请key](https://market.mashape.com/fyhao/weather-13)
参考：
[原项目](https://github.com/gurleensethi/kotlin-weather)