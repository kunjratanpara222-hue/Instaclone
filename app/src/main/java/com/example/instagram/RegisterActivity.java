package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.txtLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        binding.register.setOnClickListener(v -> {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Please wait...");
            pd.show();

            String str_username = binding.username.getText().toString();
            String str_fullname = binding.fullname.getText().toString();
            String str_email = binding.email.getText().toString();
            String str_password = binding.password.getText().toString();

            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                    || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else if (str_password.length() < 6) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                register(str_username, str_fullname, str_email, str_password);
            }
        });
    }

    private void register(String username, String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username.toLowerCase());
                        hashMap.put("fullname", fullname);
                        hashMap.put("bio", "");
                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagram-clone-2e382.appspot.com/o/placeholder.png?alt=media&token=8d6a8f6d-3e6f-4a6b-8d6f-3e6f4a6b8d6f");

                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    } else {
                        pd.dismiss();
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
