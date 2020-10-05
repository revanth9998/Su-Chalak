package com.suchalak.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suchalak.R;
import com.suchalak.ResponseData;
import com.suchalak.api.ApiService;
import com.suchalak.api.RetroClient;


import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    ProgressDialog pd;
    TextView tv_dob;
    int mYear,mMonth,mDay;
    String DAY,MONTH,YEAR;
    LinearLayout ll_reg_form,ll_sal_per_hour,ll_job_title,ll_company_name,ll_img_upload,ll_provinces;
    Button btn_submit,btn_sal_prev,btn_sal_next,job_previous,job_next,btn_company_prev,btn_company_next,btn_submit_all;
    EditText et_first_name,et_last_name,et_email,et_username,et_password,et_sal,et_job_title,et_company_name;
    Button btn_upload_img,btn_provinces_submit;
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://cegepclm.com/";
    Spinner sp_role;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);



        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        tv_dob=(TextView)findViewById(R.id.tv_dob);
        tv_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datepicker();
            }
        });

        et_first_name=(EditText)findViewById(R.id.et_first_name);

        et_last_name=(EditText)findViewById(R.id.et_last_name);

        et_email=(EditText)findViewById(R.id.et_email);
        et_username=(EditText)findViewById(R.id.et_username);
        et_password=(EditText)findViewById(R.id.et_password);

        sp_role=(Spinner)findViewById(R.id.sp_role);


        btn_upload_img=(Button)findViewById(R.id.btn_upload_img);
        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
            }
        });



        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            uploadImageToServer();



            }
        });



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, RegistrationActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK){
            uri = data.getData();
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, RegistrationActivity.this);
                file = new File(filePath);

            }else{
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    File file;
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, RegistrationActivity.this);
            file = new File(filePath);
            // uploadImageToServer();
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");

    }

    private void uploadImageToServer(){
        pd=new ProgressDialog(RegistrationActivity.this);
        pd.setTitle("Loading");
        pd.show();
        Map<String, String> map = new HashMap<>();
        map.put("fname",et_first_name.getText().toString());
        map.put("lname",et_last_name.getText().toString());
        map.put("email",et_email.getText().toString());
        map.put("dob",tv_dob.getText().toString());

        map.put("pwd",et_password.getText().toString());
        map.put("role",sp_role.getSelectedItem().toString());

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService uploadImage = retrofit.create(ApiService.class);
        Call<ResponseData> fileUpload = uploadImage.userRegistration(fileToUpload, map);
        fileUpload.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                pd.dismiss();
                Toast.makeText(RegistrationActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(RegistrationActivity.this, "Error"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void datepicker() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        DAY = dayOfMonth + "";
                        MONTH = monthOfYear + 1 + "";
                        YEAR = year + "";

                        //tv_dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        tv_dob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
