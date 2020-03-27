package com.example.phase1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.widget.Toast.LENGTH_LONG;

public class Click extends AppCompatActivity {


    private Button Upload;
    private Button OpenList;

    private RatingBar Rating;
    private ImageView ProfilePic;
    private EditText ProfileName;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String Name;
    private ImageView listPic;
    private int Rate;
    private String link;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Places");

    private static final int PICK_IMAGE_REQUEST = 234;


    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click);

        Upload = findViewById(R.id.Upload);
        Rating = findViewById(R.id.listRatingMain);
        ProfilePic = findViewById(R.id.listPicMain);
        ProfileName = findViewById(R.id.listNameMain);
        OpenList = findViewById(R.id.OpenList);
        listPic = findViewById(R.id.listPicMain);

        listPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        OpenList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Click.this, List.class);
                startActivity(intent);
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ProfilePic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(Click.this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            final StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/pic.jpg");
            //final String link = riversRef.getDownloadUrl().toString();
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successful
                            //hiding the progress dialog
                            Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!downloadUri.isComplete());
                            Uri downloadUrl =downloadUri.getResult();
                            link = downloadUrl.toString();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "Image updated successfully.", LENGTH_LONG).show();
                            Name = ProfileName.getText().toString();
                            Rate = Rating.getNumStars();
                            if(!TextUtils.isEmpty(Name)){
                                reference.child(Name).child("Name").setValue(Name);
                                reference.child(Name).child("Rate").setValue(Rate);
                                reference.child(Name).child("link").setValue(link);
                            }
                            startActivity(new Intent(Click.this,Click.class));
                        }
                    });
        }


    }

}