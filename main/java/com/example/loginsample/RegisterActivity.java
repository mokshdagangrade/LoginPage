package com.example.loginsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    TextView btn;

    private EditText inputUsername,inputPassword,inputEmail,inputConfirmPassword;
    Button btnregister;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn = findViewById(R.id.alreadyhaveacc);

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.ConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(RegisterActivity.this);
        btnregister = findViewById(R.id.btnRegister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkCredentials();
            }

        });

        checkCredentials();

        btn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));
    }

    private void checkCredentials() {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmpassword = inputConfirmPassword.getText().toString();

        if(username.isEmpty() || username.length()<7)
        {
            showError(inputUsername,"Your Username is not valid!");
        }
        else if(email.isEmpty() || !email.contains("@"))
        {
            showError(inputEmail,"Email is not valid!");
        }
        else if (password.isEmpty() || password.length()<7)
        {
            showError(inputPassword,"Password not valid.Enter 7 Charachetrs");
        }
        else if(confirmpassword.isEmpty() || !confirmpassword.equals(password))
        {
            showError(inputConfirmPassword,"Not valid!");
        }
        else
        {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void showError(EditText input, String s) {

        input.setError(s);
        input.requestFocus();

    }
}