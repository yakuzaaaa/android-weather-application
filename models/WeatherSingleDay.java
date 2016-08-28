package com.example.nilarnab.mystats.models;

/**
 * Created by nilarnab on 6/8/16.
 */
public class WeatherSingleDay {
    private String description;
    private String dayOfWeek;
    private long date;
    private Double max;
    private Double min;
    private Double humidity;
    private Double windSpeed;
    private Double pressure;
    private Double degree;
    private long weatherConditionId;

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public long getWeatherConditionId() {
        return weatherConditionId;
    }

    public void setWeatherConditionId(long weatherConditionId) {
        this.weatherConditionId = weatherConditionId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
