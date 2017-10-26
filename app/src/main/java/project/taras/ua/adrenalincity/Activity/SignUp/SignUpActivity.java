package project.taras.ua.adrenalincity.Activity.SignUp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.HelperClasses.Calculus;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MainActivity;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.Login.User;
import project.taras.ua.adrenalincity.R;

public class SignUpActivity extends AppCompatActivity implements RequestManager.OnLoginActivityListener, GoogleApiClient.OnConnectionFailedListener {

    private Pref pref;
    private RequestManager requestManager;

    //manual registration
    private RelativeLayout rlRoot;
    private EditText etEmail;
    private EditText etPassword;
    //private Button bLogIn;
    private Button bSignUp;
    private TextView tvSignUp;

    //facebook
    private Button b_login_fb;
    private CallbackManager callbackManager;

    //Firebase Auth
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private Button b_google_plus;


    private boolean newUserComing = false;

    /**
     * CALLBACKS
     **/

    @Override
    public void onAccessGranted() {
        //TODO: yet to be implemented...
        if (newUserComing)
            pref.saveCurrentUser(user);

        Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToMainActivity);
        //overridePendingTransition();
    }

  /*  @Override
    public void onAccessDenied() {
        //TODO: yet to be implemented...
    }*/

    @Override
    public void onUserNotFound() {
        Log.v("g_test", "not found");
        requestManager.sendRequest(Constants.CREATE_NEW_USER, Constants.TYPE_CREATE_USER, userCredentials);
    }

    @Override
    public void checkPassword(String sha1Password) {
        String filledPassword = password;

        String generatedSha1 = Calculus.generateSHA1(filledPassword);
        String generatedMD5 = Calculus.generateMD5(generatedSha1);

        if (sha1Password.equalsIgnoreCase(generatedMD5)) {
            Toast.makeText(this, "password has matched", Toast.LENGTH_LONG).show();
            onAccessGranted();
        } else Toast.makeText(this, "incorrect password", Toast.LENGTH_LONG).show();
    }

    @Override
    public void appendUserDbId(String userDbId) {
        user.setDbId(userDbId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(onClickListener);
        setSupportActionBar(toolbar);

        pref = Pref.getInstance(this);

        //requestManager = RequestManager.getInstance(this);
        requestManager = new RequestManager(this, Constants.NOT_SINGLETON);
        //requestManager.init(this, Constants.RM_LOGIN_ACTIVITY);
        requestManager.setOnLoginActivityListener(this);

        JSONObject jsonCurrentUser = null;
        try {
            jsonCurrentUser = pref.getCurrentUser();
        } catch (NullPointerException e) {
            Log.e("userstatus", "null");
        }

        if (jsonCurrentUser != null) {
            Log.e("userstatus", "not null");
            onAccessGranted();
        } else {
            Log.e("userstatus", "null");
            initScreen();
            initFbCallback();
            initGCallback();
        }
    }

    private void initGCallback() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void initScreen() {
        rlRoot = (RelativeLayout) findViewById(R.id.content_sign_up);
        rlRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = SignUpActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        //bLogIn = (Button) findViewById(R.id.bLogIn);
        bSignUp = (Button) findViewById(R.id.bSignUp);
        //bLogIn.setOnClickListener(onClickListener);
        bSignUp.setOnClickListener(onClickListener);
        b_login_fb = (Button) findViewById(R.id.b_facebook);
        b_login_fb.setOnClickListener(onClickListener);
        //loginButtonGoogle = (SignInButton) findViewById(R.id.bGoogleLogin);
        b_google_plus = (Button) findViewById(R.id.b_google_plus);
        b_google_plus.setOnClickListener(onClickListener);

        //tvSignUp = (TextView) findViewById(R.id.login_tv_first_time_sign_up);
    }

    /**
     * user is used to save all FB users credentials
     * regardless of whether logging in or signing up has been successfully done.
     * If user signed up without troubles we save all credentials to sharedPreferences
     * in OnAccessGranted callback
     */
    private User user;
    private Map<String, String> userCredentials = new HashMap<>();

    private void initFbCallback() {
        //fb callbackManager
        callbackManager = CallbackManager.Factory.create();

        //loginButtonFB.setReadPermissions("email", "public_profile", "user_photos");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //new FetchUserData().execute(user);

                AccessToken token = loginResult.getAccessToken();
                GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        newUserComing = true;
                        user = new User();

                        Log.v("userjson", object.toString());

                        new AsyncTask<JSONObject, Void, Void>() {
                            @Override
                            protected Void doInBackground(JSONObject... params) {
                                try {
                                    JSONObject object = params[0];
                                    String name = object.getString("name");
                                    String link = object.getString("link");
                                    String id = object.getString("id");
                                    String imgUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                    Bitmap userProfilePhoto = BitmapFactory.decodeStream(
                                            new URL(imgUrl).openConnection().getInputStream());
                                    String b64UserPhoto = convertBitmapToBase64(userProfilePhoto);

                                    Log.v("bmPhoto", convertBitmapToBase64(userProfilePhoto));
                                    pref.saveUserPhoto(b64UserPhoto);

                                    user.setId(id);
                                    user.setName(name);
                                    user.setLink(link);
                                    user.setProfilePhoto(imgUrl);

                                    userCredentials.put("fb_uid", id);
                                    userCredentials.put("name", name);
                                    userCredentials.put("password", "0000");
                                    userCredentials.put("email", id + "@fb.com");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                sendCheckRequest();
                                pref.setHowUserLoggedIn(Constants.FACEBOOK_LOG_IN);


                                Log.v("checkuser", user.getId() + user.getProfilePhoto());
                            }
                        }.execute(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,cover,picture.type(large)");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                //isn't proper spot for intent to be placed here

            }

            @Override
            public void onCancel() {
                Log.e("fbcancel", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("fbcancel", error.toString());
            }
        });
    }

    private String convertBitmapToBase64(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendCheckRequest() {
        //TODO: SEND REQUEST
        requestManager.sendRequest(
                Constants.CHECK_USER_IN_DATABASE_WITH_SN_ID_ + "fb/" + user.getId(),
                Constants.TYPE_CHECK_USER_WITH_SN_ID);
        /** wait until callback has been called **/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.v("g_plus", result.getStatus().toString());
            if (result.isSuccess()) {
                final GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                Log.v("g_plus", acct.getDisplayName());
                Log.v("g_plus", acct.getEmail());

                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.v("g_plus", user.getEmail());
                                    prepareGUserBeforeSavingToPref(acct);
                                    checkGuser(acct);
                                    pref.setHowUserLoggedIn(Constants.GOOGLE_LOG_IN);
                                } else {
                                    // If sign in fails, display a message to the user.
                                }
                                // ...
                            }
                        });
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void prepareGUserBeforeSavingToPref(GoogleSignInAccount acct) {
        user = new User();
        newUserComing = true;

        name = acct.getDisplayName();
        id = acct.getId();
        email = acct.getEmail();
        password = "0000";

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfilePhoto("nophoto");

        //userCredentials.put("g_uid", id);
        userCredentials.put("name", name);
        userCredentials.put("password", password);
        userCredentials.put("email", email);
    }

    private void checkGuser(GoogleSignInAccount acct) {
        Log.v("g_plus", acct.getEmail());
        requestManager.sendRequest(Constants.CHECK_USER_IN_DATABASE_WITH_EMAIL_ + acct.getEmail(),
                Constants.TYPE_CHECK_USER_WITH_EMAIL,
                Constants.TYPE_CHECK_GOOGLE_USER_WITH_EMAIL);
    }

    private String email;
    private String name;
    private String password;
    private String id;

    public void signUpNewUser() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        int endIndex = email.indexOf("@");
        name = email.substring(0, endIndex);
        Log.v("signupuser", "name " + name);
        Log.v("signupuser", "password " + password);
        Log.v("signupuser", "email " + email);

        newUserComing = true;
        user = new User();

        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setProfilePhoto("nophoto");

        userCredentials.put("name", name);
        userCredentials.put("password", password);
        userCredentials.put("email", email);

        requestManager.sendRequest(Constants.CREATE_NEW_USER, Constants.TYPE_CREATE_USER, userCredentials);
        pref.setHowUserLoggedIn(Constants.EMAIL_LOG_IN);
    }

    private void logIn() {
        newUserComing = true;
        prepareUserCredentBeforeSavingToPref();
        requestManager.sendRequest(Constants.CHECK_USER_IN_DATABASE_WITH_EMAIL_ + email, Constants.TYPE_CHECK_USER_WITH_EMAIL);
        pref.setHowUserLoggedIn(Constants.EMAIL_LOG_IN);
    }

    private void prepareUserCredentBeforeSavingToPref() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        int endIndex = email.indexOf("@");
        name = email.substring(0, endIndex);

        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setProfilePhoto("nophoto");
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bLogIn:
                    //logIn();
                    break;
                case R.id.bSignUp:
                    Log.v("bsignup", "click");
                    signUpNewUser();
                    break;
                case R.id.toolbar:
                    onBackPressed();
                    break;
                case R.id.b_facebook:
                    LoginManager.getInstance()
                            .logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile", "user_photos"));
                    break;
                case R.id.b_google_plus:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, 1);
                    break;
            }
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("g_plus", connectionResult.getErrorMessage());
    }
}
