package com.nerdery.umbrella.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nerdery.umbrella.R;
import com.nerdery.umbrella.api.ApiManager;
import com.nerdery.umbrella.model.WeatherData;
import com.nerdery.umbrella.recyclers.DailyItem;
import com.nerdery.umbrella.recyclers.FeedItem;
import com.nerdery.umbrella.recyclers.MyRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    ApiManager api = new ApiManager();
    String zipCode;
    private Menu myMenu;
    private WeatherData weatherData;

    private static final String TAG = "RecyclerViewExample";
    private List<FeedItem> feedsList;
    private List<DailyItem> dailyList;
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWeather();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.myMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivity(intent);
    }

    public void getWeather (){
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        String defaultZip = getString(R.string.homeZip);
        String myZip = sharedPreferences.getString(getResources().getString(R.string.ZIP_CODE), defaultZip);

        api.getWeatherApi().getForecastForZip(Integer.parseInt(myZip), new Callback<WeatherData>() {
            @Override
            public void success(WeatherData newWeatherData, Response response) {

                weatherData = newWeatherData;
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                        getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

                ActionBar ab = getSupportActionBar();
                String city = weatherData.currentObservation.displayLocation.city;
                String icon = weatherData.currentObservation.icon;
                ab.setTitle(city);

                String iconUrl = api.getIconApi().getUrlForIcon(icon, true);
                Log.v("myicon:", icon);


                buildList();

                adapter = new MyRecyclerAdapter(MainActivity.this, feedsList);
                mRecyclerView.setAdapter(adapter);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("failure", "failure");
            }
        });
    }

    // yes = fahrenheit / no = celsius
    private String getTemp(int i){
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        String murica = sharedPreferences.getString(getString(R.string.MURICA), "yes");
        if(i < 1){
            if(murica.equals("yes")){
                return Integer.toString(Math.round(weatherData.currentObservation.tempFahrenheit))+"°";
            } else {
                return Integer.toString(Math.round(weatherData.currentObservation.tempCelsius))+"°C";
            }
        } else {
            if(murica.equals("yes")){
                return Integer.toString(Math.round(weatherData.forecast.get(i-1).tempFahrenheit))+"°";
            } else {
                return Integer.toString(Math.round(weatherData.forecast.get(i-1).tempCelsius))+"°C";
            }
        }
    }

    private TextView hour (String myHour){
        TextView hour = new TextView(MainActivity.this);
        hour.setText(myHour);
        hour.setPadding(30, 10, 10, 20);
        return hour;
    }

    private TextView temp(int i){
        TextView temp = new TextView(MainActivity.this);
        temp.setText(getTemp(i));
        temp.setPadding(60, 10, 10, 20);
        return temp;
    }

    private void buildList () {
        feedsList = new ArrayList<>();
        int listLength = weatherData.forecast.size();

        for (int i = 0; i < listLength; i++) {
            FeedItem item = new FeedItem();
            item.setTemp(getTemp(i));
            item.setTime(weatherData.forecast.get(i).displayTime);
            item.setThumbnail(api.getIconApi().getUrlForIcon(weatherData.forecast.get(i).icon, true));


            feedsList.add(item);
            if ((i+1)%4 == 0){

            }
        }
    }

}
