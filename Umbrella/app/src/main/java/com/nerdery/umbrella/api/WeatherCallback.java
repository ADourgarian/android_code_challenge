package com.nerdery.umbrella.api;

import com.nerdery.umbrella.model.WeatherData;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by AVALON on 3/3/16.
 */

public class WeatherCallback implements Callback<WeatherData>{

    @Override
    public void success(WeatherData weatherData, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {

    }
}
