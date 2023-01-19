package com.mastik.biology;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Result extends AppCompatActivity {

    TextView result;
    Button home, theori;
    String doc = "fish";
    ScrollView scroll;
    LinearLayout scroller;
    int i = 0, l = 0, answered_quests_count = 0, theme_id = 0;
    Typeface shreft;
    String[][] quests;
    long pressedTime = 0;

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setChoosenTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        shreft = Typeface.createFromAsset(getAssets(), "shreft.ttf");

        home = findViewById(R.id.home);
        theori = findViewById(R.id.open);
        result = findViewById(R.id.totalResult);
        scroll = findViewById(R.id.scroll);
        scroller = findViewById(R.id.scrollLay);

        result.setTypeface(shreft);
        home.setTypeface(shreft);
        theori.setTypeface(shreft);

        if(theme_id == 1) {
            home.setTextColor(Color.parseColor("#cf958a"));
            theori.setTextColor(Color.parseColor("#cf958a"));
            findViewById(R.id.main_back).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,getTheme()));
        }

        String resu = getIntent().getStringExtra("answ");
        String theoriya = getIntent().getStringExtra("theoriya");
        Bundle answrs  = getIntent().getExtras();
        quests = (String[][]) answrs.getSerializable("quests");
        int[] user_answers =answrs.getIntArray("user_answers");

        Toast.makeText(getApplicationContext(),"Загрузка...", Toast.LENGTH_SHORT).show();

        l=0;
        while(l<quests.length && quests[l][0]!=null){
            i=0;
            if(user_answers[l] == 0){
                if(l-1 < 0 || user_answers[l-1] != 0) {
                    TextView dynamicTextView = new TextView(this);
                    dynamicTextView.setText("\n" + (l + 1));
                    dynamicTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    dynamicTextView.setTypeface(shreft);
                    dynamicTextView.setTextSize(20);
                    int j = l;
                    while (j + 1 < quests.length && quests[j+1][0] != null && user_answers[j + 1] == 0) {
                        j++;
                    }
                    if (j != l)
                        dynamicTextView.append("-" + (j + 1) + " Вопрос не отвечен\n");
                    else
                        dynamicTextView.append(". Вопрос не отвечен\n");
                    scroller.addView(dynamicTextView);
                }
            }else
            while(i<answ_indx(l) && quests[l][i+1]!=null){
                TextView dynamicTextView = new TextView(this);
                dynamicTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                dynamicTextView.setText(quests[l][i]);
                if(i!=0) {
                    dynamicTextView.setPadding(convertDpToPixel(20), 0, 0, 0);
                    if (parseInt(quests[l][answ_indx(l)].replaceAll(" ","")) < 10) {
                        if (i == user_answers[l]) {
                            dynamicTextView.setTextColor(getResources().getColor(R.color.red));
                        }
                        if(parseInt(quests[l][answ_indx(l)]) == i){
                            dynamicTextView.setTextColor(getResources().getColor(R.color.gren));
                        }

                    }else{
                        int answer_numb = answ_indx(l);
                        for(int t=0;t<3;t++){
                            int comp = Integer.parseInt(String.valueOf(user_answers[l]).substring(t, t+1));
                            if (i == comp) {
                                dynamicTextView.setTextColor(getResources().getColor(R.color.red));
                                break;
                            }
                        }
                            for(int t=0;t<3;t++){
                                if(parseInt(quests[l][answer_numb].split(" ")[t]) == i) {
                                    if(dynamicTextView.getCurrentTextColor() == getResources().getColor(R.color.red)) {
                                        dynamicTextView.setTextColor(getResources().getColor(R.color.gren));
                                        break;
                                    }else{
                                        dynamicTextView.setTextColor(getResources().getColor(R.color.yellow));
                                    }
                                }
                            }
                    }
                    dynamicTextView.setTypeface(shreft);
                    dynamicTextView.setTextSize(20);

                    scroller.addView(dynamicTextView);
                }else{
                    answered_quests_count++;
                    if(quests[l][0].contains("&")){
                        String[] lines=quests[l][0].split("&");
                        dynamicTextView.setText((l+1) + ". " +lines[0]);
                        SpannableString[] spannableString = new SpannableString[lines.length];
                        for(int y=1;y<lines.length;y++){
                            int number_of_chars = lines[y].split("\n")[0].length();
                            int space_margin = 7;
                            spannableString[y] = new SpannableString("       "+lines[y]);
                            StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
                            spannableString[y].setSpan(italicSpan, space_margin, number_of_chars+space_margin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString[y].setSpan(new RelativeSizeSpan(1.2f), space_margin, number_of_chars+space_margin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            dynamicTextView.append(spannableString[y]);
                        }
                    }else
                        dynamicTextView.setText((l+1) + ". " + quests[l][i]);

                    dynamicTextView.setTypeface(shreft);
                    dynamicTextView.setTextSize(20);

                    scroller.addView(dynamicTextView);

                    if(theoriya.equals("bird") && l>59 && l<70) {

                        ImageView img = new ImageView(this);
                        img.getLayoutParams().height = convertDpToPixel(250);
                        if (l - 59 == 2)
                            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), getResources().getIdentifier("bird1", "drawable", getPackageName())));
                        else
                            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("bird" + (l - 59), "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(theoriya.equals("milk") && l>39 && l<50){

                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("milk"+(l-39), "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(dynamicTextView.getText().toString().contains("Сердечный индекс")){
                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("heart_index", "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(dynamicTextView.getText().toString().contains("Ознакомьтесь с графиком интенсивности метаболизма")){
                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("metabolism_intensity", "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(dynamicTextView.getText().toString().contains("Схематические рисунки 1—4")){
                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("breath_organs", "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(dynamicTextView.getText().toString().contains("температуры окружающей среды на температуру тела лягушки")){
                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("frog", "drawable", getPackageName())));
                        scroller.addView(img);
                    }
                    if(dynamicTextView.getText().toString().contains("температуры окружающей среды на температуру тела собаки")){
                        ImageView img = new ImageView(this);
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("dog", "drawable", getPackageName())));
                        scroller.addView(img);
                    }else
                    if(dynamicTextView.getText().toString().contains("Укажите названия костей (частей скелета)")){
                        ImageView img = new ImageView(this);
                        int number;
                        if(l<10)
                            number = l;
                        else
                            number = parseInt(Integer.toString(l).substring(Integer.toString(l).length()-1));
                        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpToPixel(250)));
                        img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("evo_bones"+(number), "drawable", getPackageName())));
                        scroller.addView(img);
                    }
                }

                i++;

            }
            if(user_answers[l] != 0)
            if(answ_indx(l)<5){

                boolean isLetters= !quests[l][answ_indx(l)].substring(0, 1).matches("\\d+(?:\\.\\d+)?");
                TextView dynamicTextView = new TextView(this);
                dynamicTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if(isLetters){
                    dynamicTextView.setText("Ваш ответ: ");
                    for(int y=0;y<Integer.toString(user_answers[l]).length();y++){
                        char char1 = 'А';
                        if(y!=0) {
                            char1 = dynamicTextView.getText().toString().charAt(dynamicTextView.getText().toString().length() - 2);
                            char1 = (char) (char1 + 1);
                        }
                        dynamicTextView.append(" "+char1+Integer.toString(user_answers[l]).charAt(y));
                    }
                    dynamicTextView.append(";\nПравильный ответ:");
                    for(int y=0;y<quests[l][answ_indx(l)].length()/2;y++){
                        dynamicTextView.append(" "+quests[l][answ_indx(l)].substring(y*2,y*2+2));
                    }
                    dynamicTextView.append(".");
                }else
                    dynamicTextView.setText("Ваш ответ: "+user_answers[l]+";\nПравильный ответ: "+quests[l][answ_indx(l)]+".");
                dynamicTextView.setPadding(convertDpToPixel(20), 0, 0, 0);
                dynamicTextView.setTypeface(shreft);
                dynamicTextView.setTextSize(20);
                if(user_answers[l] == parseInt(quests[l][answ_indx(l)].replaceAll("[^0-9]", "")))
                    dynamicTextView.setTextColor(getResources().getColor(R.color.gren));
                else
                    dynamicTextView.setTextColor(getResources().getColor(R.color.red));
                scroller.addView(dynamicTextView);
            }
            l++;
        }

        switch (theoriya) {
            case "fish":
                doc = "fish";
                break;
            case "ground":
                doc = "ground";
                break;
            case "birds":
                doc = "bird";
                break;
            case "milk":
                doc = "milk";
                break;
            case "presm":
            case "evo":
            case "test":
                setNone(theori);
                break;
        }

        if(resu != null){
            int percent = (Integer.parseInt(resu)*100)/answered_quests_count;
            result.setText("Ваш результат:\n" + resu + " / " + answered_quests_count + "   " + percent + "%");
            String theme_name = theoriya;
            switch(theoriya){
                case "fish": theme_name = "Рыбы"; break;
                case "ground": theme_name = "Земноводные"; break;
                case "birds": theme_name = "Птицы"; break;
                case "milk": theme_name = "Млекопитающие"; break;
                case "presm": theme_name = "Пресмыкающиеся"; break;
                case "evo": theme_name = "Эволюция"; break;
                case "test": theme_name = "Контрольный тест"; break;
            }
            SimpleDateFormat format = new SimpleDateFormat("HH:mm dd.MM", Locale.getDefault());
            fileAppendData("answer_log", theme_name + " " + resu + "/" + answered_quests_count + "  " + percent + "%split" + format.format(System.currentTimeMillis()));
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Activity = new Intent(Result.this, MainActivity.class);
                startActivity(Activity);
            }
        });
        theori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Activity = new Intent(Result.this, pdfView.class);

                Activity.putExtra("docName", doc);
                startActivity(Activity);
            }
        });
    }
    public void setNone(Button to_set_none){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) to_set_none.getLayoutParams();
        layoutParams.weight=0;
        layoutParams.width=0;
        to_set_none.setLayoutParams(layoutParams);
    }
    public int convertDpToPixel(float dp){
        return Math.round(dp * ((float) getApplicationContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public int answ_indx(int quest_number){
        int i=quests[quest_number].length-1;
        while(quests[quest_number][i]==null){
            i--;
        }
        return i;
    }
    private void fileAppendData(String file_name, String data){
        writeData(file_name, readData(file_name) + data + "\n");
    }
    private void writeData(String file_name, String data)
    {
        try
        {
            FileOutputStream fos = openFileOutput(file_name+".txt", Context.MODE_PRIVATE);
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
                text.append(line).append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e("Error!", "Error occured while reading text file from Internal Storage!");
        }

        return text.toString();
    }
    private void setChoosenTheme(){

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
                if(temp.toString().equals("1")) {
                    setTheme(R.style.Theme_Biology_Konst);
                    theme_id = 1;
                }else{
                    setTheme(R.style.Theme_Biology);
                    theme_id = 0;
                }
            else
                setTheme(R.style.Theme_Biology);

            fin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}