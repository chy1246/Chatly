package com.example.android.chatly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private StorageReference mStorageRef;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.portrait) ImageView _portrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        _portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Uploading...");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                progressDialog.setMessage("Creating the account...");
                createAccount(email, password);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent goToLogin = new Intent(SignupActivity.this, LoginActivity.class);
                goToLogin.putExtra("emailFromSignup", _emailText.getText().toString());
                startActivity(goToLogin);

                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

     private void createAccount(String email, String password) {
         Log.d(TAG, "createAccount:" + email);
        if (!validate()) {
         return;
        }
        progressDialog = new ProgressDialog(this);
         progressDialog.setIndeterminate(true);
         progressDialog.setMessage("Creating the account");
         progressDialog.show();

     // [START create_user_with_email]
     mAuth.createUserWithEmailAndPassword(email, password)
     .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
    if (task.isSuccessful()) {
    // Sign in success, update UI with the signed-in user's information
    Log.d(TAG, "createUserWithEmail:success");
        Toast.makeText(SignupActivity.this, "Success",
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, null);
        FirebaseUser user = mAuth.getCurrentUser();
        String userName = _nameText.getText().toString();
        //write to DB
        writeNewUser(user, userName);

        progressDialog.dismiss();
    //updateUI(user);
    } else {
    // If sign in fails, display a message to the user.
    Log.w(TAG, "createUserWithEmail:failure", task.getException());
    Toast.makeText(SignupActivity.this, "Authentication failed.",
    Toast.LENGTH_SHORT).show();
    //updateUI(null);
    }

    // [START_EXCLUDE]
    progressDialog.dismiss();
    // [END_EXCLUDE]
    }
    });
     // [END create_user_with_email]
     }


    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void writeNewUser(FirebaseUser firebaseUser, String userName){
        User newUser = new User(firebaseUser.getEmail(), firebaseUser.getUid());
        newUser.setUserName(userName);
        String userId = firebaseUser.getUid();
        mDatabase.child("users").child(userId).setValue(newUser);
        uploadPicture(firebaseUser);
        mDatabase.child("nameIDmap").child(userName).setValue(userId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                _portrait.setImageBitmap(bitmap);
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadPicture(FirebaseUser firebaseUser){
        if(filePath != null) {
            StorageReference childRef = mStorageRef.child("Portrait").child(firebaseUser.getUid());

            //uploading the image
            UploadTask uploadTask = childRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent goToLogin = new Intent(this, LoginActivity.class);
        goToLogin.putExtra("emailFromSignup", _emailText.getText().toString());
        startActivity(goToLogin);

        super.onBackPressed();
    }

    /**
     private boolean validateForm() {
     boolean valid = true;

     String email = _emailText.getText().toString();
     if (TextUtils.isEmpty(email)) {
     _emailText.setError("Required.");
     valid = false;
     } else {
     _emailText.setError(null);
     }

     String password = _passwordText.getText().toString();
     if (TextUtils.isEmpty(password)) {
     _passwordText.setError("Required.");
     valid = false;
     } else {
     _passwordText.setError(null);
     }

     return valid;
     }

     public void requestPermission(){
     if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
     != PackageManager.PERMISSION_GRANTED) {

     // Should we show an explanation?
     if (shouldShowRequestPermissionRationale(
     Manifest.permission.READ_EXTERNAL_STORAGE)) {
     // Explain to the user why we need to read the contacts
     }

     requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
     MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

     // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
     // app-defined int constant that should be quite unique

     return;
     }
     }
     **/
}
