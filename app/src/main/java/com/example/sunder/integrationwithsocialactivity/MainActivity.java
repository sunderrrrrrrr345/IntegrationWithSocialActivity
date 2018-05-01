package com.example.sunder.integrationwithsocialactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.item_facebook)
    LinearLayout mFacebook;

    @BindView(R.id.item_facebook_text)
    TextView mFacebookText;

    @BindView(R.id.item_first_name)
    TextView mFirstName;

    @BindView(R.id.item_last_name)
    TextView mLastName;

    @BindView(R.id.item_email)
    TextView mEmail;

    @BindView(R.id.item_id)
    TextView mId;


    private CallbackManager mFacebookCallbackManager;
    private LoginManager mLoginManager;
    private AccessTokenTracker mAccessTokenTracker;
    private boolean loggedin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        // Init
        setupInit();

        // Facebook
        setupFacebook();
        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.i("Loggie",""+loggedin);
                //this is for true condition
                if (loggedin) {
                    LoginManager.getInstance().logOut();
                   // mFacebookText.setText("Log in with Facebook");
                    mFirstName.setText("First Name:");
                    mLastName.setText("Last Name:");
                    mEmail.setText("Email:");
                    mId.setText("Id:");
                    loggedin = false;
                   Toast.makeText(MainActivity.this,"Hi",Toast.LENGTH_SHORT).show();
                } else {

                    //this is for false condition
                    mAccessTokenTracker.startTracking();
                    mLoginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
                   // Toast.makeText(MainActivity.this,"Hi1",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupFacebook() {
        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // handle
//                Toast.makeText(MainActivity.this,""+currentAccessToken.getToken(),Toast.LENGTH_SHORT).show();
   //             Log.i("currentAccessToken",""+currentAccessToken.getToken());
            }
        };

        //it is show customer is login or not.

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new  FacebookCallback<LoginResult>() {
            @Override
            public  void onSuccess(final LoginResult loginResult) {
                if (loginResult.getRecentlyGrantedPermissions().contains("email")) {
                    requestObjectUser(loginResult.getAccessToken());
                } else {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(MainActivity.this, "Error permissions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR", error.toString());
            }
        });

        if (AccessToken.getCurrentAccessToken() != null) {
            requestObjectUser(AccessToken.getCurrentAccessToken());
        }
    }



    private void setupInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Toolbar
        setSupportActionBar(mToolbar);
        //title
        setTitle(getString(R.string.app_name));
        loggedin = false;
    }


    private void requestObjectUser(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (response.getError() != null) {
                    // handle error
                } else {
                   // Toast.makeText(MainActivity.this, "Access Token: " + accessToken.getToken(), Toast.LENGTH_SHORT).show();
                    loggedin = true;
               //     mFacebookText.setText("Log out");
                    mFirstName.setText("First Name: " + object.optString("first_name"));
                    mLastName.setText("Last Name: " + object.optString("last_name"));
                    mEmail.setText("Email: " + object.optString("email"));
                    mId.setText("Id: " + object.optString("id"));
                    Log.i("Token",""+accessToken.getToken());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
