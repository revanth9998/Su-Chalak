package com.suchalak.activities;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected void showMessage(Context cnt,String msg){
        Toast.makeText(cnt, msg, Toast.LENGTH_LONG).show();
    }
}
