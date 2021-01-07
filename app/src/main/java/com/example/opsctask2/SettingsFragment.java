package com.example.opsctask2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SettingsFragment extends Fragment {
    Button savebtn;
    Spinner system, transport;
    FirebaseFirestore fstore;
    ProgressBar progressBar;
    String userID;
    FirebaseAuth fAuth;
    String updateSpin,updateSpin2;
    String phone;
    EditText phoneText;
    String p;

    String spinneritem1, spinneritem2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container,false);

        savebtn = (Button) v.findViewById(R.id.btnSave);
        system = (Spinner) v.findViewById(R.id.spinnerSystem);
        transport = (Spinner) v.findViewById(R.id.spinnerTransport);
        phoneText = (EditText) v.findViewById((R.id.editText));

        try {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();

            DocumentReference documentReference = fstore.collection("users").document(userID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            updateSpin = (String) document.getString("System");
                            updateSpin2 = (String) document.getString("Transport");
                            phone = document.getString("phone");
                            phoneText.setText(phone);




                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                    if (updateSpin.equalsIgnoreCase("Metric System")) {
                        system.setSelection(0);
                    } else {
                        system.setSelection(1);
                    }
                    if (updateSpin2.equalsIgnoreCase("Car")) {
                        transport.setSelection(0);
                    } else if (updateSpin2.equalsIgnoreCase("Walking")) {
                        transport.setSelection(1);
                    } else if (updateSpin2.equalsIgnoreCase("Cycling")) {
                        transport.setSelection(2);
                    } else if (updateSpin2.equalsIgnoreCase("Public Transport")) {
                        transport.setSelection(3);
                    } else {
                        transport.setSelection(4);
                    }
                }

            });
        }
        catch(Exception e){
            Toast.makeText(getActivity(), "Please Update your Profile"+ e, Toast.LENGTH_SHORT).show();
        }


       // Toast.makeText(getActivity(),updateSpin2,Toast.LENGTH_SHORT).show();





        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = phoneText.getText().toString();
                MainActivity.saveSettings(system,transport,p);
            }
        });


        return v;
    }











}
