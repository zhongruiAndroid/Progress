# Progress
![github](https://github.com/zhongruiAndroid/Progress/blob/master/app/src/main/res/drawable/demo.gif "github")  


| 属性           | 类型      | 说明                                                                  |
|----------------|-----------|-----------------------------------------------------------------------|
| viewWidth      | dimension | 进度条宽度(不设置则默认为layout_width)                                |
| viewHeight     | dimension | 进度条高度(不设置则默认为layout_height)                               |
| bgColor        | color     | 进度条背景颜色                                                        |
| borderColor    | color     | 进度条边框颜色                                                        |
| borderWidth    | dimension | 进度条边框宽度                                                        |
| progressColor  | color     | 进度条颜色                                                            |
| nowProgress       | float   | 进度条当前进度(默认为30)                                              |
| maxProgress    | float   | 进度条最大值(默认为100)                                               |
| angle          | integer   | 进度条旋转角度                                                        |
| radius         | dimension | 进度条圆角大小(默认为viewHeight的一半,最大值不会超过viewHeight的一半) |
| useAnimation   | boolean   | 是否使用动画(默认为true)                                              |
| duration       | integer   | 动画执行时间(默认为1200毫秒)                                          |
| allInterval    | dimension | 进度条与边框的距离                                                    |
| leftInterval   | dimension | 进度条与左边框的距离                                                  |
| topInterval    | dimension | 进度条与顶部边框的距离                                                |
| rightInterval  | dimension | 进度条与右边框的距离                                                  |
| bottomInterval | dimension | 进度条与底部边框的距离                                                |


#### 设置进度条颜色渐变
```java
//进度条边框颜色渐变
Shader borderShader;
//进度条背景颜色渐变
Shader bgShader;
//进度条颜色渐变
Shader progressShader;
//进度条动画插入器,建议在setProgress方法之前设置(默认为DecelerateInterpolator)
TimeInterpolator interpolator

//为进度条设置线性颜色渐变,起始坐标为view左上角,结束坐标为view右下角(注意view圆点坐标在view中心)
LinearGradient linearGradient = new LinearGradient(
                                -myProgress.getViewWidth() / 2,
                                -myProgress.getViewHeight() / 2, 
                                myProgress.getViewWidth() / 2, 
                                myProgress.getViewHeight() / 2,
                                ContextCompat.getColor(MainActivity.this, R.color.green),
                                ContextCompat.getColor(MainActivity.this, R.color.blue),
                                Shader.TileMode.MIRROR);

myProgress.setNowProgress(30).setMaxProgress(100).setProgressShader(linearGradient).complete();
```
### 注意:代码设置属性之后调用complete()才能生效
#### 进度监听
```java
MyProgress myProgress = (MyProgress) findViewById(R.id.myProgress);
myProgress.setOnProgressInter(new MyProgress.OnProgressInter() {
  @Override
    public void progress(float scaleProgress, float progress, float max) {
        //scaleProgress:动画执行时的进度
        //progress:设置的进度
        //max:进度条最大值
    }
});
```  

<br/> 

[ ![Download](https://api.bintray.com/packages/zhongrui/mylibrary/MyProgress/images/download.svg) ](https://bintray.com/zhongrui/mylibrary/MyProgress/_latestVersion)<--版本号
<br/> 
```gradle
compile 'com.github:MyProgress:版本号看上面'
```
