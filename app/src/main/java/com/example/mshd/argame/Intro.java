package com.example.mshd.argame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mshd on 2016-11-17.
 */

public class Intro extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
