package com.example.notesstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText=findViewById(R.id.email_edit_text);
        passwordEditText=findViewById(R.id.password_edit_text);
        loginBtn=findViewById(R.id.login_button);
        progressBar=findViewById(R.id.progress_bar);
        createAccountBtnTextView=findViewById(R.id.create_account_text_view_button);


        loginBtn.setOnClickListener((v -> loginUser()));
        createAccountBtnTextView.setOnClickListener((v)-> startActivities(new Intent[]{new Intent(LoginActivity.this, CreateAccountActivity.class)}) );


    }

    void loginUser(){
        String email=emailEditText.getText().toString();
        String password=passwordEditText.getText().toString();


        boolean isValidated = validateData(email, password );
        if (! isValidated){
            return;
        }

        loginAccountInFirebase(email, password);

    }

    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);

                if (task.isSuccessful()){
                    //login is successful.
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    }else {
                        //when login is failed.
                        Utility.showToast(LoginActivity.this, "Email is not verified, Please verified your email ");
                    }
                }else{
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }

            }
        });
    }


    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }


    boolean validateData(String email, String password){
        if (! Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length()<6){
            passwordEditText.setError("Invalid Password Length");
            return false;
        }

        return true;
    }


}