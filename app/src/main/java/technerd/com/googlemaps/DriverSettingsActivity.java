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
import android.widget.ImageView;
import android.widget.TextView;

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
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

public class DriverSettingsActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mNameField,mPhoneField,mCarField;
    private Button mConfirm,mBack;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private  String userId;
    private  String mName;
    private  String mPhone;
    private  String mCar;
    private  String mProfileImageUrl;
    private Uri resultUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);

     /*   mNameField = findViewById(R.id.Ridername);
        mPhoneField = findViewById(R.id.Riderphone);
        mProfileImage = findViewById(R.id.RiderprofileImage);
        mCarField = findViewById(R.id.car);





        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);

        //mRadioGroup = findViewById(R.id.radioGroup);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mDriverDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);

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
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
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

                    if(map.get("car")!=null){
                        mCar = map.get("car").toString();
                        mCarField.setText(mCar);
                    }

                    *//*if(map.get("service")!=null){
                        mServis = map.get("service").toString();
                        switch (mServis){
                            case "UberX":
                                mRadioGroup.check(R.id.UberX);
                                break;

                            case "UberBlack":
                                mRadioGroup.check(R.id.UberBlack);
                                break;
                            case "UberXl":
                                mRadioGroup.check(R.id.UberXl);
                                break;
                        }*//*

                    //}

                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);

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
        mCar = mCarField.getText().toString();

        *//*int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = findViewById(selectId);

        if(radioButton.getText() == null){
            return;
        }*//*

        //mServis = radioButton.getText().toString();


        Map userInfo = new HashMap(); //saving into hashmap
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        userInfo.put("car", mCar);
        //userInfo.put("service", mServis);
        mDriverDatabase.updateChildren(userInfo);

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
                            mDriverDatabase.updateChildren(newImage);

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
*/


    mProfileImage=findViewById(R.id.RiderprofileImage);
    mNameField = findViewById(R.id.Ridername);
    mPhoneField = findViewById(R.id.Riderphone);
    mCarField = findViewById(R.id.car);
    mConfirm = findViewById(R.id.Riderconfirm);
    mBack = findViewById(R.id.Riderback);

    mAuth = FirebaseAuth.getInstance();
    userId = mAuth.getCurrentUser().getUid();
    mDriverDatabase = FirebaseDatabase.getInstance().
            getReference().child("Users").child("Driver").child(userId);
    getUserInfo();



    mProfileImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
//            startActivityForResult(1);
                startActivityForResult(intent,1);
        }
    });

    mConfirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveUserInformation();

        }
    });

    mBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
            return;

        }
    });

    }
    private  void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map =(Map<String, Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        mName = map.get("name").toString();
                        mNameField.setText(mName);
                    }
                    if (map.get("phone")!=null){
                        mPhone = map.get("phone").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if (map.get("car")!=null){
                        mCar= map.get("car").toString();
                        mCarField.setText(mCar);

                    }
                    if (map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveUserInformation(){
        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mCar =mCarField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name",mName);
        userInfo.put("phone",mPhone);
        userInfo.put("car", mCar);
        mDriverDatabase.updateChildren(userInfo);
        if (resultUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication()
                        .getContentResolver(),resultUri);


            }catch (IOException e){
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask =filePath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl",uri.toString());
                            mDriverDatabase.updateChildren(newImage);
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
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final  Uri imageUri = data.getData();
            resultUri =imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
