package com.example.nilarnab.mystats.events;

import com.example.nilarnab.mystats.models.WeatherSingleDay;

import java.util.ArrayList;

/**
 * Created by nilarnab on 6/8/16.
 */
public class WeatherFetchedEvent {
    private boolean isSuccess;
    private ArrayList<WeatherSingleDay> list;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public ArrayList<WeatherSingleDay> getList() {
        return list;
    }

    public void setList(ArrayList<WeatherSingleDay> list) {
        this.list = list;
    }
}
