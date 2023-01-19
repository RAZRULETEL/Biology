package com.mastik.biology;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Settings extends AppCompatActivity {

    Button open_log;
    ImageButton style,style1;
    SwitchCompat switch_system_keyboard, switch_random;
    FirebaseAuth mAuth, mAuth2;
    FirebaseFirestore db;
    TextView keys, numeration, unactivated_keys;
    LinearLayout key_gen_view, quest_answer_log;
    DisplayMetrics displayMetrics;
    ActionBar actionBar;
    private int keys_number = 0, theme_id = 0;
    String group_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        setTheme();
        setContentView(R.layout.activity_settings);

        if(theme_id == 1) {
            findViewById(R.id.scrollView3).setBackgroundColor(Color.parseColor("#bbddf8"));
            findViewById(R.id.key_gen_view).setBackgroundColor(Color.parseColor("#bbddf8"));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy, MM, dd", Locale.getDefault());

        displayMetrics = getResources().getDisplayMetrics();

        quest_answer_log = findViewById(R.id.quest_answer_log);
        quest_answer_log.setTranslationX(-displayMetrics.widthPixels);
        key_gen_view = findViewById(R.id.key_gen_view);
        key_gen_view.setTranslationX(-displayMetrics.widthPixels);
        unactivated_keys = findViewById(R.id.unactivated_keys);

        style = findViewById(R.id.style1);
        style1 = findViewById(R.id.style2);

        switch_system_keyboard = findViewById(R.id.switchKeyboard);
        open_log = findViewById(R.id.openLog);
        switch_random = findViewById(R.id.switchRandom);

        if(theme_id == 1)
            open_log.setTextColor(Color.parseColor("#cf958a"));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if(!FirebaseApp.getApps(getApplicationContext()).toString().contains("key_reg")) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("biology-mastik")
                    .setApplicationId("1:746834036930:android:c115c739616102db8fd35b")
                    .setApiKey("AIzaSyDxrEVLqEq3ptD19kPG-f8IZI1WORADhOw")
                    .build();
            FirebaseApp.initializeApp(getApplicationContext(), options, "key_reg");
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("key_reg"));
        }

        actionBar.setDisplayHomeAsUpEnabled(true);


        style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeData(0,"themes");
                System.exit(0);
            }
        });
        style1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeData(1,"themes");
                System.exit(0);
            }
        });
        switch_random.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    writeData(1,"random");
                else
                    writeData(0,"random");
            }
        });
        open_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quest_answer_log.animate().translationX(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                LinearLayout log_view = findViewById(R.id.log_scroll);
                if(log_view.getChildCount() == 0){
                    String[] results = readData("answer_log").split("\n");
                    if(results[0].length() > 1)
                    for(int i = 0; i < results.length; i++){
                            TextView this_result = new TextView(getApplicationContext());
                            this_result.setTextSize(24);
                            this_result.setText(results[i].split("split")[0]);
                            TextView time = new TextView(getApplicationContext());
                            time.setTextSize(24);
                            time.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                            time.setPadding(0, 0, 30, 0);
                            time.setText(results[i].split("split")[1]);
                            log_view.addView(this_result);
                            log_view.addView(time);
                            if(i < results.length - 1){
                                TextView border = new TextView(getApplicationContext());
                                border.setTextSize(1);
                                border.setBackgroundColor(Color.GRAY);
                                log_view.addView(border);
                            }
                        }
                    else{
                        TextView no_logs = new TextView(getApplicationContext());
                        no_logs.setTextSize(24);
                        no_logs.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, displayMetrics.heightPixels-200));
                        no_logs.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        no_logs.setGravity(Gravity.CENTER);
                        no_logs.setText("История пуста");
                        log_view.addView(no_logs);
                    }
                }

            }
        });
        switch_system_keyboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    writeData(1,"keyboard");
                else
                    writeData(0,"keyboard");
            }
        });
        if (readData("keyboard").replaceAll("[^0-9]", "").equals("1"))
            switch_system_keyboard.setChecked(true);

        if (readData("themes").replaceAll("[^0-9]", "").equals("0"))
            style.setEnabled(false);
        else
            style1.setEnabled(false);

        if (readData("random").replaceAll("[^0-9]", "").equals("1"))
            switch_random.setChecked(true);

        if(mAuth.getCurrentUser().getEmail().contains("bio.admin") || mAuth.getCurrentUser().getEmail().contains("mipt")){
            SpannableStringBuilder keys_builder = new SpannableStringBuilder();
            switch_random.setVisibility(View.VISIBLE);
            Button open_key_gen = findViewById(R.id.open_key_gen);
            open_key_gen.setText("Генератор ключей");
            open_key_gen.setTextSize(16);
            open_key_gen.setVisibility(View.VISIBLE);
            if(theme_id == 1)
                open_key_gen.setTextColor(Color.parseColor("#cf958a"));
            open_key_gen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    key_gen_view.animate().translationX(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }
            });
            Button gen_keys = findViewById(R.id.generate_keys);
            if(theme_id == 1)
                gen_keys.setTextColor(Color.parseColor("#cf958a"));
            gen_keys.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(((EditText)findViewById(R.id.number_of_keys)).getText().toString().contains(".") || ((EditText)findViewById(R.id.key_length)).getText().toString().contains(".")){
                        Toast.makeText(getApplicationContext(), "Количество ключей и длина должны быть целыми числами", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int number = Integer.parseInt(((EditText)findViewById(R.id.number_of_keys)).getText().toString());
                    int length = Integer.parseInt(((EditText)findViewById(R.id.key_length)).getText().toString());

                    if(length < 3){
                        Toast.makeText(getApplicationContext(), "Длина ключей должна быть как минимум 4 символа", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(length > 20){
                        Toast.makeText(getApplicationContext(), "Длина ключей слишком велика", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(number > 24){
                        Toast.makeText(getApplicationContext(), "Не стоит создавать слишком много ключей", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(keys == null) {
                        Button share_keys = findViewById(R.id.share_keys);
                        share_keys.setVisibility(View.VISIBLE);

                        Button gen_group_key = findViewById(R.id.generate_group_key);
                        gen_group_key.setVisibility(View.VISIBLE);

                        TextView gen_title = findViewById(R.id.gen_title);
                        gen_title.setVisibility(View.VISIBLE);

                        LinearLayout keys_scroll = findViewById(R.id.keys_generated);
                        LinearLayout numeration_scroll = findViewById(R.id.numeration);
                        keys = new TextView(getApplicationContext());
                        keys.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        keys.setTextIsSelectable(true);
                        keys.setTextSize(24);
                        keys_scroll.addView(keys);
                        numeration = new TextView(getApplicationContext());
                        numeration.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        numeration.setTextSize(24);
                        numeration_scroll.addView(numeration);
                        numeration_scroll.getLayoutParams().width = displayMetrics.widthPixels/2;
                    }else{
                        keys_builder.append("\n");
                        numeration.append("\n");
                    }
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    for(int i = 0; i < number; i++){
                        String key = "";
                        for(int l = 0; l < length; l++){
                            key += (char)(97 + new Random().nextInt(26));
                        }
                        int finalI = i;

                        mAuth2.createUserWithEmailAndPassword(key+"@whateverdomain.com", key)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    int start_pos = keys_builder.length();
                                    if(task.isSuccessful()) {
                                        keys_builder.append(task.getResult().getUser().getEmail().split("@")[0]);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("expires_in", format.format(System.currentTimeMillis()+31_536_000_000f));
                                        data.put("activated", false);
                                        db.collection("keys").document(task.getResult().getUser().getEmail().split("@")[0]).set(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            keys_builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gren)),start_pos, keys_builder.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                                                            keys.setText(keys_builder);
                                                        }
                                                    }
                                                });
                                    }else
                                        keys_builder.append("Ошибка создания");
                                    keys_builder.setSpan(new ForegroundColorSpan(Color.RED),start_pos, keys_builder.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                                    keys.setText(keys_builder);
                                    mAuth2.signOut();
                                    numeration.append(++keys_number+".");
                                    if (finalI < number - 1){
                                        keys_builder.append("\n");
                                        numeration.append("\n");
                                    }
                                }
                            });
                    }
                }
            });
            Button share_keys = findViewById(R.id.share_keys);
            if(theme_id == 1)
                share_keys.setTextColor(Color.parseColor("#cf958a"));

            share_keys.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                    sharingIntent.setType("text/plain");

                    String shareBody = keys.getText().toString();

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Отправить с помощью"));
                }
            });
            Button gen_group_key = findViewById(R.id.generate_group_key);
            if(theme_id == 1)
                gen_group_key.setTextColor(Color.parseColor("#cf958a"));

            gen_group_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("gen");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this, R.style.AlertDialogCustom);
                    if(group_key.equals(""))
                        for(int l = 0; l < 6; l++)
                            group_key += (char)(97 + new Random().nextInt(26));
                    List<String> keys_arr = Arrays.asList(keys_builder.toString().split("\n"));
                    Map<String, Object> data = new HashMap<>();
                    data.put("keys", keys_arr);
                    data.put("expires_in", format.format(System.currentTimeMillis()+86_400_000));
                    db.collection("keys").document("group."+group_key).set(data, SetOptions.merge())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        builder.setMessage("Ключ: group."+group_key)
                                                .setTitle("Вы создали групповой ключ")
                                                .setPositiveButton("Закрыть", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        builder.show();
                                        unactivated_keys.setVisibility(View.VISIBLE);
                                        checkKeys("group."+group_key);
                                    }
                                }
                            });

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        if(key_gen_view.getTranslationX() >= -1 || quest_answer_log.getTranslationX() >= -1) {
            key_gen_view.animate().translationX(-displayMetrics.widthPixels).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).start();
            quest_answer_log.animate().translationX(-displayMetrics.widthPixels).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).start();
        }else
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(key_gen_view.getTranslationX() >= -1 || quest_answer_log.getTranslationX() >= -1) {
                key_gen_view.animate().translationX(-displayMetrics.widthPixels).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).start();
                quest_answer_log.animate().translationX(-displayMetrics.widthPixels).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).start();
            }else
                this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeData(int themeNUmber, String value)
    {
        try
        {
            FileOutputStream fos = openFileOutput(value+".txt", Context.MODE_PRIVATE);
            String data = Integer.toString(themeNUmber);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private String readData(String file_name){

        StringBuilder text = new StringBuilder();


        try {


            FileInputStream fIS = getApplicationContext().openFileInput(file_name+".txt");
            InputStreamReader isr = new InputStreamReader(fIS, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                text.append(line + '\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e("Error!", "Error occured while reading text file from Internal Storage!");
        }
        if(text.toString().equals(""))
            return "0";
        return text.toString();
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
                        setTheme(R.style.Theme_Biology_Konst_Settings);
                        GradientDrawable back = new GradientDrawable();
                        back.setColor(Color.parseColor("#cf958a"));
                        actionBar.setBackgroundDrawable(back);
                        setStatusBarColor("#000000");
                        theme_id = 1;
                        break;
                    case "0":
                        setTheme(R.style.Theme_Biology_Settings);
                        break;
                }
            else{
                setTheme(R.style.Theme_Biology_Settings);
            }
            fin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void setStatusBarColor(String color){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor(color));
    }
    private void checkKeys(String group_key){
        db.collection("keys").document(group_key).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            JSONArray keys = new JSONArray(task.getResult().get("keys").toString());
                            int unActKeys = keys.length();
                            for(int i = 0; i < keys.length(); i++){
                                if(task.getResult().contains(keys.get(i).toString()))
                                    unActKeys--;
                            }
                            unactivated_keys.setText("Не активированных ключей: "+unActKeys);
                            new Thread(()->{
                                try {
                                    Thread.sleep(1000);
                                    checkKeys(group_key);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}