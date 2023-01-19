package com.mastik.biology;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "Firebase_Auth";
    private String[] keys, radio;
    private int keyId;
    private int[] reauth;
    private EditText key_view;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        key_view = findViewById(R.id.key);
        Button login = findViewById(R.id.login);

// ...
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!key_view.getText().toString().startsWith("group.")) {
                    String key = key_view.getText().toString();
                    authenticate(key);
                }else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("keys").document(key_view.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        if(Timestamp.valueOf(task.getResult().getString("expires_in").replaceAll(", ","-")+" 00:00:00.00").getTime() > System.currentTimeMillis()){
                                            JSONArray keys_arr = new JSONArray();
                                            try {
                                                keys_arr = new JSONArray(task.getResult().get("keys").toString());
                                                authenticateGroup(key_view.getText().toString(), keys_arr, task.getResult());
                                            } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        }else
                                            Toast.makeText(getApplicationContext(), "Срок действия группового ключа истёк",Toast.LENGTH_LONG).show();
                                    }else
                                        try {
                                            if(!isConnected())
                                                Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_LONG).show();
                                            else
                                                Toast.makeText(getApplicationContext(), "Ошибка чтения данных группового ключа", Toast.LENGTH_LONG).show();
                                        } catch (InterruptedException | IOException e) {
                                            e.printStackTrace();
                                        }
                                }
                            });
                }


                /*if(Arrays.asList(keys).contains(key))
                for(keyId = 0; keyId < keys.length; keyId++){
                    if(keys[keyId].equals(key) && (radio[keyId].equals(Build.getRadioVersion()) || radio[keyId].equals(""))) {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "signInWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Map<String, Object> city = new HashMap<>();
                                            city.put("key", keys[keyId]);
                                            city.put("model", Build.MODEL);
                                            city.put("device", Build.DEVICE);
                                            city.put("radio", Build.getRadioVersion());
                                            city.put("reauth", reauth[keyId]);

                                            db.collection("keys").document(keyId + "")
                                                    .set(city)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error writing document", e);
                                                        }
                                                    });
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                            //Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "signInWithEmail:success");
                                                        Toast.makeText(getApplication(), "Ключ успешно активирован", Toast.LENGTH_SHORT).show();
                                                        Map<String, Object> city = new HashMap<>();
                                                        city.put("key", keys[keyId]);
                                                        city.put("model", Build.MODEL);
                                                        city.put("device", Build.DEVICE);
                                                        city.put("radio", Build.getRadioVersion());
                                                        city.put("reauth", reauth[keyId]);

                                                        db.collection("keys").document(keyId + "")
                                                                .set(city)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error writing document", e);
                                                                    }
                                                                });
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        updateUI(user);
                                                    } else {
                                                        Toast.makeText(getApplication(), "Ошибка активации ключа", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                        break;
                    }else{
                        if(keys[keyId].equals(key) && !radio[keyId].equals(Build.getRadioVersion())){
                            Toast.makeText(getApplication(), "Ключ уже активирован на другом устройстве",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    updateUI(null);*/
            }
        });
    }

    private  void updateUI(FirebaseUser user){
        if(user == null){
            Toast.makeText(getApplicationContext(), "Вы указали неккоректный ключ",Toast.LENGTH_SHORT).show();
        }else{
            finish();
        }
    }
    private void authenticate(String key){
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        mAuth.signInWithEmailAndPassword(key + "@whateverdomain.com", key)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("keys").document(key)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if(task.getResult().contains("activated") && !task.getResult().getBoolean("activated")){
                                                    if(Timestamp.valueOf(task.getResult().getString("expires_in").replaceAll(", ","-")+" 00:00:00.00").getTime() > System.currentTimeMillis()) {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("activated", true);
                                                        db.collection("keys").document(key).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Ключ успешно активирован", Toast.LENGTH_LONG).show();
                                                                    updateUI(FirebaseAuth.getInstance().getCurrentUser());
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Ошибка при активации ключа", Toast.LENGTH_LONG).show();
                                                                    //Toast.makeText(getApplicationContext(), task.getException()+"", Toast.LENGTH_LONG).show();
                                                                    key_view.setText("");
                                                                    FirebaseAuth.getInstance().signOut();
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(getApplicationContext(), "Срок действия ключа истёк", Toast.LENGTH_LONG).show();
                                                        //Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();
                                                    }
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "Данный ключ уже был активирован", Toast.LENGTH_LONG).show();
                                                    key_view.setText("");
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Ошибка подключения к БД", Toast.LENGTH_LONG).show();
                                                //Toast.makeText(getApplicationContext(), task.getException()+"", Toast.LENGTH_LONG).show();
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                if(isConnected()) {
                                    Toast.makeText(getApplicationContext(), "Неккоректный ключ "+key, Toast.LENGTH_LONG).show();
                                    key_view.setText("");
                                }else{
                                    Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_LONG).show();
                                }
                            } catch (InterruptedException | IOException e) {
                                e.printStackTrace();
                            }
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                        }
                    }
                });

    }
    private void authenticateGroup(String group_key, JSONArray keys_arr, DocumentSnapshot doc){
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            if(keys_arr.length() == 0)
                return;
            String key = null;
            try {
                while(doc.contains(keys_arr.getString(0)) && keys_arr.length() > 0) {
                    keys_arr.remove(0);
                }
                if(keys_arr.length() == 0){
                    Toast.makeText(getApplicationContext(), "Ключей в группе не осталось", Toast.LENGTH_LONG).show();
                    return;
                }
                key = keys_arr.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            keys_arr.remove(0);
            String finalKey = key;
            System.out.println(key);
            mAuth.signInWithEmailAndPassword(key + "@whateverdomain.com", key)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("keys").document(finalKey)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (task.getResult().contains("activated") && !task.getResult().getBoolean("activated")) {
                                                        if (Timestamp.valueOf(task.getResult().getString("expires_in").replaceAll(", ", "-") + " 00:00:00.00").getTime() > System.currentTimeMillis()) {
                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("activated", true);
                                                            db.collection("keys").document(finalKey).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getApplicationContext(), "Ключ успешно активирован", Toast.LENGTH_LONG).show();
                                                                        if(!group_key.equals("")){
                                                                            Map<String, Object> data1 = new HashMap<>();
                                                                            data1.put(finalKey, true);
                                                                            db.collection("keys").document(group_key).set(data1, SetOptions.merge())
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        updateUI(FirebaseAuth.getInstance().getCurrentUser());
                                                                                    }
                                                                                });
                                                                        }else
                                                                            updateUI(FirebaseAuth.getInstance().getCurrentUser());
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "Ошибка при активации ключа", Toast.LENGTH_LONG).show();
                                                                        //Toast.makeText(getApplicationContext(), task.getException()+"", Toast.LENGTH_LONG).show();
                                                                        key_view.setText("");
                                                                        FirebaseAuth.getInstance().signOut();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Срок действия ключа истёк", Toast.LENGTH_LONG).show();
                                                            //Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Данный ключ уже был активирован, пробуем другой", Toast.LENGTH_SHORT).show();
                                                        key_view.setText("");
                                                        FirebaseAuth.getInstance().signOut();
                                                        authenticateGroup(group_key, keys_arr, doc);
                                                    }
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Ошибка подключения к БД", Toast.LENGTH_LONG).show();
                                                    //Toast.makeText(getApplicationContext(), task.getException()+"", Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                    FirebaseAuth.getInstance().signOut();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    if (isConnected()) {
                                        Toast.makeText(getApplicationContext(), "Неккоректный ключ " + finalKey, Toast.LENGTH_LONG).show();
                                        key_view.setText("");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Проверьте подключение к интернету", Toast.LENGTH_LONG).show();
                                    }
                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                            }
                        }
                    });
        }

    }
    private String getUniqueIdentifier(){
        String uuid_u = "";
        try
        {
            File uuid = new File(getFilesDir(), "uuid.txt");
            FileInputStream fin = new FileInputStream(uuid);
            int a;
            StringBuilder temp = new StringBuilder();
            while ((a = fin.read()) != -1)
            {
                temp.append((char)a);
            }

            // setting text from the file.
            if(!temp.equals(""))
                uuid_u = temp.toString();
            fin.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            uuid_u = UUID.randomUUID().toString();
            try
            {
                File uuid = new File(getFilesDir(), "uuid.txt");
                FileOutputStream fos = new FileOutputStream(uuid);
                String data = uuid_u;
                fos.write(data.getBytes());
                fos.flush();
                fos.close();
            }
            catch (IOException r)
            {
                r.printStackTrace();
            }
        }
        return uuid_u;
    }
    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return Runtime.getRuntime().exec(command).waitFor() == 0;
    }
}