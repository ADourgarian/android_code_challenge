package com.nerdery.umbrella.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nerdery.umbrella.R;
import com.nerdery.umbrella.api.ApiManager;
import com.nerdery.umbrella.model.WeatherData;

import java.io.InputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    ApiManager api = new ApiManager();
    String zipCode;
    private Menu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        String defaultZip = getString(R.string.homeZip);
        String myZip = sharedPreferences.getString(getResources().getString(R.string.ZIP_CODE), defaultZip);

        getWeather(myZip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        ActionBar ab = getSupportActionBar();

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

    public void getWeather (String myZip){
        api.getWeatherApi().getForecastForZip(Integer.parseInt(myZip), new Callback<WeatherData>() {
            @Override
            public void success(WeatherData weatherData, Response response) {

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                        getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

                ActionBar ab = getSupportActionBar();
                String city = weatherData.currentObservation.displayLocation.city;
                String weather = weatherData.currentObservation.weather;
                String currentTemp = Float.toString(Math.round(weatherData.currentObservation.tempFahrenheit));
                String icon = weatherData.currentObservation.icon;
                ab.setTitle(city);

                String iconUrl = api.getIconApi().getUrlForIcon(icon, false);
                Log.v("myicon:", icon);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.CURRENT_CONDITIONS), weather);
                editor.putString(getString(R.string.CURRENT_TEMP), currentTemp);
                editor.putString(getString(R.string.ICON_URl), iconUrl);
                editor.commit();

                buildMenu(sharedPreferences, ab, weatherData, iconUrl);
                buildHourly(weatherData);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("failure", "failure");
            }
        });
    }

    private void buildMenu (SharedPreferences sharedPreferences, ActionBar ab, WeatherData weatherData, String iconUrl){
        TextView currentCondition = new TextView(MainActivity.this);
        currentCondition.setText(sharedPreferences.getString(getString(R.string.CURRENT_CONDITIONS), "Condition"));
        currentCondition.setPadding(5, 0, 5, 0);
        currentCondition.setTypeface(null, Typeface.BOLD);
        currentCondition.setTextSize(14);
        myMenu.add("Text").setActionView(currentCondition).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        TextView currentTemp = new TextView(MainActivity.this);
        currentTemp.setText(getTemp(weatherData, 0));
        currentTemp.setPadding(5, 0, 5, 0);
        currentTemp.setTypeface(null, Typeface.BOLD);
        currentTemp.setTextSize(14);
        myMenu.add("Text").setActionView(currentTemp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        ImageView icon = new ImageView(MainActivity.this);

        // show The Image in a ImageView
        new DownloadImageTask(icon)
                .execute(iconUrl);


        icon.setPadding(5, 0, 5, 0);
        myMenu.add("Image").setActionView(icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.weather_warm)));
    }

    private void buildHourly (WeatherData weatherData){

        LinearLayout myRoot = (LinearLayout) findViewById(R.id.body);

        for (int i = 0; i < weatherData.forecast.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(50, 20, 0, 20);

            linearLayout.addView(hour(weatherData.forecast.get(i).displayTime));
            linearLayout.addView(icon(weatherData.forecast.get(i).icon));
            linearLayout.addView(temp(weatherData, i));

            myRoot.addView(linearLayout);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // yes = fahrenheit / no = celsius
    private String getTemp(WeatherData weatherData,int i){
        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        String murica = sharedPreferences.getString(getString(R.string.MURICA), "yes");
        if(i < 1){
            if(murica.equals("yes")){
                return Integer.toString(Math.round(weatherData.currentObservation.tempFahrenheit));
            } else {
                return Integer.toString(Math.round(weatherData.currentObservation.tempCelsius));
            }
        } else {
            if(murica.equals("yes")){
                return Integer.toString(Math.round(weatherData.forecast.get(i-1).tempFahrenheit));
            } else {
                return Integer.toString(Math.round(weatherData.forecast.get(i-1).tempCelsius));
            }
        }
    }

    private TextView hour (String myHour){
        TextView hour = new TextView(MainActivity.this);
        hour.setText(myHour);
        return hour;
    }

    private ImageView icon (String myIcon){
        ImageView icon = new ImageView(MainActivity.this);
        String iconUrl = api.getIconApi().getUrlForIcon(myIcon, true);
        new DownloadImageTask(icon)
                .execute(iconUrl);
        return icon;
    }

    private TextView temp (WeatherData weatherData, int i){
        TextView temp = new TextView(MainActivity.this);
        temp.setText(getTemp(weatherData, i));
        return temp;
    }


}
