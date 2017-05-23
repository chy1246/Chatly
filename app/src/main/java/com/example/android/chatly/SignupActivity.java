package com.example.android.chatly;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;
    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        mAuth = FirebaseAuth.getInstance();
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
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                createAccount(email, password);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
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
     **/
}
