package com.example.opsctask2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapFragment extends Fragment {

    EditText start,end;
    String userID, lat,lon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container,false);

        start = (EditText)v.findViewById(R.id.startLocation);
        end = (EditText)v.findViewById(R.id.destination);
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore fstore = FirebaseFirestore.getInstance();

            DocumentReference documentReference = fstore.collection("trips").document(userID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            lat = (String) document.getString("Start Latitude");
                            lon = (String) document.getString("Start Longitude");
                            start.setText(lat);
                            end.setText(lon);

                        }

                    }

                }
            });
        }
        catch(Exception e){
            start.setText("Please enable Location Services");
        }


        return v;
    }
}
