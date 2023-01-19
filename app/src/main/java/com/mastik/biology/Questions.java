package com.mastik.biology;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

public class Questions extends AppCompatActivity {

    RadioGroup grp;
    final RadioButton[] radiob = new RadioButton[5];
    final CheckBox[] checkb = new CheckBox[6];
    TextView question;
    Button submit, clear;
    ImageButton backspace;
    final Button[] numbers = new Button[9];
    int i, l=0, correct=0, answ, kolvo, theme_id=0, radio_indx=-1;
    int[] answers, user_answers;
    String radioTheme, themeColor="#986655", checkedTheme = "#956453";
    boolean[] quests_answered;
    boolean someAnswers = false, answerShowingNow = false, keyboard=false;
    ImageView img;
    GradientDrawable style;
    Typeface shreft;
    EditText line;
    TextWatcher textMask;
    int maxQuestionCount = 100, maxEvoCount = 250;

    String[][] test=new String[maxQuestionCount][8];

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        style = new GradientDrawable();
        style.setCornerRadius(50);
        style.setColor(Color.parseColor(themeColor));
        question.setBackground(style);
        for(int y=0;y<5;y++){
            style = new GradientDrawable();
            style.setCornerRadius(50);
            style.setColor(Color.parseColor(themeColor));
            if(!radiob[y].isChecked())
                radiob[y].setBackground(style);
        }
    }

    @Override
    public void onBackPressed() {

        if(!areAllFalse(quests_answered)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Questions.this, R.style.AlertDialogCustom);
            builder.setMessage("Вы можете закончить отвечать и перейти к результатам, или выйти. Ответ в текущем вопросе засчитан не будет.")
                    .setTitle("Вы попытались выйти")
                    .setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("Закончить", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            cancelQuest();
                        }
                    });
            builder.show();
        }else
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setChoosenTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        keyboard = readSourceData("keyboard").equals("1") ? true : false;

        shreft = Typeface.createFromAsset(getAssets(), "shreft.ttf");
        line = findViewById(R.id.textAnswer);
        clear = findViewById(R.id.clear);
        backspace = findViewById(R.id.backspace);
        clear.setTypeface(shreft);


        img = findViewById(R.id.imageView);
        question = findViewById(R.id.question);
        submit = findViewById(R.id.go);
        question.setTypeface(shreft);
        submit.setTypeface(shreft);


        String resu = getIntent().getStringExtra("theme");
        if(resu!=null){
            try {
        switch (resu) {
            case "baton1":
                read("fish");
                radioTheme = "fish";
                themeColor = "#986655";
                checkedTheme = "#835C4E";
                img.setMaxHeight(0);
                break;
            case "baton2":
                read("ground");
                radioTheme = "ground";
                themeColor = "#2941f1";
                checkedTheme = "#2E42D8";
                img.setMaxHeight(0);
                break;
            case "baton3":
                read("presm");
                radioTheme = "presm";
                themeColor = "#6429f1";
                checkedTheme = "#5F2ED5";
                img.setMaxHeight(0);
                break;
            case "baton4":
                read("birds");
                radioTheme = "birds";
                themeColor = "#51c8aa";
                checkedTheme = "#4FB89E";
                break;
            case "baton5":
                read("milk");
                radioTheme = "milk";
                themeColor = "#5bcb23";
                checkedTheme = "#5BBA2C";
                break;
            case "baton6":
                test=new String[maxEvoCount][8];
                read("evo");
                radioTheme = "evo";
                themeColor = "#f16e29";
                checkedTheme = "#D56B33";
                break;
            case "baton7":
                ControlTest();
                radioTheme = "test";
                themeColor = "#867f67";
                checkedTheme = "#756F5D";
                break;
        }} catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "Error file reading",
                    Toast.LENGTH_SHORT)
                    .show();
        }}
        setStatusBarColor(themeColor);
        if(theme_id==1){
            themeColor="#80FFFFFF";
            checkedTheme="#979797";
        }
        style = new GradientDrawable();
        style.setCornerRadius(50);
        style.setColor(Color.parseColor(themeColor));
        question.setBackground(style);
        submit.setBackgroundColor(Color.parseColor(themeColor));

        GradientDrawable checkedStyle = new GradientDrawable();
        checkedStyle.setCornerRadius(50);
        checkedStyle.setColor(Color.parseColor(checkedTheme));

        for(i=1;i<7;i++) {
            checkb[i-1] = findViewById(getResources().getIdentifier("check" + (i), "id", getPackageName()));
            checkb[i-1].setTypeface(shreft);
            checkb[i-1].setBackground(style.mutate());

            checkb[i-1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    style = new GradientDrawable();
                    style.setCornerRadius(50);
                    style.setColor(Color.parseColor(themeColor));
                    question.setBackground(style);
                    if(b){
                        findViewById(compoundButton.getId()).setBackground(checkedStyle);
                        findViewById(compoundButton.getId()).setMinimumHeight(WRAP_CONTENT);
                    }else{
                        findViewById(compoundButton.getId()).setBackground(style);
                        findViewById(compoundButton.getId()).setMinimumHeight(WRAP_CONTENT);
                    }
                }
            });
        }
        for(i=1;i<6;i++){
            style = new GradientDrawable();
            style.setCornerRadius(50);
            style.setColor(Color.parseColor(themeColor));
            radiob[i-1] = findViewById(getResources().getIdentifier("radio" + (i), "id", getPackageName()));

            radiob[i-1].setTypeface(shreft);
            radiob[i-1].setBackground(style.mutate());
        }
        for(i=0;i<9;i++) {
            numbers[i] = findViewById(getResources().getIdentifier("number" + (i+1), "id", getPackageName()));
            numbers[i].setTypeface(shreft);
            numbers[i].setTextColor(Color.parseColor("#ffffff"));
            numbers[i].setBackground(style.mutate());
            numbers[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    line.append(getResources().getResourceEntryName(view.getId()).substring(6,7));
                }
            });
        }
        if(kolvo>100)
                Toast.makeText(getApplicationContext(),
                "Тут всего лишь "+kolvo+" вопросов",
                500)
                .show();
        else
            Toast.makeText(getApplicationContext(),
                    "Тут "+kolvo+" вопросов",
                    500)
                    .show();
        grp = findViewById(R.id.RadioGroup);
        if(kolvo>0){
            quests_answered = new boolean[kolvo];
            user_answers = new int[kolvo];

            l = new Random().nextInt(kolvo);
            quests_answered[l] = true;
            setRandomQuest();
        }



        grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        style = new GradientDrawable();
                        style.setCornerRadius(50);
                        style.setColor(Color.parseColor(themeColor));
                        question.setBackground(style);
                        for(i=0;i<5;i++){
                            radiob[i].setBackground(style.mutate());
                            radiob[i].setMinimumHeight(WRAP_CONTENT);
                        }
                        if(checkedId > 0) {
                            findViewById(checkedId).setBackground(checkedStyle);
                            findViewById(checkedId).setMinimumHeight(WRAP_CONTENT);
                        }
                        radio_indx=checkedId;
                    }

                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question.getLayoutParams().height= WRAP_CONTENT;
                boolean isLetters=false;
                if(!test[l][answ_indx(l)].substring(0,1).matches("\\d+(?:\\.\\d+)?"))
                    isLetters=true;
                if(!someAnswers){
                    answ = parseInt(test[l][answ_indx(l)].replaceAll("[^0-9]", ""));
                }else{
                    answers = new int[test[l][answ_indx(l)].split(" ").length];
                    for(i=0;i<test[l][answ_indx(l)].split(" ").length;i++)
                        answers[i] = parseInt(test[l][answ_indx(l)].split(" ")[i]);
                }

                i=0;
                int check=0;
                if(!isLetters)
                if(radio_indx==-1 && line.getText().toString().matches("")){
                    while (i < answ_indx(l) - 1) {
                        if (checkb[i].isChecked()) {
                            check++;
                        }
                        i++;


                    }
                    if (check != 3) {
                        if (check > 3) {
                            Toast.makeText(getApplicationContext(),
                                    "Вы указали более трёх вариантов ответа ",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Пожалуйста, поделитесь ответом ",
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                    }
                }
                if(!answerShowingNow)
                if(isLetters)
                    if(line.getText().toString().length() < 3 || line.getText().toString().charAt(line.getText().toString().length()-2) != test[l][answ_indx(l)].charAt(test[l][answ_indx(l)].length()-2)){
                        Toast.makeText(getApplicationContext(),
                                "Вы указали некорректное значение 1 ",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }else;
                else
                    if(answ_indx(l)<5 && line.getText().toString().length() != test[l][answ_indx(l)].length() && !answerShowingNow){
                        Toast.makeText(getApplicationContext(),
                                "Вы указали некорректное значение 2",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                if(!areAllTrue(quests_answered)) {
                    if (answerShowingNow){
                        answerShowingNow=false;
                        submit.setText("Подтвердить");
                        for(i=0;i<5;i++) {
                            radiob[i].setClickable(true);
                            checkb[i].setClickable(true);
                        }
                        checkb[5].setClickable(true);
                        if (!someAnswers) {
                            if(answ_indx(l)<5){
                                if(line.getText().toString().replaceAll(" ", "").matches(test[l][answ_indx(l)]))
                                    correct++;

                            }else {
                                if (radiob[answ - 1].isChecked())
                                    correct++;
                                for (i = 0; i < 5; i++) {
                                    if (radiob[i].isChecked()) {
                                        user_answers[l] = i + 1;

                                        break;
                                    }
                                }
                            }
                        } else {
                            int corectCheck = 0;
                            for (i = 0; i < 6; i++) {
                                if (checkb[i].isChecked()) {
                                    user_answers[l] = i + 1 + user_answers[l] * 10;

                                    for (int o = 0; o < answers.length; o++) {
                                        if (i + 1 == answers[o]) {
                                            corectCheck++;
                                            break;
                                        }
                                    }
                                    checkb[i].setChecked(false);
                                }
                            }
                            if (corectCheck == answers.length) {
                                correct++;
                            }
                        }
                    grp.clearCheck();
                    if(readSourceData("switch_random").equals("1")){
                        l++;
                    }else
                        while (quests_answered[l] || test[l][0] == null) {
                            l = new Random().nextInt(kolvo);
                        }
                    quests_answered[l] = true;
                    setRandomQuest();
                    }else{
                        line.setEnabled(false);
                        answerShowingNow=true;
                        submit.setText("Продолжить");
                        GradientDrawable new_col_r = new GradientDrawable();
                        GradientDrawable new_col_g = new GradientDrawable();
                        GradientDrawable new_col_y = new GradientDrawable();
                        new_col_r.setColor(getResources().getColor(R.color.red));
                        new_col_r.setCornerRadius(50);
                        new_col_g.setColor(getResources().getColor(R.color.gren));
                        new_col_g.setCornerRadius(50);
                        new_col_y.setColor(getResources().getColor(R.color.yellow));
                        new_col_y.setCornerRadius(50);
                        if(answ_indx(l)<5){
                            user_answers[l]=Integer.parseInt(line.getText().toString().replaceAll("[^0-9]", ""));
                            for (i = 0; i < 9; i++)
                                numbers[i].setClickable(false);
                            clear.setClickable(false);
                            backspace.setClickable(false);
                            if(line.getText().toString().replaceAll(" ", "").matches(test[l][answ_indx(l)])) {
                                line.setTextColor(Color.parseColor("#00C800"));
                            }else{
                                line.setText("Ваш ответ: "+line.getText().toString() + ".");
                                TextView miss = findViewById(R.id.youMissed);
                                miss.getLayoutParams().height=WRAP_CONTENT;
                                miss.setTextColor(Color.parseColor("#00C800"));
                                if(isLetters){
                                    miss.setText("Правильный ответ: ");
                                    for(int y=0;y<test[l][answ_indx(l)].length()/2-1;y++){
                                        miss.append(test[l][answ_indx(l)].substring(y*2,y*2+2)+" ");
                                    }
                                    miss.append(test[l][answ_indx(l)].substring(test[l][answ_indx(l)].length()-2,test[l][answ_indx(l)].length())+".");
                                }
                                else
                                    miss.setText("Правильный ответ: "+test[l][answ_indx(l)]+".");
                                line.setTextColor(Color.parseColor("#C80000"));
                            }
                        }else
                        if (!someAnswers) {
                            for (i = 0; i < 5; i++) {
                                radiob[i].setClickable(false);
                                if (radiob[i].isChecked())
                                    radiob[i].setBackground(new_col_r);
                            }
                            radiob[answ - 1].setBackground(new_col_g);
                        }else{
                            for (i = 0; i < 6; i++) {
                                checkb[i].setClickable(false);
                                if (checkb[i].isChecked())
                                    checkb[i].setBackground(new_col_r);
                            }
                            for (int o = 0; o < answers.length; o++) {
                                if(checkb[answers[o]-1].getBackground() == new_col_r)
                                    checkb[answers[o]-1].setBackground(new_col_g);
                                else
                                    checkb[answers[o]-1].setBackground(new_col_y);
                            }


                        }

                    }
                }else{
                    if (answerShowingNow){
                    if(!someAnswers) {
                        if(answ_indx(l)<5){
                            if(line.getText().toString().replaceAll(" ","").matches(test[l][answ_indx(l)]))
                                correct++;

                        }else {
                            if (radiob[answ - 1].isChecked())
                                correct++;
                            for (i = 0; i < 5; i++) {
                                if (radiob[i].isChecked()) {
                                    user_answers[l] = i + 1;
                                    break;
                                }
                            }
                        }
                    }else{
                        int corectCheck=0;
                        for(i=0;i<6;i++){
                            if (checkb[i].isChecked()) {
                                user_answers[l] = i+1 + user_answers[l]*10;
                                for (int o = 0; o < answers.length; o++) {
                                    if (i+1 == answers[o]) {
                                        corectCheck++;
                                        break;
                                    }
                                }
                            }
                        }
                        if(corectCheck == answers.length){
                            correct++;
                        }
                    }

                    Intent Activity = new Intent(Questions.this, Result.class);
                    Bundle answrs = new Bundle();
                    answrs.putSerializable("quests", test);
                    answrs.putIntArray("user_answers",user_answers);
                    Activity.putExtras(answrs);
                    Activity.putExtra("answ", Integer.toString(correct));
                    Activity.putExtra("theoriya", radioTheme);
                    startActivity(Activity , answrs);
                }else{
                    line.setEnabled(false);
                    answerShowingNow=true;
                    submit.setText("Завершить");
                    GradientDrawable new_col_r = new GradientDrawable();
                    GradientDrawable new_col_g = new GradientDrawable();
                    GradientDrawable new_col_y = new GradientDrawable();
                    new_col_r.setColor(getResources().getColor(R.color.red));
                    new_col_r.setCornerRadius(50);
                    new_col_g.setColor(getResources().getColor(R.color.gren));
                    new_col_g.setCornerRadius(50);
                    new_col_y.setColor(getResources().getColor(R.color.yellow));
                    new_col_y.setCornerRadius(50);
                        if(answ_indx(l)<5){
                            for (i = 0; i < 9; i++)
                                numbers[i].setClickable(false);
                            clear.setClickable(false);
                            backspace.setClickable(false);
                            user_answers[l]=Integer.parseInt(line.getText().toString().replaceAll("[^0-9]", ""));
                            if(line.getText().toString().replaceAll(" ", "").matches(test[l][answ_indx(l)])) {
                                line.setTextColor(Color.parseColor("#00C800"));
                            }else{
                                line.setText("Ваш ответ: "+line.getText().toString() + ".");
                                TextView miss = findViewById(R.id.youMissed);
                                miss.getLayoutParams().height=WRAP_CONTENT;
                                miss.setTextColor(Color.parseColor("#00C800"));
                                if(isLetters){
                                    miss.setText("Правильный ответ: ");
                                    for(int y=0;y<test[l][answ_indx(l)].length()/2-1;y++){
                                        miss.append(test[l][answ_indx(l)].substring(y*2,y*2+2)+" ");
                                    }
                                    miss.append(test[l][answ_indx(l)].substring(test[l][answ_indx(l)].length()-2,test[l][answ_indx(l)].length())+".");
                                }
                                else
                                    miss.setText("Правильный ответ: "+test[l][answ_indx(l)]+".");
                                line.setTextColor(Color.parseColor("#C80000"));
                            }
                        }else
                    if (!someAnswers) {
                        for (i = 0; i < 5; i++) {
                            radiob[i].setClickable(false);
                            if (radiob[i].isChecked())
                                radiob[i].setBackground(new_col_r);
                        }
                        radiob[answ - 1].setBackground(new_col_g);
                    }else{
                        for (i = 0; i < 6; i++) {
                            checkb[i].setClickable(false);
                            if (checkb[i].isChecked())
                                checkb[i].setBackground(new_col_r);
                        }
                        for (int o = 0; o < answers.length; o++) {
                            if(checkb[answers[o]-1].getBackground() == new_col_r)
                                checkb[answers[o]-1].setBackground(new_col_g);
                            else
                                checkb[answers[o]-1].setBackground(new_col_y);
                        }


                    }

                }

                }
            }
        });
        colorOfElements(theme_id);
    }

    public void setRandomQuest(){
        if(test[l][0].indexOf("&")!=-1){
            String[] lines=test[l][0].split("&");
            question.setText(lines[0]);
            SpannableString[] spannableString = new SpannableString[lines.length];
            for(int y=1;y<lines.length;y++){
                int number_of_chars = lines[y].split("\n")[0].length();
                int space_margin = 7;
                spannableString[y] = new SpannableString("       "+lines[y].substring(0));
                StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
                spannableString[y].setSpan(italicSpan, space_margin, number_of_chars+space_margin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString[y].setSpan(new RelativeSizeSpan(1.2f), space_margin, number_of_chars+space_margin, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                question.append(spannableString[y]);
            }
        }else
            question.setText((l+1)+". "+test[l][0]);
        boolean ifImg = false;
        if(radioTheme == "birds" && l>59 && l<70) {

            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            if (l - 59 == 2)
                img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("bird1", "drawable", getPackageName())));
            else
                img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("bird" + (l - 59), "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("К тому же отряду, что и животное, изображённое на рисунке")){
            int number = 0;
            if(l<10)
                number = l;
            else
                number = parseInt(Integer.toString(l).substring(1));
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("milk"+(number-1), "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("Сердечный индекс")){
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("heart_index", "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("Ознакомьтесь с графиком интенсивности метаболизма")){
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("metabolism_intensity", "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("Схематические рисунки 1—4")){
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("breath_organs", "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("температуры окружающей среды на температуру тела лягушки")){
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("frog", "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("температуры окружающей среды на температуру тела собаки")){
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("dog", "drawable", getPackageName())));
            ifImg = true;
        }else
        if(question.getText().toString().contains("Укажите названия костей (частей скелета)")){
            int number = 0;
            if(l<10)
                number = l;
            else
                number = parseInt(Integer.toString(l).substring(1));
            img.getLayoutParams().height = convertDpToPixel(250, getApplicationContext());
            img.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),getResources().getIdentifier("evo_bones"+(number), "drawable", getPackageName())));
            ifImg = true;
        }


        if(!ifImg){
            img.getLayoutParams().height = 0;
            img.setImageDrawable(null);
        }

        if(answ_indx(l)>3) {
            if (test[l][answ_indx(l)].split(" ").length > 1)
                setCheck();
            else
                setRadio();
        }else
            setLine();

        style = new GradientDrawable();
        style.setCornerRadius(50);
        style.setColor(Color.parseColor(themeColor));
        question.setBackground(style.mutate());
        question.setMinimumHeight(WRAP_CONTENT);
    }
    public void setRadio(){
        resetLine("radio");
        someAnswers = false;

        for(i=1;i<6;i++) {
            checkb[i-1].getLayoutParams().height=0;
            checkb[i-1].setText(null);
            radiob[i-1].setText(test[l][i]);
        }

        checkb[5].getLayoutParams().height=0;
        checkb[5].setText(null);
        for(i=1;i<6;i++) {
            radiob[i-1].getLayoutParams().height= WRAP_CONTENT;
        }
        if(test[l][6] == null){

            radiob[4].getLayoutParams().height=0;

            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) checkb[5].getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            checkb[5].requestLayout();
            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) radiob[3].getLayoutParams();
            p1.setMargins(0, 0, 0,  convertDpToPixel(3, getApplicationContext()));
            radiob[3].requestLayout();
        }else{
            radiob[4].setVisibility(View.VISIBLE);

            radiob[4].getLayoutParams().height= WRAP_CONTENT;

            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) checkb[5].getLayoutParams();
            p.setMargins(0, 0, 0, convertDpToPixel(3, getApplicationContext()));
            checkb[5].requestLayout();
            ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) radiob[3].getLayoutParams();
            p1.setMargins(0, 0, 0,  convertDpToPixel(10, getApplicationContext()));
            radiob[3].requestLayout();
        }
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) checkb[4].getLayoutParams();
        p.setMargins(0, 0, 0, 0);
        checkb[4].requestLayout();

        style = new GradientDrawable();
        style.setCornerRadius(50);
        style.setColor(Color.parseColor(themeColor));
        for(i=0;i<5;i++){
            radiob[i].setBackground(style.mutate());
        }
    }
    public static int convertDpToPixel(float dp, Context context){
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    public void setCheck(){
        resetLine("check");
        someAnswers = true;
        for(i=1;i<6;i++) {
            radiob[i-1].setText(null);
            checkb[i-1].setText(test[l][i]);
            radiob[i-1].getLayoutParams().height=0;
        }
        checkb[4].setVisibility(View.INVISIBLE);
        checkb[4].getLayoutParams().height=0;
        checkb[5].setVisibility(View.INVISIBLE);
        checkb[5].getLayoutParams().height=0;
        for(i=4;i<answ_indx(l)-1;i++){
            checkb[i].setVisibility(View.VISIBLE);
            checkb[i].getLayoutParams().height= WRAP_CONTENT;
            checkb[i].setText(test[l][i+1]);
        }
        ViewGroup.MarginLayoutParams p1 = (ViewGroup.MarginLayoutParams) checkb[5].getLayoutParams();
        p1.setMargins(0, 0, 0, convertDpToPixel(3, getApplicationContext()));
        checkb[5].requestLayout();
        ViewGroup.MarginLayoutParams p2 = (ViewGroup.MarginLayoutParams) radiob[3].getLayoutParams();
        p2.setMargins(0, 0, 0,  convertDpToPixel(10, getApplicationContext()));
        radiob[3].requestLayout();
        if(answ_indx(l)==7){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) checkb[4].getLayoutParams();
            p.setMargins(0, 0, 0, convertDpToPixel(10, getApplicationContext()));
            checkb[4].requestLayout();
        }else{
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) checkb[4].getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            checkb[4].requestLayout();
        }
        for(i=1;i<answ_indx(l)-1;i++) {
            checkb[i-1].getLayoutParams().height= WRAP_CONTENT;
        }
        for(i=0;i<6;i++){
            style = new GradientDrawable();
            style.setCornerRadius(50);
            style.setColor(Color.parseColor(themeColor));
            checkb[i].setBackground(style.mutate());
            checkb[i].requestLayout();
        }
    }
    public void read(String file_name) throws IOException {
        AssetManager am = getApplicationContext().getAssets();
        InputStream is = am.open(file_name+".txt");
        Scanner scanner = new Scanner(is);
        int l = 0;
        kolvo = 0;
        while(scanner.hasNextLine()){

            String lineTXT = scanner.nextLine();

            if(lineTXT.substring(0, 1).matches("\\d+(?:\\.\\d+)?") || (lineTXT.substring(1, 2).matches("\\d+(?:\\.\\d+)?") && lineTXT.substring(3, 4).matches("\\d+(?:\\.\\d+)?"))){
                test[kolvo-1][l] = lineTXT;
                l++;
            }else{
                if(l==1){
                    test[kolvo-1][0] += "\n" + lineTXT.replaceAll("¬", " ");
                }else{
                    test[kolvo][0] = lineTXT;
                    l=1;

                    kolvo++;

                }
            }


        }
        scanner.close();
        System.out.println("Ended: "+file_name);
    }
    public void ControlTest(){

        String[][] control_test=new String[30][8];
        try {
            read("fish");
            quests_answered = new boolean[kolvo];
            int rand = new Random().nextInt(kolvo);
            for(i=0;i<5;i++){
                while(quests_answered[rand] || test[rand][0] == null) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
            test = new String[maxEvoCount][8];
            read("evo");
            System.out.println("go: "+kolvo);
            rand = new Random().nextInt(kolvo);
            quests_answered = new boolean[kolvo];
            for(i=5;i<10;i++){
                while(quests_answered[rand] || test[rand][0] == null) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
            test = new String[maxQuestionCount][8];
            read("ground");
            rand = new Random().nextInt(kolvo);
            quests_answered = new boolean[kolvo];
            for(i=10;i<15;i++){
                while(quests_answered[rand] || test[rand][0] == null) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
            test = new String[maxQuestionCount][8];
            read("presm");
            rand = new Random().nextInt(kolvo);
            quests_answered = new boolean[kolvo];
            for(i=15;i<20;i++){
                while(quests_answered[rand] || test[rand][0] == null) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
            test = new String[maxQuestionCount][8];
            read("birds");
            rand = new Random().nextInt(kolvo);
            quests_answered = new boolean[kolvo];
            for(i=20;i<25;i++){
                while(quests_answered[rand] || test[rand][0] == null) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
            test = new String[maxQuestionCount][8];
            read("milk");
            rand = new Random().nextInt(kolvo);
            quests_answered = new boolean[kolvo];
            for(i=25;i<30;i++){
                while(quests_answered[rand] || test[rand][0] == null || (rand>39 && rand<50)) rand = new Random().nextInt(kolvo);

                quests_answered[rand]=true;
                control_test[i] = test[rand];

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        test=null;
        test=control_test;
        kolvo=30;

    }
    public static boolean areAllTrue(boolean[] array){
        for(boolean b : array) if(!b) return false;
        return true;
    }
    public static boolean areAllFalse(boolean[] array){
        int i = 0;
        for(boolean b : array) if(b){
            if(i>=1)
                return false;
            i++;
        }
        return true;
    }
    public int answ_indx(int quest_number){
        int i=test[quest_number].length-1;
        while(test[quest_number][i]==null){
            i--;
        }
        return i;
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

            if(temp != null)
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
    public void colorOfElements(int themeNumber){
        if(themeNumber==1){
            findViewById(R.id.layout_fish).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,getTheme()));
            for(i=0; i<5; i++){
                checkb[i].setTextColor(Color.parseColor("#cc938a"));
                radiob[i].setTextColor(Color.parseColor("#cc938a"));
            }
            clear.setTextColor(Color.parseColor("#cc938a"));

            checkb[5].setTextColor(Color.parseColor("#cc938a"));
            for(i=0; i<9; i++){
                numbers[i].setTextColor(Color.parseColor("#cc938a"));
            }
            GradientDrawable style_back = new GradientDrawable();
            style_back.setCornerRadius(100);
            style_back.setColor(Color.parseColor("#B2E6E6E6"));
            backspace.setBackground(style_back);
            question.setTextColor(Color.parseColor("#cc938a"));
            submit.setTextColor(Color.parseColor("#cc938a"));
        }
    }
    public void setLine(){
        resetLine("");
        textMask = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!test[l][answ_indx(l)].substring(0,1).equals("А") && !test[l][answ_indx(l)].substring(0,1).equals("A")){
                    return;
                }
                if(line.getText().toString().indexOf("А")==-1)
                    line.setText("А");
                else
                    if(line.getText().toString().length() > 3 && line.getText().toString().charAt(line.getText().toString().length()-3) == test[l][answ_indx(l)].charAt(test[l][answ_indx(l)].length()-2)){
                        if(line.isEnabled()){
                            BaseInputConnection textFieldInputConnection = new BaseInputConnection(line, true);
                            textFieldInputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                        }else{
                            line.setText(line.getText().toString().substring(0,line.getText().toString().length()-1));
                        }
                    }else
                        if(line.getText().toString().substring(line.getText().toString().length()-1,line.getText().toString().length()).matches("\\d+(?:\\.\\d+)?") && line.getText().toString().charAt(line.getText().toString().length()-2) != test[l][answ_indx(l)].charAt(test[l][answ_indx(l)].length()-2)){
                            char char1=line.getText().toString().charAt(line.getText().toString().length()-2);
                            char1=(char)(char1+1);
                            if(!line.isEnabled())
                                line.setText(line.getText().toString()+" "+char1);
                            else{
                                line.setText(line.getText().toString()+" "+char1);
                                line.setSelection(line.getText().toString().length());
                            }
                        }else
                            if(line.getText().toString().length() > 2 && line.getText().toString().substring(line.getText().toString().length()-1,line.getText().toString().length()).equals(" ")){
                                line.setText(line.getText().toString().substring(0, line.getText().toString().length() - 2));
                                line.setSelection(line.getText().toString().length());
                            }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        if(test[l][answ_indx(l)].substring(0,1).equals("А") || test[l][answ_indx(l)].substring(0,1).equals("A")) {
            line.removeTextChangedListener(textMask);
            line.addTextChangedListener(textMask);
        }
        else {
            line.removeTextChangedListener(textMask);
        }
        setKeyboard(true);
        grp.getLayoutParams().height=0;
        style = new GradientDrawable();
        style.setCornerRadius(50);
        style.setColor(Color.parseColor(themeColor));
        question.setBackground(style);
        submit.setBackgroundColor(Color.parseColor(themeColor));

        GradientDrawable checkedStyle = new GradientDrawable();
        checkedStyle.setCornerRadius(50);
        checkedStyle.setColor(Color.parseColor(checkedTheme));

        for (i = 0; i < 9; i++)
            numbers[i].setClickable(true);
        clear.setClickable(true);
        backspace.setClickable(true);

        line.setTextColor(Color.BLACK);
        someAnswers = false;
        for(i=0;i<5;i++) {
            radiob[i].setText(null);
            checkb[i].setText(null);
            radiob[i].getLayoutParams().height=0;
            radiob[i].setVisibility(View.INVISIBLE);
            checkb[i].getLayoutParams().height=0;
            checkb[i].setVisibility(View.INVISIBLE);
        }
        checkb[5].setVisibility(View.INVISIBLE);
        checkb[5].getLayoutParams().height=0;
        checkb[5].setText(null);

        for(i=0; i<9; i++){
            style = new GradientDrawable();
            style.setCornerRadius(50);
            style.setColor(Color.parseColor(themeColor));
            numbers[i].setBackground(style);
        }
        clear.setBackground(style);
        clear.setTextColor(Color.parseColor("#ffffff"));
        clear.requestLayout();
        backspace.setBackground(style);
        backspace.requestLayout();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                line.setText(null);
            }
        });
        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!line.getText().toString().matches(""))
                    line.setText(line.getText().toString().substring(0,line.getText().toString().length()-1));
            }
        });
    }
    public void resetLine(String returnToVisible){
        TextView miss = findViewById(R.id.youMissed);
        miss.getLayoutParams().height=0;
        setKeyboard(false);
        switch(returnToVisible){
            case "radio":
                for(i=0;i<5;i++) {
                    radiob[i].setVisibility(View.VISIBLE);
                }
            break;
            case "check":
                for(i=0;i<5;i++) {
                    checkb[i].setVisibility(View.VISIBLE);
                }
            break;
        }
    }
    public void setStatusBarColor(String color){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor(color));
    }
    public void setKeyboard(boolean on){
        line.setText("");
        if(keyboard){
            findViewById(R.id.number_panel).getLayoutParams().height=0;
            if(on){
                line.getLayoutParams().height=WRAP_CONTENT;
                grp.getLayoutParams().height=0;
                line.setEnabled(true);
            }else{

                line.getLayoutParams().height=0;
                grp.getLayoutParams().height=WRAP_CONTENT;
                line.setEnabled(false);
            }
        }else{
            line.setEnabled(false);
            if(on){
                line.getLayoutParams().height=WRAP_CONTENT;
                grp.getLayoutParams().height=0;
                findViewById(R.id.number_panel).getLayoutParams().height=WRAP_CONTENT;
            }else{
                line.getLayoutParams().height=0;
                grp.getLayoutParams().height=WRAP_CONTENT;
                findViewById(R.id.number_panel).getLayoutParams().height=0;
            }
        }
    }
    private String readSourceData(String value){
        StringBuilder temp = new StringBuilder();

        try
        {
            FileInputStream fin = openFileInput(value+".txt");
            int a;

            while ((a = fin.read()) != -1)
            {
                temp.append((char)a);
            }

            fin.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if(temp == null || temp.toString().equals(""))
            return "0";
        else
            return temp.toString();
    }
    public void cancelQuest(){
        Intent Activity = new Intent(Questions.this, Result.class);
        Activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle answrs = new Bundle();
        answrs.putSerializable("quests", test);
        answrs.putIntArray("user_answers",user_answers);

        Activity.putExtras(answrs);
        Activity.putExtra("answ", Integer.toString(correct));
        Activity.putExtra("theoriya", radioTheme);
        startActivity(Activity , answrs);
    }
}