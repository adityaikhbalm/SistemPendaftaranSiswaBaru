package id.sch.yppui.smp.yppuiapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.firebase.messaging.FirebaseMessaging;

import id.sch.yppui.smp.yppuiapp.models.ServerRequest;
import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import id.sch.yppui.smp.yppuiapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText editEmail, editPassword;
    private Button btnLogin, btnDaftar;
    private ProgressDialog mLoginProgress;
    private SharedPreferences pref;
    private FrameLayout fm1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        mLoginProgress = new ProgressDialog(this);
        mToolbar = (Toolbar) findViewById(R.id.menu_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnDaftar = (Button) findViewById(R.id.btnDaftarSekarang);

        fm1 = (FrameLayout) findViewById(R.id.fm1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fm1.setBackgroundResource(android.R.color.transparent);
            btnLogin.setBackgroundResource(R.drawable.login_ripple);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Mohon Tunggu");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginProcess(email,password);
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), "Semua field harus di isi!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginProcess(String email,String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(findViewById(android.R.id.content), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.ID,resp.getUser().getId());
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NAME,resp.getUser().getName());
                    editor.apply();

                    FirebaseMessaging.getInstance().subscribeToTopic(pref.getString(Constants.ID,""));
                    FirebaseMessaging.getInstance().subscribeToTopic("status");

                    mLoginProgress.dismiss();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    mLoginProgress.hide();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                mLoginProgress.hide();
                Snackbar.make(findViewById(android.R.id.content), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
