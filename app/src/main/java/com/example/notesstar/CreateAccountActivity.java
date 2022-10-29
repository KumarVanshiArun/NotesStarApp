package com.example.notesstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText=findViewById(R.id.email_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
       confirmPasswordEditText=findViewById(R.id.confirm_password_edit_text);
       createAccountBtn=findViewById(R.id.create_account_button);
       progressBar=findViewById(R.id.progress_bar);
        loginTextView=findViewById(R.id.login_text_button);

        createAccountBtn.setOnClickListener(v-> createAccount());
        loginTextView.setOnClickListener(v-> finish());



    }

    void createAccount(){
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (! isValidated){
            return;
        }

        createAccountInFirebase(email, password);


    }
    void createAccountInFirebase(String email, String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()){
                    //creating acc. is done.
                    Utility.showToast(CreateAccountActivity.this, "Successfully cretaed account, Check your email to verify");

                    //Toast.makeText(CreateAccountActivity.this, "Successfully created account, Check your email to verify", Toast.LENGTH_SHORT).show();
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    firebaseAuth.signOut();
                    finish();
                }else {
                    // when creating account is fail.
                    Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                   // Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password, String confirmPassword){
        if (! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Invalid Password Length");
            return false;
        }
        if (! password.equals(confirmPassword)){
            confirmPasswordEditText.setError(" Password not matched");
            return false;
        }
        return true;
    }

}