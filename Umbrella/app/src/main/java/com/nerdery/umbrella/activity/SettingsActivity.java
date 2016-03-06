package com.nerdery.umbrella.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nerdery.umbrella.R;

public class SettingsActivity extends ActionBarActivity {

    TextView zipCode;
    TextView unit;
    String murica;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        zipCode = (TextView)findViewById(R.id.zip_code);

        setText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);

        return true;
    }

    public void setText(){

        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        String defaultValue = getResources().getString(R.string.ZIP_CODE);
        String myZip = sharedPreferences.getString(getString(R.string.ZIP_CODE), defaultValue);

        zipCode.setText(myZip);

        Log.v("zipcode", myZip);


    }

    public void setZip(View views){

        String setZipCode;

        setZipCode = zipCode.getText().toString();

        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.ZIP_CODE), setZipCode);
        editor.putString(getString(R.string.MURICA), murica);
        editor.commit();

        Log.v("bool", sharedPreferences.getString(getString(R.string.MURICA),"asdf"));


    }

    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_fahrenheit:
                if (checked)
                    murica = "yes";
                    break;
            case R.id.radio_celsius:
                if (checked)
                    murica = "no";
                    break;
        }
    }
}
