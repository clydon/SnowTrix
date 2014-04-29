package com.lydonc.snowtrix;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Conor on 4/11/2014.
 */
public class FacebookPromptActivity extends Activity {

    private Dialog progressDialog;
    private Button loginButton;
    private Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook_prompt);

        loginButton = (Button) findViewById(R.id.buttonLogin);
        skipButton = (Button) findViewById(R.id.buttonSkipLogin);

        if (ParseUser.getCurrentUser() != null ) {
            Intent myIntent = new Intent(FacebookPromptActivity.this, MainActivity.class);
            FacebookPromptActivity.this.startActivity(myIntent);
        }

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                    progressDialog = ProgressDialog.show(
                            FacebookPromptActivity.this, "", "Logging in...", true);
                    List<String> permissions = Arrays.asList("basic_info");
                    ParseFacebookUtils.logIn(permissions, FacebookPromptActivity.this, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            progressDialog.dismiss();
                            if (user == null) {
                                Toast.makeText(FacebookPromptActivity.this, "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();
                            } else if (user.isNew()) {
                                Toast.makeText(FacebookPromptActivity.this, "User signed up and logged in through Facebook!", Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(FacebookPromptActivity.this, MainActivity.class);
                                FacebookPromptActivity.this.startActivity(myIntent);
                            } else {
                                Toast.makeText(FacebookPromptActivity.this, "User logged in through Facebook! " + ParseUser.getCurrentUser().toString(), Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(FacebookPromptActivity.this, MainActivity.class);
                                FacebookPromptActivity.this.startActivity(myIntent);
                            }
                        }
                    });
            }
        });



    }

}
