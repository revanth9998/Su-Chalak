package com.suchalak.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suchalak.R;
import com.suchalak.ResponseData;
import com.suchalak.api.ApiService;
import com.suchalak.api.RetroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText et_enter_email;
    Button bt_submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_enter_email=(EditText)findViewById(R.id.et_enter_email);
        bt_submit=(Button)findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitdata();
                //Toast.makeText(ForgotPasswordActivity.this, "fdfdfdf", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public  void submitdata() {
        String email=et_enter_email.getText().toString();
        ApiService apiService = RetroClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseData> call = apiService.forgotpassword(email);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.body().message.equals("true")) {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                    startActivity(intent);
                    //Log.i("msg", "" + response.body().message);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("TAG","Response = "+t.toString());
            }
        });
    }
    @Override                                                                                                                    //add this method in your program
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
