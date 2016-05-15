## Kolley

Kolley is a kotlin RESTful http request framework which combine Volley with OkHttp. 

## Usage

### Quick Start

start a http request easily

```
Http.init(context) // init first

Http.get {
    url("http://api.openweathermap.org/data/2.5/weather")
    params {
        "q" - "shanghai"
        "appid" - "xxxx"
    }
    onSuccess { bytes ->
        // handle data
    }
    onFail { error ->
        // handle error
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
    tag(tag)
    ...
}
...
Http.getRequestQueue().cancelAll(tag)
```
