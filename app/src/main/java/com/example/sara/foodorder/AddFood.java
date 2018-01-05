package com.example.sara.foodorder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFood extends AppCompatActivity {

    private ImageButton foodImage;
    private Button addItem;
    private static final int GALLREQ = 1 ;
    private EditText name , desc , price ;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);
        foodImage = (ImageButton) findViewById(R.id.imageFood);
        addItem = (Button) findViewById(R.id.addItem);

        storageReference = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference("Item");

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("Image/*");
                startActivityForResult(gallery , GALLREQ);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameValue = name.getText().toString().trim();
                final String descValue = desc.getText().toString().trim();
                final String priceValue = price.getText().toString().trim();
                if (!TextUtils.isEmpty(nameValue) && !TextUtils.isEmpty(descValue) && !TextUtils.isEmpty(priceValue) ){
                    StorageReference filepath = storageReference.child(uri.getLastPathSegment());

                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") final Uri downloadurl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(AddFood.this,"Image uploaded",Toast.LENGTH_LONG).show();
                            final DatabaseReference newPost = myRef.push();
                            newPost.child("name").setValue(nameValue);
                            newPost.child("desc").setValue(descValue);
                            newPost.child("price").setValue(priceValue);
                            newPost.child("image").setValue(downloadurl.toString());
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLREQ && requestCode == RESULT_OK){
            uri = data.getData();
            foodImage.setImageURI(uri);
        }
    }

}
