package com.mastik.biology;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageButton[] docs=new ImageButton[4];
    ImageButton settings;
    Button[] fish=new Button[7];
    TextView choose;
    int i,theme;
    Typeface shreft;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getApplicationContext(), "Пожалуйста авторизуйтесь", Toast.LENGTH_SHORT).show();
            Intent Activity = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(Activity);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shreft = Typeface.createFromAsset(getAssets(),"shreft.ttf");

        settings = findViewById(R.id.imageButtonSettings);
        choose = findViewById(R.id.textView);
        choose.setTypeface(shreft);

        for(i=1; i<fish.length+1; i++){

            String radioID = "baton" + (i);

            int radResID = getResources().getIdentifier(radioID, "id", getPackageName());
            fish[i-1] = (findViewById(radResID));
            fish[i-1].setTypeface(shreft);

            fish[i-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Activity = new Intent(MainActivity.this, Questions.class);
                    Activity.putExtra("theme", getResources().getResourceEntryName(view.getId()));
                    startActivity(Activity);
                }
            });


        }

        for(i=1; i<docs.length+1; i++){
            String radioID = "doc" + (i);

            int radResID = getResources().getIdentifier(radioID, "id", getPackageName());
            docs[i-1] = (findViewById(radResID));

            docs[i-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Activity = new Intent(MainActivity.this, pdfView.class);
                    String doc="fish";
                    switch (getResources().getResourceEntryName(view.getId())) {
            case "doc1":
                doc = "fish";
                break;
            case "doc2":
                doc = "ground";
                break;
            case "doc3":
                doc = "bird";
                break;
            case "doc4":
                doc = "milk";
                break;
        }
                    Activity.putExtra("docName", doc);
                    startActivity(Activity);
                }
            });
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active on device
                choose.setTextColor(getResources().getColor(R.color.black));

                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active on device
                choose.setTextColor(getResources().getColor(R.color.white));
                for(i=1; i<fish.length+1; i++){
                    fish[i-1].setTextColor(getResources().getColor(R.color.white));
                }
                break;
        }
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Activity = new Intent(MainActivity.this, Settings.class);
                startActivity(Activity);
            }
        });
        colorOfElements(theme);
}
    private void setTheme(){

        try
        {
            FileInputStream fin = openFileInput("themes.txt");
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1)
            {
                temp.append((char)a);
            }

            if(!temp.toString().equals(""))
            switch(temp.toString()){
                case "1":
                    setTheme(R.style.Theme_Biology_Konst);
                    setStatusBarColor("#000000");
                    theme=1;
                break;
                case "0":
                    setTheme(R.style.Theme_Biology);
                break;
            }
            else{
                setTheme(R.style.Theme_Biology);
            }
            fin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void colorOfElements(int themeNumber){
        if(themeNumber==1){
            findViewById(R.id.layout).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,getTheme()));
            for(i=1; i<fish.length+1; i++){
                fish[i-1].setTextColor(Color.parseColor("#cc938a"));
            }

            for(i=0;i<4;i++){
                GradientDrawable style = new GradientDrawable();
                style.setCornerRadius(100);
                style.setColor(getResources().getColor(R.color.doc_button));
                docs[i].setBackground(style);
                docs[i].requestLayout();
            }
            choose.setTextColor(getResources().getColor(R.color.white));
        }
    }
    public void setStatusBarColor(String color){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor(color));
    }
}