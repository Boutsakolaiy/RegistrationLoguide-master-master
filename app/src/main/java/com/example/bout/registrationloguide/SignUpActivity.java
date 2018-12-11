package com.example.bout.registrationloguide;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scrounger.countrycurrencypicker.library.Buttons.CountryCurrencyButton;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;

public class SignUpActivity extends AppCompatActivity {

    private Button btnVerify, btnCancel;
    private EditText edtName, edtSurname, edtEmail, edtPassword;
    FirebaseAuth firebaseAuth;
    String name, surname, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        edtName = findViewById(R.id.edtFullName);
        edtSurname = findViewById(R.id.edtSurname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnVerify = findViewById(R.id.btnVerify);
        btnCancel = findViewById(R.id.btnCancel);

        firebaseAuth = FirebaseAuth.getInstance();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = edtName.getText().toString();
                surname = edtSurname.getText().toString();
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                if (valid()){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                sendEmail();

                                sendUserValues();
                                Toast.makeText(getApplicationContext(), "Values are added", Toast.LENGTH_SHORT).show();
                                edtName.setText("");
                                edtSurname.setText("");
                                edtEmail.setText("");
                                edtPassword.setText("");

                            }else {
                                Toast.makeText(getApplicationContext(),"ERROR Sing Up", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    });
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //        select country

        CountryCurrencyButton button = findViewById(R.id.btnCountry);
        button.setOnClickListener(new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                if (country.getCurrency() == null) {
                    Toast.makeText(SignUpActivity.this,
                            String.format("name: %s\ncode: %s", country.getName(), country.getCode())
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this,
                            String.format("name: %s\ncurrencySymbol: %s", country.getName(), country.getCurrency().getSymbol())
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSelectCurrency(Currency currency) {

            }
        });
    }

    private Boolean valid (){

        Boolean result = false;

        if (name.equals("") || surname.equals("") || email.equals("")|| password.equals("")){
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
        }else {

            result = true;
        }
        return result;
    }

    private void sendEmail(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        finish();
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(), "Sign Up successful. verification send to your Email",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }else {
                        Toast.makeText(getApplicationContext(), "Sign Up fields. verification not send to your Email",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserValues(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        UserValues userValue = new UserValues(name, surname, email, password);
        databaseReference.setValue(userValue);

    }

}
