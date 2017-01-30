package com.example.nejc.predvajalnikselected;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Nejc on 30. 01. 2017.
 */

public class Nastavitve extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nastavitve);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }
}
