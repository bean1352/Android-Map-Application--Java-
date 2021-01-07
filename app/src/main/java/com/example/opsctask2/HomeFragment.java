package com.example.opsctask2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    TextView name, transport;
    String namee,ttransport,co,lat,lon;
    String userID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container,false);

        name = (TextView)v.findViewById(R.id.NameText);
        transport = (TextView)v.findViewById(R.id.textTransport);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    if(document!= null && document.exists()){
                        namee = (String) document.getString("fullname");
                        ttransport = (String) document.getString("Transport");
                        String system = (String) document.getString("System");
                        String number = (String) document.getString("phone");



                        name.setText("Welcome back, "+namee);
                        transport.setText("Here's what we know about you, \n\nYour preferred mode of transport is "+ttransport+"\n\nYou prefer to use the "+ system+ "\n\nHere's your Cellphone number: "+ number+"\n\n We also Log all your trips into our Database\n\nIf you would like to change any of these, go to Settings");

                    }
                }}});

       return v;
    }
}



