package com.techilyfly.notesty;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterActivity";
    int AUTH_UI_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            finish();
        }

    }

    public void HandleLoginRegister(View view) {

        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//                new AuthUI.IdpConfig.GoogleBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setTosAndPrivacyPolicyUrls("https://abdurrahmang.com/disclaimer", "https://abdurrahmang.com/privacy-policy")
                .setLogo(R.drawable.notesty_logo)
//                .setAlwaysShowSignInMethodScreen(true)
                .build();

        startActivityForResult(intent, AUTH_UI_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_UI_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                //We have two users 1 Signed User or New User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.getEmail());
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()){
                    Toast.makeText(this, "Welcome to the NotesTy's World!", Toast.LENGTH_SHORT).show();
                } else {
                    // This is a returning user
                    Toast.makeText(this, "Welcome Back to NotesTy's World!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();

            } else {
                // Signing is failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null){
                    Log.d(TAG, "onActivityResult: the user has cancelled the sign in request");
                } else {
                    Log.d(TAG, "onActivityResult: ", response.getError());
                }
            }
        }
    }
}