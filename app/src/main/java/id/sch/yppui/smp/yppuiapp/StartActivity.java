package id.sch.yppui.smp.yppuiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private TextView txtWelcome, txtYppui;
    private Button SignIn, SignUp;
    private SharedPreferences pref;
    private FrameLayout fm1, fm2;
    private ImageView logo;
    private Animation fromTop, fromBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        if (pref.getBoolean(Constants.IS_LOGGED_IN, false)) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/DancingScript.ttf");
        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        txtYppui = (TextView) findViewById(R.id.txtYppui);
        txtWelcome.setTypeface(myCustomFont);

        logo = (ImageView) findViewById(R.id.imgLogo);
        SignIn = (Button) findViewById(R.id.btnSignIn);
        SignUp = (Button) findViewById(R.id.btnSignUp);
        fm1 = (FrameLayout) findViewById(R.id.fm1);
        fm2 = (FrameLayout) findViewById(R.id.fm2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fm1.setBackgroundResource(android.R.color.transparent);
            fm2.setBackgroundResource(android.R.color.transparent);
            SignIn.setBackgroundResource(R.drawable.signin_ripple);
            SignUp.setBackgroundResource(R.drawable.signup_ripple);
        }

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });

        fromTop = AnimationUtils.loadAnimation(this, R.anim.enter_from_top);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.enter_from_bottom);

        logo.setAnimation(fromTop);
        txtWelcome.setAnimation(fromTop);
        txtYppui.setAnimation(fromTop);
        fm1.setAnimation(fromBottom);
        fm2.setAnimation(fromBottom);
    }
}
