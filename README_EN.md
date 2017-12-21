# Kolley

Kolley is a kotlin RESTful http request framework which combine [Volley](https://developer.android.com/training/volley/index.html) with [OkHttp](http://square.github.io/okhttp).

[ ![Download](https://api.bintray.com/packages/ohmerhe/maven/kolley/images/download.svg) ](https://bintray.com/ohmerhe/maven/kolley/_latestVersion)


## Reference

```
repositories {
    jcenter()
    maven { url "https://jitpack.io" } // for PersistentCookieJar
}

compile 'com.ohmerhe.kolley:kolley:0.3.1'
```

## 0.3.1

- support persistent cookie 
- fix bug for chinese chars

## 0.3.0

- support raw post
- support upload files
- fix without headers bug

## Standard HTTP Usage

### Quick Start

start a http request easily

```
Http.init(context) // init first, you can just init `Http` in your application

// start a request anywhere
Http.get {
    url = "http://api.openweathermap.org/data/2.5/weather"
    onSuccess { bytes ->
        // handle data
    }
}    
```

### Params

params can be added with `"key" - "value"` format in `params` block, each param must stay at single line.

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

### Callback

you can get callback easily like this:

```
Http.get {
    ...
    onStart { // do something before http request }

    onSuccess { bytes ->
        // get data
    }

    onFail { error ->
        // handle error
    }

    onFinish { // do something after http request finished }
    ...
}
```

### Headers

headers info can be added like params in `headers` block.

```
Http.get {
    ...
    headers {
        "Content-Type" - "application/json"
    }
    ...
} 
```

### Cancel

There two ways to cancel a request.

- `Request` instance call cancel directly

```
val request = Http.get{...}
...
request.cancel
```

- cancel with tag

You can set tag for a request first, and then cancel it from `RequestQueue`.

```
Http.get {
    ...
    tag = mTag
    ...
}
...
Http.getRequestQueue().cancelAll(tag)
```

## Upload

```
Http.upload{
    url = "http://192.168.199.110:3000"
    files {
        "image" - $imagePath
    }
}
```

## Image Get Usage

### Init First

you can just simplely init `Image` in your application.

```
Image.init(context)
```

### Configuration

or, you can init `Image` with configuration.

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

### Display Image

display image directly or with custom options.

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

### Load Image

load image directly or with options.

```
Image.load {
    url = "http://7xpox6.com1.z0.glb.clouddn.com/android_bg.jpg"
    onSuccess { bitmap ->
        mImageView.setImageBitmap(bitmap)
    }
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
    onSuccess { bitmap ->
        _imageView2?.setImageBitmap(bitmap)
    }
    onFail { error ->
        log(error.toString())
    }
}
```

