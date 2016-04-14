## Kolley

Kolley is a kotlin RESTful http request framework which combine Volley with OkHttp. 

## Usage

start a http request,

```
get(context) {

    url("http://api.openweathermap.org/data/2.5/weather")

    params {
        "q" - "shanghai"
        "appid" - "xxxx"
    }

    start { Log.d(TAG, "on start") }

    success { bytes ->
        Log.d(TAG, "on success ${bytes.toString()}")
    }

    fail { error ->
        Log.d(TAG, "on fail ${error.toString()}")
    }
    
    finish { Log.d(TAG, "on finish") }
}    
```