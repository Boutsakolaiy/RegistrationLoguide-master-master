package com.example.bout.registrationloguide;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView tvSignUp;
    private Button btnSignIn;
    private EditText edtEmail, edtPassword;
    FirebaseAuth firebaseAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.email_edt);
        edtPassword = findViewById(R.id.password_edt);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        firebaseAuth =FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=edtEmail.getText().toString();
                password=edtPassword.getText().toString();

                if (valid()){

                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                checkEmail();

                            }else {
                                Toast.makeText(getApplicationContext(), "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private Boolean valid(){
        Boolean result = false;

        if ( email.equals("")|| password.equals("")){
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
        }else {
            result = true;
        }

        return result;
    }

    private void checkEmail(){

        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        Boolean emailVerify = firebaseUser.isEmailVerified();

        if (emailVerify){
            finish();
            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
        }else {
            firebaseAuth.signOut();
            Toast.makeText(getApplicationContext(), "Verify your Email", Toast.LENGTH_SHORT).show();
        }

    }
}
