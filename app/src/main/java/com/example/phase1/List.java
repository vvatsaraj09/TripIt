package com.example.phase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class List extends AppCompatActivity {
   private static final String TAG = "List";
   private DatabaseReference reference;
   private ImageView ImageView;
   private EditText editText;
   private Button search;
   private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.d(TAG,"OnCreate: started");
        ImageView = (ImageView)findViewById(R.id.ImageView);
        editText = findViewById(R.id.editName);
        search = findViewById(R.id.searchList);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = FirebaseDatabase.getInstance().getReference().child("Places");
                name = editText.getText().toString();
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String link = dataSnapshot.child(name).child("link").getValue().toString();
                        if(!link.isEmpty()){
                            Picasso.get().load(link).into(ImageView);
                        }
                        editText.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
