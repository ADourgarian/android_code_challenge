package com.nerdery.umbrella.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nerdery.umbrella.R;

public class SettingsActivity extends ActionBarActivity {

    TextView zipCode;
    TextView unit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        zipCode = (TextView)findViewById(R.id.zip_code);
        unit = (TextView)findViewById(R.id.unit);

        setText();
    }

    public void setText(){

        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        String defaultValue = getResources().getString(R.string.ZIP_CODE);
        String myZip = sharedPreferences.getString(getString(R.string.ZIP_CODE), defaultValue);

        String defaultValue2 = getResources().getString(R.string.UNIT);
        String myUnit = sharedPreferences.getString(getString(R.string.UNIT), defaultValue2);

        zipCode.setText(myZip);
        unit.setText(myUnit);

        Log.v("zipcode", myZip);


    }

    public void setZip(View views){

        String setZipCode;
        String setUnit;

        setZipCode = zipCode.getText().toString();

        SharedPreferences sharedPreferences = SettingsActivity.this.getSharedPreferences(
                getString(R.string.PREF_FILE), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.ZIP_CODE),setZipCode);
        editor.commit();


    }
}
