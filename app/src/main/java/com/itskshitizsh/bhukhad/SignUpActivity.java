package com.itskshitizsh.bhukhad;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    Button SignUpButton;
    private MaterialEditText editRollNo, editName, editPhoneNo, editPassword;

    private boolean isNetworkConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editRollNo = findViewById(R.id.editRollNoU);
        editName = findViewById(R.id.editNameU);
        editPhoneNo = findViewById(R.id.editPhoneU);
        editPassword = findViewById(R.id.editPasswordU);

        SignUpButton = findViewById(R.id.SignUpButton);

        try {
            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                isNetworkConnected = true;

            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (isNetworkConnected) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference userTable = database.getReference("User");

            SignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Registering...\nTrying to connect with server");
                    mDialog.show();

                    if (editName.getText().toString().isEmpty()) {
                        editName.setError("Required");
                        editName.requestFocus();
                    }
                    if (editPassword.getText().toString().isEmpty()) {
                        editPassword.setError("Required");
                        editPassword.requestFocus();
                    }
                    if (editPhoneNo.getText().toString().isEmpty()) {
                        editPhoneNo.setError("Required");
                        editPhoneNo.requestFocus();
                    }
                    if (editRollNo.getText().toString().isEmpty()) {
                        editRollNo.setError("Required");
                        editRollNo.requestFocus();
                    }
                    if ((!editName.getText().toString().isEmpty()) && (!editPassword.getText().toString().isEmpty()) && (!editPhoneNo.getText().toString().isEmpty()) && (!editRollNo.getText().toString().isEmpty())) {
                        userTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(editRollNo.getText().toString()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Phone Number Already Registered!", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDialog.dismiss();
                                    User currentUser = new User(editName.getText().toString(), editPhoneNo.getText().toString(), editPassword.getText().toString());
                                    userTable.child(editRollNo.getText().toString()).setValue(currentUser);
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Try to connect with Internet and Restart the App.", Toast.LENGTH_SHORT).show();
        }
    }
}