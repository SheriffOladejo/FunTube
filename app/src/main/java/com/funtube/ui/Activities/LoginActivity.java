package com.funtube.ui.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.ApiResponse;
import com.funtube.Provider.PrefManager;
import com.funtube.ui.view.OtpEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{


    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private LoginButton sign_in_button_facebook;
    private SignInButton sign_in_button_google;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;

    private ProgressDialog register_progress;
    private TextView text_view_skip_login;
    private RelativeLayout relative_layout_google_login;
    private RelativeLayout relative_layout_phone_login;
    String VerificationCode = "";

    private ProgressBar phone_bar;
    private ProgressBar google_bar;
    private ProgressBar facebook_bar;
    private TextView phone_text;
    private TextView google_text;

    private CountryCodePicker countryCodePicker;
    private RelativeLayout relative_layout_confirm_phone_number;
    private OtpEditText otp_edit_text_login_activity;
    private RelativeLayout relative_layout_confirm_top_login_activity;
    private EditText edit_text_phone_number_login_acitivty;
    private LinearLayout linear_layout_buttons_login_activity;
    private LinearLayout linear_layout_otp_confirm_login_activity;
    private LinearLayout linear_layout_phone_input_login_activity;
    private RelativeLayout relative_layout_confirm_full_name;
    private LinearLayout linear_layout_name_input_login_activity;
    private EditText edit_text_name_login_acitivty;
    private String phoneNum ="";
    private PrefManager prf;

    private EditText edit_text_reference_code;
    private RelativeLayout relative_layout_reference_coode;
    private Button btn_send_code;
    private boolean referenceCodeAsked = false;
    private boolean askReferenceDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        prf= new PrefManager(getApplicationContext());

        if (prf.getString("LOGGED").toString().equals("TRUE")){
            Intent intent= new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        initView();
        initAction();
        FacebookSignIn();
        GoogleSignIn();
    }


    public void initView(){

        this.btn_send_code   =      (Button)  findViewById(R.id.btn_send_code);
        this.edit_text_reference_code   =      (EditText)  findViewById(R.id.edit_text_reference_code);
        this.relative_layout_reference_coode   =      (RelativeLayout)  findViewById(R.id.relative_layout_reference_coode);
        this.phone_bar = findViewById(R.id.progress_bar_phone);
        this.facebook_bar = findViewById(R.id.progress_bar_facebook);
        this.google_bar = findViewById(R.id.progress_bar_google);
        this.phone_text = findViewById(R.id.text_view_phone);
        this.google_text = findViewById(R.id.textview_google);
        this.edit_text_name_login_acitivty   =      (EditText)  findViewById(R.id.edit_text_name_login_acitivty);
        this.edit_text_phone_number_login_acitivty   =      (EditText)  findViewById(R.id.edit_text_phone_number_login_acitivty);
        this.otp_edit_text_login_activity   =      (OtpEditText)  findViewById(R.id.otp_edit_text_login_activity);
        this.relative_layout_confirm_top_login_activity   =      (RelativeLayout)  findViewById(R.id.relative_layout_confirm_top_login_activity);
        this.relative_layout_google_login   =      (RelativeLayout)  findViewById(R.id.relative_layout_google_login);
        this.sign_in_button_google   =      (SignInButton)  findViewById(R.id.sign_in_button_google);
        this.sign_in_button_facebook =      (LoginButton)   findViewById(R.id.sign_in_button_facebook);
        this.sign_in_button_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in_button_facebook.setLoginText("");
                facebook_bar.setVisibility(View.VISIBLE);
            }
        });
        this.relative_layout_phone_login =      (RelativeLayout)   findViewById(R.id.relative_layout_phone_login);
        this.relative_layout_confirm_phone_number =      (RelativeLayout)   findViewById(R.id.relative_layout_confirm_phone_number);
        this.linear_layout_buttons_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_buttons_login_activity);
        this.linear_layout_otp_confirm_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_otp_confirm_login_activity);
        this.linear_layout_phone_input_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_phone_input_login_activity);
        this.linear_layout_name_input_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_name_input_login_activity);
        this.relative_layout_confirm_full_name =      (RelativeLayout)   findViewById(R.id.relative_layout_confirm_full_name);

        this.countryCodePicker =      (CountryCodePicker)   findViewById(R.id.CountryCodePicker);

    }
    public void initAction(){
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_reference_code.getText().toString().trim().length()<4){
                    Toasty.error(LoginActivity.this, "Reference key very short try to use connect one ", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendCode();
            }
        });
        relative_layout_confirm_full_name.setOnClickListener(v->{
            String  token = FirebaseInstanceId.getInstance().getToken();
            String token_user =  prf.getString("TOKEN_USER");
            String id_user =  prf.getString("ID_USER");
            if (edit_text_name_login_acitivty.getText().toString().length()<3) {
                Toasty.error(getApplicationContext(), "This name very shot ", Toast.LENGTH_LONG).show();
                return;
            }
            askReferenceDialog =  true;
            updateToken(Integer.parseInt(id_user),token_user,token,edit_text_name_login_acitivty.getText().toString());

        });
        relative_layout_confirm_top_login_activity.setOnClickListener(v->{
            if(otp_edit_text_login_activity.getText().toString().length()<6){
                Toasty.error(this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG, "verificationCode: " + VerificationCode);
                if (otp_edit_text_login_activity.getText().toString().trim().equals(VerificationCode.toString().trim())){
                    String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
                    signUp(phoneNum,phoneNum,"null".toString(),"phone",photo);
                }else{
                    Toasty.error(this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.relative_layout_phone_login.setOnClickListener(v -> {
            linear_layout_buttons_login_activity.setVisibility(View.GONE);
            linear_layout_phone_input_login_activity.setVisibility(View.VISIBLE);
        });
        relative_layout_confirm_phone_number.setOnClickListener(v ->{
            phoneNum = "+"+countryCodePicker.getSelectedCountryCode().toString()+edit_text_phone_number_login_acitivty.getText().toString();

            new AlertDialog.Builder(this)
                    .setTitle("We will be verifying the phone number:"  )
                    .setMessage(" \n"+phoneNum+" \n\n Is this OK,or would you like to edit the number ?")
                    .setPositiveButton("Confirm",
                            (dialog, which) -> {
                                //Do Something Here
                                loginWithPhone();

                            })
                    .setNegativeButton("Edit",
                            (dialog, which) -> {
                                dialog.dismiss();
                            }).show();
        });
        relative_layout_google_login.setOnClickListener(view -> signIn());
        this.sign_in_button_google.setOnClickListener(view -> signIn());

    }

    private void loginWithPhone() {
        linear_layout_phone_input_login_activity.setVisibility(View.GONE);
        linear_layout_otp_confirm_login_activity.setVisibility(View.VISIBLE);

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                VerificationCode = phoneAuthCredential.getSmsCode().toString();
                if(!VerificationCode.equals("") && VerificationCode!=null){
                    String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
                    signUp(phoneNum,phoneNum,"null".toString(),"phone",photo);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toasty.error(LoginActivity.this, "Unable to verify your number", Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
            }
        };

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum,
                30,
                TimeUnit.SECONDS,
                LoginActivity.this,
                mCallbacks);


//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNum, 30L /*timeout*/, TimeUnit.SECONDS,
//                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                    }
//
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                        VerificationCode = phoneAuthCredential.getSmsCode().toString();
//                    }
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//                        Toasty.error(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getResultGoogle(result);
        }
    }
    public void GoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    public void FacebookSignIn(){

        FacebookSdk.sdkInitialize(LoginActivity.this);
        // Other app specific specializationsign_in_button_facebook.setReadPermissions(Arrays.asList("public_profile"));
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        sign_in_button_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebook_bar.setVisibility(View.GONE);
                sign_in_button_facebook.setLoginText("Continue with Facebook");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getResultFacebook(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
                Toasty.success(getApplicationContext(), "Operation Successful!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Registration successful");
            }

            @Override
            public void onCancel() {
                facebook_bar.setVisibility(View.GONE);
                sign_in_button_facebook.setLoginText("Continue with Facebook");
                set(LoginActivity.this, "Operation has been cancelled ! ");
                Log.d(TAG, "Registration cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                facebook_bar.setVisibility(View.GONE);
                sign_in_button_facebook.setLoginText("Continue with Facebook");
                set(LoginActivity.this, "Operation failed ! ");
                Log.d(TAG, "Registration failed");
            }
        });
    }
    private void signIn() {
        google_text.setText("");
        google_bar.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private void getResultGoogle(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        google_text.setText("Continue with google");
        google_bar.setVisibility(View.GONE);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
            if (acct.getPhotoUrl()!=null){
                photo =  acct.getPhotoUrl().toString();
            }
            Log.d(TAG, "google sign in success:");
            signUp(acct.getId().toString(),acct.getId(), acct.getDisplayName().toString(),"google",photo);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {
            Log.d(TAG, "google sign in failed:");
        }
    }
    private void getResultFacebook(JSONObject object){
        Log.d(TAG, object.toString());

        try {
            Log.d(TAG, "facebook sign in:" + object.toString());
            signUp(object.getString("id").toString(),object.getString("id").toString(),object.getString("name").toString(),"facebook",object.getJSONObject("picture").getJSONObject("data").getString("url"));
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "facebook sign in:" + e.getMessage());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void signUp(String username,String password,String name,String type,String image){
        try {
            register_progress = ProgressDialog.show(getApplicationContext(), null, getResources().getString(R.string.operation_progress), true);
        }
        catch(Exception e){}
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name,username,password,type,image);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){

                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        String registered="x";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("registered")){
                                registered=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
                            String  token = FirebaseInstanceId.getInstance().getToken();
                            if (name_user.toLowerCase().equals("null")){
                                linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                                linear_layout_name_input_login_activity.setVisibility(View.VISIBLE);
                            }else{
                                if (registered.equals("true")){
                                    if (prf.getString("EARNING_SYSTEM").equals("TRUE")){
                                        relative_layout_reference_coode.setVisibility(View.VISIBLE);
                                        linear_layout_buttons_login_activity.setVisibility(View.GONE);
                                        referenceCodeAsked = true;
                                    }
                                }
                                updateToken(Integer.parseInt(id_user),token_user,token,name_user);
                            }
                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(), "Operation has been cancelled! Response code is 500 ", Toast.LENGTH_SHORT, true).show();
                    }
                }

                else{
                    Toasty.error(getApplicationContext(), "Operation has been cancelled! Response body is empty ", Toast.LENGTH_SHORT, true).show();
                }
                try {
                    register_progress.dismiss();
                }
                catch(Exception e){}
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                try {
                    register_progress.dismiss();
                }
                catch(Exception e){}
            }
        });
    }
    public static void set(Activity activity, String s){
        Toasty.error(activity,s,Toast.LENGTH_LONG).show();
        activity.finish();
    }
    public void updateToken(Integer id,String key,String token,String name){
        if (askReferenceDialog)
            register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id,key,token,name);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                prf.setString("NAME_USER",name );
                try {
                    register_progress.dismiss();
                }
                catch(Exception e){}
                if (response.isSuccessful()){
                    Toasty.success(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                }
                if (askReferenceDialog){
                    if (prf.getString("EARNING_SYSTEM").equals("TRUE")){
                        relative_layout_reference_coode.setVisibility(View.VISIBLE);
                        linear_layout_buttons_login_activity.setVisibility(View.GONE);
                        linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                        linear_layout_name_input_login_activity.setVisibility(View.GONE);
                        referenceCodeAsked = true;
                    }else{
                        finish();
                    }
                }
                if (!referenceCodeAsked){
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                try {
                    register_progress.dismiss();
                }
                catch(Exception e){}
                Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                if (askReferenceDialog){
                    if (prf.getString("EARNING_SYSTEM").equals("TRUE")){
                        relative_layout_reference_coode.setVisibility(View.VISIBLE);
                        linear_layout_buttons_login_activity.setVisibility(View.GONE);
                        linear_layout_otp_confirm_login_activity.setVisibility(View.GONE);
                        linear_layout_name_input_login_activity.setVisibility(View.GONE);
                        referenceCodeAsked = true;
                    }else{
                        finish();
                    }
                }
                if (!referenceCodeAsked){
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }

    public void sendCode(){
        final PrefManager prefManager = new PrefManager(this);
        Integer id_user = 0;
        String key_user= "";
        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
            id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
            key_user=  prefManager.getString("TOKEN_USER");
        }
        register_progress = new ProgressDialog(LoginActivity.this);
        register_progress.setCancelable(true);
        register_progress.setMessage(getResources().getString(R.string.operation_progress));
        register_progress.show();
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.sendRefereceCode(id_user,key_user,edit_text_reference_code.getText().toString().trim());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                register_progress.dismiss();
                if (response.isSuccessful()){
                    if (response.body().getCode().equals(200)) {
                        Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                        finish();
                    }else{
                        Toasty.error(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                Toasty.error(getApplicationContext(), "Operation has been cancelled!"+t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                super.onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

