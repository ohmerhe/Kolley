# Kolley

Kolley是用kotlin实现，结合了[Volley](https://developer.android.com/training/volley/index.html)和[OkHttp](http://square.github.io/okhttp)的一个RESTful网络请求框架。

[ ![Download](https://api.bintray.com/packages/ohmerhe/maven/kolley/images/download.svg) ](https://bintray.com/ohmerhe/maven/kolley/_latestVersion)


## 引用

```
repositories {
    jcenter()
}

compile 'com.ohmerhe.kolley:kolley:0.3.0'
```

## 0.3.0

- 支持 Post 请求上传 Raw
- 支持 upload 文件
- 修复请求中没有传递 Header 的bug

## 标准HTTP请求

### 快速使用

初始化一次以后，可以很方便的发起一个网络请求。

```
Http.init(context) 

Http.get {
    url = "http://api.openweathermap.org/data/2.5/weather"
    onSuccess { bytes ->
        // handle data
    }
}    
```

### 参数

可以在`params`配置块中使用`"key" - "value"`的格式为网络请求置顶参数，每个参数占一行。

```
Http.get {
    ...
    params {
        "q" - "shanghai"
        "appid" - "xxxx"
    }
    ...
}
```   

### 回调

回调也是类似配置的方式

```
Http.get {
    ...
    onStart {  }

    onSuccess { bytes ->
        // get data
    }

    onFail { error ->
        // handle error
    }

    onFinish {  }
    ...
}
```

### 请求Headers

添加`headers`的方式和`params`的方式很相似

```
Http.get {
    ...
    headers {
        "Content-Type" - "application/json"
    }
    ...
} 
```

### 取消

有两种方式可以取消网络请求

- `Request`对象直接取消

```
val request = Http.get{...}
...
request.cancel
```

- 使用`tag`的方式取消

你可以为每个网络请求打上一个`tag`，然后通过`RequestQueue`取消。

```
Http.get {
    ...
    tag = mTag
    ...
}
...
Http.getRequestQueue().cancelAll(tag)
```

## 上传文件

将需要上传的文件添加到 `files` 下面，参数形式和 `params` 一直，前面是 `key`，后面的 `value` 是对应文件的路径。
可支持多文件上传。

```
Http.upload{
    url = "http://192.168.199.110:3000"
    files {
        "image" - $imagePath
    }
}
```

如果上传文件需要带其他参数，也可以和其他网络请求一样添加在 `params` 下面。


## 获取图片

### 初始化

在你的application中直接初始化`Image`，

```
Image.init(context)
```

### Configuration

或者带上一些配置信息一起初始化

```
Image.init(this){
    // these values are all default value , you do not need specific them if you do not want to custom
    memoryCacheEnabled = true
    memoryCacheSize = (Runtime.getRuntime().maxMemory() / 8).toInt()
    diskCacheDir = File(cacheImagePath)
    diskCacheSize = 200 * 1024 * 1024 // default 200m size
    diskCacheEnabled = true
    compressFormat = Bitmap.CompressFormat.JPEG
    compressQuality = 80
}
```

### 显示图片

直接显示图片或者配置化显示

```
Image.display {
    url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
    imageView = mImageView
}
```


```
Image.display {
    url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
    imageView = mImageView
    options {
        // these values are all default value , you do not need specific them if you do not want to custom
        imageResOnLoading = R.drawable.default_image
        imageResOnLoading = R.drawable.default_image
        imageResOnFail = R.drawable.default_image
        decodeConfig = Bitmap.Config.RGB_565
        scaleType = ImageView.ScaleType.CENTER_CROP
        maxWidth = ImageDisplayOption.DETAULT_IMAGE_WIDTH_MAX
        maxHeight = ImageDisplayOption.DETAULT_IMAGE_HEIGHT_MAX
    }
}
```

### 下载图片

直接下载或者配置化下载。

```
Image.load {
    url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
    onSuccess { bitmap -> mImageView.setImageBitmap(bitmap)}
}
```

```
Image.load {
    url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
    options {
        scaleType = ImageView.ScaleType.CENTER_CROP
        maxWidth = ImageDisplayOption.DETAULT_IMAGE_WIDTH_MAX
        maxHeight = ImageDisplayOption.DETAULT_IMAGE_HEIGHT_MAX
    }
    onSuccess { bitmap -> _imageView2?.setImageBitmap(bitmap) }
    onFail { error ->
        log(error.toString())
    }
}
```

