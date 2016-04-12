package com.ohmerhe.kolley.demo;

import java.util.List;

/**
 * Created by ohmer on 9/22/15.
 * use test
 */
public class Weather {


    /**
     * coord : {"lon":121.46,"lat":31.22}
     * weather : [{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}]
     * base : stations
     * main : {"temp":296.86,"pressure":1010,"humidity":88,"temp_min":296.15,"temp_max":298.15}
     * visibility : 7000
     * wind : {"speed":3,"deg":90}
     * clouds : {"all":90}
     * dt : 1442929468
     * sys : {"type":1,"id":7452,"message":0.0173,"country":"CN","sunrise":1442871738,"sunset":1442915463}
     * id : 1796236
     * name : Shanghai
     * cod : 200
     */

    private CoordEntity coord;
    private String base;
    private MainEntity main;
    private int visibility;
    private WindEntity wind;
    private CloudsEntity clouds;
    private int dt;
    private SysEntity sys;
    private int id;
    private String name;
    private int cod;
    private List<WeatherEntity> weather;

    public void setCoord(CoordEntity coord) {
        this.coord = coord;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void setMain(MainEntity main) {
        this.main = main;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setWind(WindEntity wind) {
        this.wind = wind;
    }

    public void setClouds(CloudsEntity clouds) {
        this.clouds = clouds;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public void setSys(SysEntity sys) {
        this.sys = sys;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public void setWeather(List<WeatherEntity> weather) {
        this.weather = weather;
    }

    public CoordEntity getCoord() {
        return coord;
    }

    public String getBase() {
        return base;
    }

    public MainEntity getMain() {
        return main;
    }

    public int getVisibility() {
        return visibility;
    }

    public WindEntity getWind() {
        return wind;
    }

    public CloudsEntity getClouds() {
        return clouds;
    }

    public int getDt() {
        return dt;
    }

    public SysEntity getSys() {
        return sys;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCod() {
        return cod;
    }

    public List<WeatherEntity> getWeather() {
        return weather;
    }

    public static class CoordEntity {
        /**
         * lon : 121.46
         * lat : 31.22
         */

        private double lon;
        private double lat;

        public void setLon(double lon) {
            this.lon = lon;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
        }
    }

    public static class MainEntity {
        /**
         * temp : 296.86
         * pressure : 1010
         * humidity : 88
         * temp_min : 296.15
         * temp_max : 298.15
         */

        private double temp;
        private int pressure;
        private int humidity;
        private double temp_min;
        private double temp_max;

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public void setTemp_min(double temp_min) {
            this.temp_min = temp_min;
        }

        public void setTemp_max(double temp_max) {
            this.temp_max = temp_max;
        }

        public double getTemp() {
            return temp;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public double getTemp_max() {
            return temp_max;
        }
    }

    public static class WindEntity {
        /**
         * speed : 3
         * deg : 90
         */

        private int speed;
        private int deg;

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public void setDeg(int deg) {
            this.deg = deg;
        }

        public int getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }
    }

    public static class CloudsEntity {
        /**
         * all : 90
         */

        private int all;

        public void setAll(int all) {
            this.all = all;
        }

        public int getAll() {
            return all;
        }
    }

    public static class SysEntity {
        /**
         * type : 1
         * id : 7452
         * message : 0.0173
         * country : CN
         * sunrise : 1442871738
         * sunset : 1442915463
         */

        private int type;
        private int id;
        private double message;
        private String country;
        private int sunrise;
        private int sunset;

        public void setType(int type) {
            this.type = type;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setMessage(double message) {
            this.message = message;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setSunrise(int sunrise) {
            this.sunrise = sunrise;
        }

        public void setSunset(int sunset) {
            this.sunset = sunset;
        }

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public double getMessage() {
            return message;
        }

        public String getCountry() {
            return country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }
    }

    public static class WeatherEntity {
        /**
         * id : 804
         * main : Clouds
         * description : overcast clouds
         * icon : 04n
         */

        private int id;
        private String main;
        private String description;
        private String icon;

        public void setId(int id) {
            this.id = id;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}
