## Kolley

Kolley is a kotlin RESTful http request framework which combine Volley with OkHttp. 

## Usage

### Quick Start

start a http request easily

```
get(context) {
    url("http://api.openweathermap.org/data/2.5/weather")
    params {
        "q" - "shanghai"
        "appid" - "xxxx"
    }
    success { bytes ->
        // handle data
    }
    fail { error ->
        // handle error
    }
}    
```

### Params

params can be added with `"key" - "value"` format in `params` block, each param must stay at single line.

```
get(context) {
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
get(context) {
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
val request = get(context){...}
...
request.cancel
```

- cancel with tag

You can set tag for a request first, and then cancel it from `RequestQueue`.

```
get(context) {
    ...
    tag(tag)
    ...
}
...
RequestManager.getRequestQueue(context).cancelAll(tag)
```
