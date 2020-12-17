package com.example.loginsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.FileReader;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    TextView btn;
    EditText inputEmail,inputPassword;
    Button btnLogin;
    private FirebaseAuth mAuth;

    private ProgressDialog mLoadingBar;

    private SignInButton btnGoogle;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    public void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn = findViewById(R.id.signup);
        inputEmail=findViewById(R.id.inputemail);
        inputPassword=findViewById(R.id.inputusername);


        btnLogin = findViewById(R.id.Login);
        btnLogin.setOnClickListener(v -> checkCredentials());


        mLoadingBar = new ProgressDialog(LoginActivity.this);

        btnGoogle = findViewById(R.id.Google);

        btn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));


        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        processRequest();
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void processRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


    private void checkCredentials() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if(email.isEmpty() || !email.contains("@"))
        {
            showError(inputEmail,"Email is not valid!");
        }
        else if (password.isEmpty() || password.length()<7)
        {
            showError(inputPassword,"Password not valid.Enter 7 Charachetrs");
        }
        else
        {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        mLoadingBar.dismiss();

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