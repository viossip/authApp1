package com.example.vitaly.authapp1.ViewController;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.vitaly.authapp1.Model.SecretAuth;
import com.example.vitaly.authapp1.R;


public class MainLoginActivity extends AppCompatActivity {

    private static final String[] CREDENTIALS = new String[]{
            "admin:1234:d0ecd2c7db75e999e256d0b396f995e1",
            "user:4321:e7096f6143aff9ceb073b14872b1659d",
            "sudo:1111:a7d134af6240efabcd1bf548c757a4a1"
    };

    private UserLoginTask mAuthTask = null;
    private SecretAuth secretAuth = new SecretAuth();

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private ImageView outcomeAnimationPlaceHolder;
    private AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        mEmailView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);

        Button mUserSignInButton = findViewById(R.id.email_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        outcomeAnimationPlaceHolder = findViewById(R.id.result_animation);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, outcomeAnimationPlaceHolder);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Asynchronous login/registration task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private ImageView animationPlaceHlder;

        UserLoginTask(String email, String password, ImageView animationPlaceHlder) {
            mUsername = email;
            mPassword = password;
            this.animationPlaceHlder = animationPlaceHlder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : CREDENTIALS) {
                String[] creds = credential.split(":");
                if (creds[0].equals(mUsername)) {
                    return  creds[1].equals(mPassword) &&
                            creds[2].equals(secretAuth.processSecret(secretAuth.getSecret(getApplicationContext())));
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                animationPlaceHlder.setBackgroundResource(R.drawable.animation_win);
            } else {
                animationPlaceHlder.setBackgroundResource(R.drawable.animation_loss);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

            animation = (AnimationDrawable) this.animationPlaceHlder.getBackground();
            animation.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animation.stop();
                }
            }, 5000);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

