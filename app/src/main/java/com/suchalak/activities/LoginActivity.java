package com.suchalak.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.suchalak.R;
import com.suchalak.ResponseData;
import com.suchalak.Utils;
import com.suchalak.api.ApiService;
import com.suchalak.api.RetroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button bt_signin;
    EditText et_uname,et_pwd;
    TextView tv_forget_pass,bt_signup,tv_driver;
    Spinner sp_role;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        bt_signup=(TextView) findViewById(R.id.bt_signup);
        et_uname=(EditText)findViewById(R.id.et_uname);
        et_pwd=(EditText)findViewById(R.id.et_pwd);

        sp_role=(Spinner)findViewById(R.id.sp_role);
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);

            }
        });
        bt_signin=(Button)findViewById(R.id.bt_signin);
        bt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);*/
                loginData();
            }
        });
        tv_forget_pass=(TextView)findViewById(R.id.tv_forget_pass);
        tv_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);

            }
        });

        tv_driver=(TextView)findViewById(R.id.tv_driver);
        tv_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,DriverLoginActivity.class);
                startActivity(intent);
            }
        });
    }
    ProgressDialog pd;
    public  void loginData() {
        pd= new ProgressDialog(LoginActivity.this);
        pd.setTitle("Loading...");
        pd.show();
        ApiService apiService = RetroClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseData> call = apiService.userLogin(et_uname.getText().toString(),et_pwd.getText().toString(),sp_role.getSelectedItem().toString());
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                pd.dismiss();
                if (response.body().status.equals("true")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor et=sharedPreferences.edit();
                    et.putString("uname",et_uname.getText().toString());
                    et.commit();
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
