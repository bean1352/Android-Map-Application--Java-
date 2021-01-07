package com.example.opsctask2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.internal.Utils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button go;
    EditText start, end;
    GoogleMap map;

    static Button savebtn;
    static Spinner system, transport;
    static FirebaseFirestore fstore;
    static ProgressBar progressBar;
    //static String userID;
    static FirebaseAuth fAuth;
    static String userID;
    FirebaseUser user;
    static Activity thisActivity = null;
    String spinneritem1, spinneritem2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();

        go = findViewById(R.id.goBtn);
        start = findViewById(R.id.startLocation);
        end = findViewById(R.id.destination);

        savebtn = findViewById(R.id.btnSave);
        system = (Spinner)findViewById(R.id.spinnerSystem);
        transport = (Spinner)findViewById(R.id.spinnerTransport);
        progressBar = findViewById(R.id.progressBar3);

        thisActivity = this;




    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_maps:
                            selectedFragment = new MapFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                            selectedFragment).commit();

                    return true;
                }
            };


    public void goButton(View view){

        try {
            startActivity(new Intent(getApplicationContext(), MapsActivity2.class));
        }
        catch (Exception e){
            Log.e(TAG, "There was a problem");
            Toast.makeText(thisActivity, "There was a problem" , Toast.LENGTH_SHORT).show();
        }




        }

        public String fillSpinner(Spinner z, Spinner y){

        String x = "";

        return x;
        }


    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();}

    public static void saveSettings(Spinner system, Spinner transport, String phone){

        String systemString = system.getSelectedItem().toString();
        String pTransport = transport.getSelectedItem().toString();
        String p = phone;


        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();

        //progressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = fstore.collection("users").document(userID);

        documentReference.update(
                "System", systemString,
                "Transport", pTransport,
                "phone", p
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showMsg();
                }
            }
        });

        /*DocumentReference documentReference = fstore.collection("users").document(userID);
        Map<String,Object> user1 = new HashMap<>();
        user1.put("System", systemString);
        user1.put("Transport", pTransport);*/

        /*documentReference.set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: Settings Saved "+ userID);
            }
        });
        documentReference.set(user1).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","onFailure: "+ e.toString());
            }
        });*/

        //progressBar.setVisibility(View.GONE);


    }

    public static void showMsg()
    {
        Toast.makeText(thisActivity, "Successfully Updated Settings" , Toast.LENGTH_SHORT).show();
    }


}






    /*public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    }

}*/

