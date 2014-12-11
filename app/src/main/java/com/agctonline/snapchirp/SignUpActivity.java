package com.agctonline.snapchirp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected EditText mFirstName;
    protected EditText mLastName;
    protected EditText mHomeTown;
    protected EditText mWebAddress;
    protected Button mCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //turn on progress bar while signing up, must put it here before setcontentview
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_sign_up);

        //hide action bar
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        mEmail = (EditText)findViewById(R.id.emailField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mUsername = (EditText)findViewById(R.id.usernameField);
        mFirstName = (EditText)findViewById(R.id.firstNameField);
        mLastName = (EditText)findViewById(R.id.lastNameField);
        mSignUpButton = (Button)findViewById(R.id.signUpButton);
        mHomeTown = (EditText)findViewById(R.id.homeTownField);
        mWebAddress = (EditText)findViewById(R.id.webAddressField);
        mCancel = (Button)findViewById(R.id.cancelButton);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//close current activity and return to previous Activity
            }
        });


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String firstName = mFirstName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();
                String homeTown = mHomeTown.getText().toString().trim();
                String webAdrress = mWebAddress.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {

                    //To show a alert dialog, build it first, then create, then call show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.signup_error_title)
                    .setPositiveButton(android.R.string.ok, null);//must provide second param

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    //create new user
                    setProgressBarIndeterminateVisibility(true);//turn on progress bar
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put(ParseConstants.KEY_FIRST_NAME,firstName);
                    newUser.put(ParseConstants.KEY_LAST_NAME,lastName);
                    newUser.put(ParseConstants.KEY_HOME_TOWN,homeTown);
                    newUser.put(ParseConstants.KEY_WEB_ADDRESS,webAdrress);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);//turn off progress bar
                            if (e == null) {
                                //Success
                                Intent goToMainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                goToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                goToMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(goToMainActivityIntent);

                            } else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);//must provide second param

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
