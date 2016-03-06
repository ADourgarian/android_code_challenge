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

                icon = api.getIconApi().getUrlForIcon(icon, false);
                Log.v("myicon:", icon);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.CURRENT_CONDITIONS), weather);
                editor.putString(getString(R.string.CURRENT_TEMP), currentTemp);
                editor.putString(getString(R.string.ICON_URl), icon);
                editor.commit();

                buildMenu(sharedPreferences, ab);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("failure", "failure");
            }
        });
    }

    private void buildMenu (SharedPreferences sharedPreferences, ActionBar ab){
        TextView currentCondition = new TextView(MainActivity.this);
        currentCondition.setText(sharedPreferences.getString(getString(R.string.CURRENT_CONDITIONS), "Condition"));
        currentCondition.setPadding(5, 0, 5, 0);
        currentCondition.setTypeface(null, Typeface.BOLD);
        currentCondition.setTextSize(14);
        myMenu.add("Text").setActionView(currentCondition).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        TextView currentTemp = new TextView(MainActivity.this);
        currentTemp.setText(sharedPreferences.getString(getString(R.string.CURRENT_TEMP), "Temperature"));
        currentTemp.setPadding(5, 0, 5, 0);
        currentTemp.setTypeface(null, Typeface.BOLD);
        currentTemp.setTextSize(14);
        myMenu.add("Text").setActionView(currentTemp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        ImageView icon = new ImageView(MainActivity.this);

        // show The Image in a ImageView
        new DownloadImageTask(icon)
                .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");


        icon.setPadding(5, 0, 5, 0);
        myMenu.add("Image").setActionView(icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.weather_warm)));
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


}
