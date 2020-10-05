package com.suchalak.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.suchalak.R;
import com.suchalak.ResponseData;
import com.suchalak.Utils;
import com.suchalak.api.ApiService;
import com.suchalak.api.RetroClient;
import com.suchalak.model.EditProfilePojo;

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

public class EditProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    EditText et_first_name,et_lastname,et_pass,et_username;
    ProgressDialog progressDialog;
    List<EditProfilePojo> a1;
    SharedPreferences sharedPreferences;
    String session;
    ResponseData a2;
    TextView tv_dob;
    int mYear,mMonth,mDay;
    String DAY,MONTH,YEAR;
    Button bt_update,btn_upload_img;
    ImageView image_view;
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://paytracker.ca/";
    private Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Utils.SHREF, Context.MODE_PRIVATE);
        session = sharedPreferences.getString("uname", "def-val");

        tv_dob=(TextView)findViewById(R.id.tv_dob);
        tv_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datepicker();
            }
        });

        et_first_name=(EditText)findViewById(R.id.et_first_name);
        et_lastname=(EditText)findViewById(R.id.et_lastname);
        et_pass=(EditText)findViewById(R.id.et_pass);
        et_username=(EditText)findViewById(R.id.et_username);
/*

        et_password=(EditText)findViewById(R.id.et_password);
        et_sal=(EditText)findViewById(R.id.et_sal);
        et_job_title=(EditText)findViewById(R.id.et_job_title);
        et_company_name=(EditText)findViewById(R.id.et_company_name);*/

        image_view=(ImageView)findViewById(R.id.image_view);

        et_username.setText(session);

        bt_update=(Button)findViewById(R.id.bt_update);
        btn_upload_img=(Button)findViewById(R.id.btn_upload_img);
        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                openGalleryIntent.setType("image/*");
                startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);

            }
        });

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToServer();

            }
        });
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiService service = RetroClient.getRetrofitInstance().create(ApiService.class);
        Call<List<EditProfilePojo>> call = service.get_user_profile(session);

        call.enqueue(new Callback<List<EditProfilePojo>>() {
            @Override
            public void onResponse(Call<List<EditProfilePojo>> call, Response<List<EditProfilePojo>> response) {

                progressDialog.dismiss();
                a1 = response.body();
                EditProfilePojo user = a1.get(0);
                et_first_name.setText(user.getFirstname());
                et_lastname.setText(user.getLastname());
                et_pass.setText(user.getPass());
                tv_dob.setText(user.getDob());
                Glide.with(EditProfileActivity.this).load(a1.get(0).getProfile_pic()).into(image_view);

            }

            @Override
            public void onFailure(Call<List<EditProfilePojo>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, EditProfileActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK){
            uri = data.getData();
            if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, EditProfileActivity.this);
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
            String filePath = getRealPathFromURIPath(uri, EditProfileActivity.this);
            file = new File(filePath);
            // uploadImageToServer();
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");

    }

    private void uploadImageToServer(){
        progressDialog=new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        map.put("fname",et_first_name.getText().toString());
        map.put("email",session);
        map.put("lname",et_lastname.getText().toString());
        map.put("dob",tv_dob.getText().toString());
        map.put("pwd",et_pass.getText().toString());



        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService uploadImage = retrofit.create(ApiService.class);
        Call<ResponseData> fileUpload = uploadImage.user_update_profile(fileToUpload, map);
        fileUpload.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                //finish();
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Error"+t.getMessage(), Toast.LENGTH_LONG).show();
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
    @Override
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
