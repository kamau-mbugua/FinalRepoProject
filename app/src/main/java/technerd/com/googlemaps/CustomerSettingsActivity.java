package technerd.com.googlemaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettingsActivity extends AppCompatActivity {
    private EditText mNameField,mPhoneField;
    private Button mBack, mConfirm;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userId;
    private String mName;
    private String mPhone;
    private String mProfileUrl;
    private Uri resultUri;
    private ImageView mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Customer Settings");
        getSupportActionBar().setSubtitle("Kindly Input Correct Details");

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mProfileImage = findViewById(R.id.profileImage);

        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK); //when users clicks on image it will make him choose from gallery
                intent.setType("image/*"); //only pictures can be choosen
                startActivityForResult(intent,1); //
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return; //goes to previous activity
            }
        });

    }

    private void getUserInfo(){ //if there are alredy name and phone in database we will write
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){ //makes sure that there is stg in database

                    Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }

                    if(map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }

                    if(map.get("profileImageUrl")!=null){
                        mProfileUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileUrl).into(mProfileImage);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void saveUserInformation() { //if the user is saving informations for thw first time or if its changing
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString(); //if there is no value

        Map userInfo = new HashMap(); //saving into hashmap
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUri != null){ //saving image in firebase
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId); //stores data inside firabase storage
            //works as DatabaseReferance
            Bitmap bitmap = null;

            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri); //gives resultUri path on stg on the phone, get location from result uri


            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); //to save place on firebasae we compress
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data); //try to upload image to storage


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Uri downloadUri = taskSnapshot.getUploadSessionUri(); //getDownload
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mCustomerDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //When we start another activity from current activity to get the result for it, we call the method
        super.onActivityResult(requestCode, resultCode, data);
        //opens camera, gallery
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){  //startActivityForResult(intent,1); // ako je ovaj ovde requestcode 1
            final Uri imageUri = data.getData(); //location that the user shows
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri); //saving it on page but not on database

        }
    }

       /* mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mProfileImage = findViewById(R.id.profileImage);

        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }
    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        mName=map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("profileImageUrl")!=null){
                        mProfileUrl=map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileUrl).into(mProfileImage);
                    }
                    if (map.get("phone")!=null){
                        mPhone=map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void saveUserInformation(){
        mName=mNameField.getText().toString();
        mPhone =mPhoneField.getText().toString();

        Map userInfo=new HashMap();
        userInfo.put("name",mName);
        userInfo.put("phone",mPhone);
        mCustomerDatabase.updateChildren(userInfo);
        if (resultUri!=null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);

            }
            catch (IOException e){
                e.printStackTrace();
            }
            ByteArrayOutputStream baos =new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           Map newImage =new HashMap();
                           newImage.put("profileImageUrl", uri.toString());
                           mCustomerDatabase.updateChildren(newImage);

                           finish();
                           return;
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           finish();
                           return;
                       }
                   });
                }
            });
        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 &&  requestCode == Activity.RESULT_OK){
            final  Uri imagegeUri = data.getData();
            resultUri= imagegeUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }*/
}
