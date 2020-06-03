package com.funtube.ui.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.login.LoginManager;
import com.funtube.Adapters.LanguageAdapter;
import com.funtube.Adapters.SelectableViewHolder;
import com.funtube.InAppBillingUtils.IabBroadcastReceiver;
import com.funtube.InAppBillingUtils.IabHelper;
import com.funtube.InAppBillingUtils.IabResult;
import com.funtube.InAppBillingUtils.Inventory;
import com.funtube.InAppBillingUtils.Purchase;
import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.config.Global;
import com.funtube.model.ApiResponse;
import com.funtube.model.Language;
import com.funtube.ui.fragement.CategroiesFragement;
import com.funtube.ui.fragement.DownloadsFragement;
import com.funtube.ui.fragement.FavoritesFragment;
import com.funtube.ui.fragement.FollowFragment;
import com.funtube.ui.fragement.HomeFragment;
import com.funtube.ui.fragement.PopularFragment;
import com.funtube.ui.view.ScrollHandler;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.funtube.InAppBillingUtils.Constants.SKU_FUNTUBE_3MONTHS;
import static com.funtube.InAppBillingUtils.Constants.SKU_FUNTUBE_MONTHLY;
import static com.funtube.InAppBillingUtils.Constants.SKU_FUNTUBE_WEEKLY;
import static com.funtube.InAppBillingUtils.Constants.base64EncodedPublicKey;

public class MainActivity extends AppCompatActivity
        implements SelectableViewHolder.OnItemSelectedListener ,
        NavigationView.OnNavigationItemSelectedListener,
        DialogInterface.OnClickListener{


    private Boolean EarningSystem  = true;
    private Dialog rateDialog;

    private static final String TAG ="MainActivity ----- : " ;
    ConsentForm form;

    private MaterialSearchView searchView;
    private ViewPagerAdapter adapter;
    private NavigationView navigationView;

    private RewardedVideoAd mRewardedVideoAd;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private AlertDialog.Builder builderLanguage;
    private PrefManager prefManager;
    private Menu menu;
    private LanguageAdapter languageAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragments;

    private TextView text_view_name_nave_header;
    private CircleImageView circle_image_view_profile_nav_header;

    private  Boolean FromLogin = false;

    boolean weeklySub = false;
    boolean monthlySub = false;
    boolean months3 = false;
    static boolean youTubeActive = false;

    private FollowFragment followFragment;
    private Dialog dialog;
    private  Boolean DialogOpened = false;
    private TextView text_view_go_pro;

    IInAppBillingService mService;

    private static final String LOG_TAG = "iabv3";

    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();

        }
    };
    private String old_language;
    private MenuItem item_language;
    private SpeedDialView speed_dial_main_activity;
    private int openAction = 80001;

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,   Manifest.permission.READ_CONTACTS)) {
                    Intent intent_status  =  new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                } else {
                    Intent intent_status  =  new Intent(getApplicationContext(), PermissionActivity.class);
                    startActivity(intent_status);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBuy();
        prefManager= new PrefManager(getApplicationContext());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_side_nav);
        toolbar.getOverflowIcon().setTint(ContextCompat.getColor(this, R.color.black));
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("EARNING_SYSTEM").equals("TRUE")){
            EarningSystem = true;
        }else{
            EarningSystem = false;
        }
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel("MyNotif", "MyNotif", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }

        Log.d(TAG, "Creating IAB helper.");

        checkPermission();
        initData();
        iniView();
        loadLang();
        initAction();
        firebaseSubscribe();
        initEvent();
        initGDPR();
        initRewarded();
        loadRewardedVideoAd();
    }

    public void initRewarded() {

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d("Rewarded","onRewardedVideoAdOpened ");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d("Rewarded","onRewardedVideoStarted ");

            }

            @Override
            public void onRewardedVideoCompleted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                loadRewardedVideoAd();
                Log.d("Rewarded","onRewardedVideoAdClosed ");

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

                    switch (openAction){
                        case 80001:
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadYoutubeActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                            break;
                        case 80002:
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadGifActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                            break;
                        case 80003:
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadImageActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                            break;
                        case 80004:
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadVideoActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                            break;
                        case 80005:
                            if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadQuoteActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                            break;
                    }

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d("Rewarded","onRewardedVideoAdFailedToLoad "+ i);
            }
        });

    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }
    public void loadRewardedVideoAd() {
        PrefManager     prefManager= new PrefManager(getApplicationContext());
        mRewardedVideoAd.loadAd(prefManager.getString("ADMIN_REWARDED_ADMOB_ID"),
                new AdRequest.Builder().build());
    }


    private void initGDPR() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ConsentInformation consentInformation =
                ConsentInformation.getInstance(MainActivity.this);
//// test
/////
        PrefManager prf= new PrefManager(getApplicationContext());
        String[] publisherIds = {  prf.getString("ADMIN_PUBLISHER_ID")};
        consentInformation.requestConsentInfoUpdate(publisherIds, new
                ConsentInfoUpdateListener() {
                    @Override
                    public void onConsentInfoUpdated(ConsentStatus consentStatus) {
// User's consent status successfully updated.
                        Log.d(TAG,"onConsentInfoUpdated");
                        switch (consentStatus){
                            case PERSONALIZED:
                                Log.d(TAG,"PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.PERSONALIZED);
                                break;
                            case NON_PERSONALIZED:
                                Log.d(TAG,"NON_PERSONALIZED");
                                ConsentInformation.getInstance(MainActivity.this)
                                        .setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                break;


                            case UNKNOWN:
                                Log.d(TAG,"UNKNOWN");
                                if
                                        (ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown
                                        ()){
                                    URL privacyUrl = null;
                                    try {
// TODO: Replace with your app's privacy policy URL.
                                        privacyUrl = new URL(Global.API_URL.replace("/api/","/privacy_policy.html"));

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
// Handle error.

                                    }
                                    form = new ConsentForm.Builder(MainActivity.this,
                                            privacyUrl)
                                            .withListener(new ConsentFormListener() {
                                                @Override
                                                public void onConsentFormLoaded() {
                                                    Log.d(TAG,"onConsentFormLoaded");
                                                    showform();
                                                }
                                                @Override
                                                public void onConsentFormOpened() {
                                                    Log.d(TAG,"onConsentFormOpened");
                                                }
                                                @Override
                                                public void onConsentFormClosed( ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                                    Log.d(TAG,"onConsentFormClosed");
                                                }
                                                @Override
                                                public void onConsentFormError(String errorDescription) {
                                                    Log.d(TAG,"onConsentFormError");
                                                    Log.d(TAG,errorDescription);
                                                }
                                            })
                                            .withPersonalizedAdsOption()
                                            .withNonPersonalizedAdsOption()
                                            .build();
                                    form.load();
                                } else {
                                    Log.d(TAG,"PERSONALIZED else");
                                    ConsentInformation.getInstance(MainActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    @Override
                    public void onFailedToUpdateConsentInfo(String errorDescription) {
// User's consent status failed to update.
                        Log.d(TAG,"onFailedToUpdateConsentInfo");
                        Log.d(TAG,errorDescription);
                    }
                });
    }
    private void showform(){
        if (form!=null){
            Log.d(TAG,"show ok");
            form.show();
        }
    }

    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, base64EncodedPublicKey, new BillingProcessor.IBillingHandler() {

            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                Intent intent= new Intent(MainActivity.this,SlideActivity.class);
                Toasty.success(MainActivity.this, "Purchase Successful", Toasty.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
                //updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Toasty.error(MainActivity.this, "Billing Error", Toasty.LENGTH_SHORT).show();
            }
            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.initialize();
        weeklySub = bp.isSubscribed(SKU_FUNTUBE_WEEKLY);
        monthlySub = bp.isSubscribed(SKU_FUNTUBE_MONTHLY);
        months3 = bp.isSubscribed(SKU_FUNTUBE_3MONTHS);
        Log.d(TAG, "weekly"+weeklySub);
        Log.d(TAG, "month"+monthlySub);
        Log.d(TAG, "3months"+months3);

        if(weeklySub || monthlySub || months3)
            youTubeActive = true;
    }

    private void updateTextViews() {
        PrefManager prf= new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }

    public Bundle getPurchases(){
        if (!bp.isInitialized()) {
            return null;
        }
        try{
            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void firebaseSubscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("FunTube").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
                else{
                    Toasty.error(MainActivity.this, "Unable to subscribe for firebase FCM", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initAction() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent  = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        /*this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prf= new PrefManager(getApplicationContext());
                if (prf.getString("LOGGED").toString().equals("TRUE")){


                        Intent intent_video  =  new Intent(getApplicationContext(), UploadVideoActivity.class);
                        startActivity(intent_video);
                        overridePendingTransition(R.anim.enter, R.anim.exit);


                }else{
                    Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    FromLogin=true;

                }
            }
        });*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        MenuItem item = menu.findItem(R.id.action_search);
        item_language = menu.findItem(R.id.action_language);
        searchView.setMenuItem(item);

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_language :
                Intent intent = new Intent(MainActivity.this, LanguageActivity.class);
                startActivity(intent);
                //finish();
                break;
            case R.id.action_pro :
                showDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setIconItem(final MenuItem item,String url){

        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                BitmapDrawable mBitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                item.setIcon(mBitmapDrawable);
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.d("DEBUG", "onBitmapFailed");
                item.setIcon(getResources().getDrawable(R.drawable.ic_global));

            }
            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d("DEBUG", "onPrepareLoad");
            }
        };
        Picasso.with(this).load(url).placeholder(R.drawable.flag_placeholder).error(R.drawable.flag_placeholder).into(mTarget);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            viewPager.setCurrentItem(0);
        }else if(id == R.id.login){
            Intent intent= new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            FromLogin=true;

        }else if (R.id.whatsapp_saver== id){
            Intent intent= new Intent(MainActivity.this, WhatsAppActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_exit) {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
            }
        }else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id==R.id.my_profile){
            PrefManager prf= new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")){
                Intent intent  =  new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("image",prf.getString("IMAGE_USER").toString());
                intent.putExtra("name",prf.getString("NAME_USER").toString());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }else{
                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                FromLogin=true;
            }
        }else if (id==R.id.my_earnings){
            PrefManager prf= new PrefManager(getApplicationContext());
            if (prf.getString("LOGGED").toString().equals("TRUE")){
                Intent intent  =  new Intent(getApplicationContext(), EarningActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }else{
                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                FromLogin=true;
            }
        }
        else if (id==R.id.logout){
            logout();
        }
        else if (id==R.id.nav_share){
            final String appPackageName=getApplication().getPackageName();
            String shareBody = "Download "+getString(R.string.app_name)+" From :  "+"http://play.google.com/store/apps/details?id=" + appPackageName;
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
        }else if (id == R.id.nav_rate) {
            rateDialog(false);
        }else if (id == R.id.nav_help){
            Intent intent = new Intent(getApplicationContext(), SupportActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (id == R.id.nav_policy  ){
            Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }else if (id == R.id.buy_now){
            showDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void iniView() {

        this.speed_dial_main_activity = (SpeedDialView) findViewById(R.id.speed_dial_main_activity);
        speed_dial_main_activity.setMainFabClosedBackgroundColor(getResources().getColor(R.color.green));
        speed_dial_main_activity.setMainFabOpenedBackgroundColor(getResources().getColor(R.color.green));
        speed_dial_main_activity.inflate(R.menu.menu_speed_dial);

        speed_dial_main_activity.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_gif,R.drawable.ic_gif)
                        //.setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()))
                        .setLabel(getString(R.string.upload_gif))
                        .setFabBackgroundColor(getResources().getColor((R.color.maincolor)))
                        .setLabelBackgroundColor(getResources().getColor((R.color.white)))
                        .create()
        );
        speed_dial_main_activity.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_video,R.drawable.ic_videocam)
                        //.setFabImageTintColor(ResourcesCompat.getColor(getResources(),  R.color.maincolor, getTheme()))
                        .setLabel(getString(R.string.upload_video))
                        .setFabBackgroundColor(getResources().getColor((R.color.maincolor)))
                        .setLabelBackgroundColor(getResources().getColor((R.color.white)))
                        .create()
        );
        Log.d(TAG, "you"+youTubeActive);

            speed_dial_main_activity.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_youtube,R.drawable.ic_youtube)
                        //.setFabImageTintColor(ResourcesCompat.getColor(getResources(),  R.color.white, getTheme()))
                        .setLabel(getString(R.string.upload_youtube))
                        .setFabBackgroundColor(getResources().getColor((R.color.maincolor)))
                        .setLabelBackgroundColor(getResources().getColor((R.color.white)))
                        .create()
            );
        speed_dial_main_activity.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_image,R.drawable.ic_image)
                        //.setFabImageTintColor(ResourcesCompat.getColor(getResources(),  R.color.white, getTheme()))
                        .setLabel(getString(R.string.upload_image))
                        .setFabBackgroundColor(getResources().getColor((R.color.maincolor)))
                        .setLabelBackgroundColor(getResources().getColor((R.color.white)))
                        .create()
        );
        speed_dial_main_activity.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_quote,R.drawable.ic_quote)
                        //.setFabImageTintColor(ResourcesCompat.getColor(getResources(),  R.color.white, getTheme()))
                        .setLabel(getString(R.string.write_quote))
                        .setFabBackgroundColor(getResources().getColor((R.color.maincolor)))
                        .setLabelBackgroundColor(getResources().getColor((R.color.white)))
                        .create()
        );
        final PrefManager prf= new PrefManager(getApplicationContext());

        speed_dial_main_activity.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.fab_youtube:
                        if(youTubeActive){
                            if (!checkSUBSCRIBED()) {
                                if (mRewardedVideoAd.isLoaded()) {
                                    mRewardedVideoAd.show();
                                    openAction = 80001;
                                } else {
                                    if (prf.getString("LOGGED").toString().equals("TRUE")) {
                                        startActivity(new Intent(MainActivity.this, UploadYoutubeActivity.class));
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                    } else {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        FromLogin = true;
                                    }
                                }
                            }else{
                                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                                    startActivity(new Intent(MainActivity.this, UploadYoutubeActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    FromLogin = true;
                                }
                            }
                            return false;
                        }
                        else{
                            showDialog();
                            return true;
                        }
                         // true to keep the Speed Dial open
                    case R.id.fab_gif:
                        if (!checkSUBSCRIBED()) {
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                                openAction = 80002;
                            } else {
                                if (prf.getString("LOGGED").toString().equals("TRUE")) {
                                    startActivity(new Intent(MainActivity.this, UploadGifActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    FromLogin = true;
                                }
                            }
                        }else{
                            if (prf.getString("LOGGED").toString().equals("TRUE")) {
                                startActivity(new Intent(MainActivity.this, UploadGifActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin = true;
                            }
                        }

                        return false; // true to keep the Speed Dial open
                    case R.id.fab_image:

                        if (!checkSUBSCRIBED()) {
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                                openAction = 80003;
                            } else {
                                if (prf.getString("LOGGED").toString().equals("TRUE")){
                                    startActivity(new Intent(MainActivity.this,UploadImageActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }else{
                                    Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    FromLogin=true;
                                }
                            }
                        }else{
                            if (prf.getString("LOGGED").toString().equals("TRUE")){
                                startActivity(new Intent(MainActivity.this,UploadImageActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }else{
                                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin=true;
                            }
                        }

                        return false; // true to keep the Speed Dial open
                    case R.id.fab_video:

                        if (!checkSUBSCRIBED()) {
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                                openAction = 80004;
                            } else {
                                if (prf.getString("LOGGED").toString().equals("TRUE")){
                                    startActivity(new Intent(MainActivity.this,UploadVideoActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }else{
                                    Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    FromLogin=true;
                                }
                            }
                        }else{
                            if (prf.getString("LOGGED").toString().equals("TRUE")){
                                startActivity(new Intent(MainActivity.this,UploadVideoActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }else{
                                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin=true;
                            }
                        }


                        return false; // true to keep the Speed Dial open
                    case R.id.fab_quote:
                        if (!checkSUBSCRIBED()) {
                            if (mRewardedVideoAd.isLoaded()) {
                                mRewardedVideoAd.show();
                                openAction = 80005;
                            } else {
                                if (prf.getString("LOGGED").toString().equals("TRUE")){
                                    startActivity(new Intent(MainActivity.this,UploadQuoteActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }else{
                                    Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    FromLogin=true;
                                }
                            }
                        }else{
                            if (prf.getString("LOGGED").toString().equals("TRUE")){
                                startActivity(new Intent(MainActivity.this,UploadQuoteActivity.class));
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }else{
                                Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                FromLogin=true;
                            }
                        }

                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });
        this.followFragment = new FollowFragment();
        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new PopularFragment());
        adapter.addFragment(followFragment);
        adapter.addFragment(new CategroiesFragement());
        adapter.addFragment(new FavoritesFragment());
        adapter.addFragment(new DownloadsFragement());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);



        final BubbleNavigationConstraintView bubbleNavigationLinearView =(BubbleNavigationConstraintView) findViewById(R.id.top_navigation_constraint);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bubbleNavigationLinearView.getLayoutParams();
        layoutParams.setBehavior(new ScrollHandler());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });



        View headerview = navigationView.getHeaderView(0);
        this.text_view_name_nave_header=(TextView) headerview.findViewById(R.id.text_view_name_nave_header);
        this.circle_image_view_profile_nav_header=(CircleImageView) headerview.findViewById(R.id.circle_image_view_profile_nav_header);
        this.viewPager=(ViewPager) findViewById(R.id.vp_horizontal_ntb);
        this.viewPager.setOffscreenPageLimit(100);

        // set Adapters

        viewPager.setAdapter(adapter);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.parent);

    }
    private void initEvent() {
        // set listener to change the current item of view pager when click bottom nav item

    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    private void initData() {
        fragments = new ArrayList<>(4);

        // create music fragment and add it
        HomeFragment homeFragment = new HomeFragment();


        // create backup fragment and add it
        PopularFragment popularFragment = new PopularFragment();

        // create friends fragment and add it
        FavoritesFragment favorFragment = new FavoritesFragment();

        // create friends fragment and add it
        followFragment = new FollowFragment();


        // add to fragments for Adapters
        fragments.add(homeFragment);
        fragments.add(popularFragment);
        fragments.add(followFragment);
        fragments.add(favorFragment);

    }
    public void setFromLogin(){
        this.FromLogin = true;
    }


    @Override
    public void onItemSelected(Language item) {

        List<Language> selectedItems = languageAdapter.getSelectedItems();
        //  Toast.makeText(MainActivity.this,"Selected item is "+ item.getLanguage()+ ", Totally  selectem item count is "+selectedItems.size(),Toast.LENGTH_LONG).show();
    }



    public int getDefaultLangiage(){
        return prefManager.getInt("LANGUAGE_DEFAULT");
    }

    public void loadLang(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.languageAll();
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>1){
                        try {
                            item_language.setVisible(true);
                        }catch (NullPointerException e){

                        }
                        if (!prefManager.getString("first_lang_set").equals("true")){
                            prefManager.setString("first_lang_set","true");
                            Intent intent_status  =  new Intent(getApplicationContext(), LanguageActivity.class);
                            startActivity(intent_status);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    }else{
                        if (item_language!=null){
                            item_language.setVisible(false);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
            }
        });
    }
    public void logout(){
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.remove("LOGGED");
        LoginManager.getInstance().logOut();
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")){
            }else {
            }
        }else{
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.my_earnings).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);
            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        followFragment.Resume();

        Toast.makeText(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);

        super.onResume();
        checkPermission();

        updateTextViews();

        PrefManager prf= new PrefManager(getApplicationContext());

        Menu nav_Menu = navigationView.getMenu();


        if (prf.getString("LOGGED").toString().equals("TRUE")){
            nav_Menu.findItem(R.id.my_profile).setVisible(true);
            if (EarningSystem){
                nav_Menu.findItem(R.id.my_earnings).setVisible(false);
            }else{
                nav_Menu.findItem(R.id.my_earnings).setVisible(false);

            }
            nav_Menu.findItem(R.id.logout).setVisible(true);
            nav_Menu.findItem(R.id.login).setVisible(false);
            text_view_name_nave_header.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
            if (prf.getString("TYPE_USER").toString().equals("google")){
            }else {
            }
        }else{
            nav_Menu.findItem(R.id.my_earnings).setVisible(false);
            nav_Menu.findItem(R.id.my_profile).setVisible(false);
            nav_Menu.findItem(R.id.logout).setVisible(false);
            nav_Menu.findItem(R.id.login).setVisible(true);

            text_view_name_nave_header.setText(getResources().getString(R.string.please_login));
            Picasso.with(getApplicationContext()).load(R.drawable.profile).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_profile_nav_header);
        }
        if (FromLogin){
            followFragment.Resume();
            FromLogin = false;
        }
        if(old_language==null){
            old_language = prefManager.getString("LANGUAGE_DEFAULT");
        }else{
            if (old_language!=prefManager.getString("LANGUAGE_DEFAULT")){
                old_language = prefManager.getString("LANGUAGE_DEFAULT");
                Intent intent_save = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent_save);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final PrefManager prf = new PrefManager(getApplicationContext());
            if (prf.getString("NOT_RATE_APP").equals("TRUE")) {
                super.onBackPressed();
            } else {
                rateDialog(true);
                return;
            }
        }

    }

    int which = 0;
    public void showDialog(){
        this.dialog = new Dialog(this,
                R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscribe);

        this.text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);
        RelativeLayout relativeLayout_close_rate_gialog=(RelativeLayout) dialog.findViewById(R.id.relativeLayout_close_rate_gialog);
        relativeLayout_close_rate_gialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        this.text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);

        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(prefManager.getString("LOGGED").equals("TRUE")){
                    if(weeklySub && !monthlySub && !months3){
                        CharSequence[] options;
                        options = new CharSequence[2];
                        options[0] = "Monthly Sub: 4800PKR";
                        options[1] = "3-Months Sub: 8000PKR";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Upgrade/Downgrade Sub")
                                .setSingleChoiceItems(options, 0 /* checkedItem */, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        which = i;
                                    }
                                })
                                .setPositiveButton(R.string.subscription_prompt_continue, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (which == 0 /* First choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_WEEKLY, SKU_FUNTUBE_MONTHLY);
                                        } else if (which == 1 /* Second choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_WEEKLY, SKU_FUNTUBE_3MONTHS);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.subscription_prompt_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(monthlySub && !weeklySub && !months3){
                        CharSequence[] options;
                        options = new CharSequence[2];
                        options[0] = "Weekly Sub: 1600PKR";
                        options[1] = "3-Months Sub: 8000PKR";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Upgrade/Downgrade Sub")
                                .setSingleChoiceItems(options, 0 /* checkedItem */, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        which = i;
                                    }
                                })
                                .setPositiveButton(R.string.subscription_prompt_continue, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (which == 0 /* First choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_MONTHLY, SKU_FUNTUBE_WEEKLY);
                                        } else if (which == 1 /* Second choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_MONTHLY, SKU_FUNTUBE_3MONTHS);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.subscription_prompt_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(months3 && !weeklySub && !monthlySub){
                        CharSequence[] options;
                        options = new CharSequence[2];
                        options[0] = "Weekly Sub: 1600PKR";
                        options[1] = "Monthly Sub: 4800PKR";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Upgrade/Downgrade Sub")
                                .setSingleChoiceItems(options, 0 /* checkedItem */, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        which = i;
                                    }
                                })
                                .setPositiveButton(R.string.subscription_prompt_continue, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (which == 0 /* First choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_3MONTHS, SKU_FUNTUBE_WEEKLY);
                                        } else if (which == 1 /* Second choice item */) {
                                            if(readyToPurchase)
                                                bp.updateSubscription(MainActivity.this, SKU_FUNTUBE_3MONTHS, SKU_FUNTUBE_MONTHLY);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.subscription_prompt_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else{
                        CharSequence[] options;
                        options = new CharSequence[3];
                        options[0] = "Weekly Sub: 1600PKR";
                        options[1] = "Monthly Sub: 4800PKR";
                        options[2] = "3-Months Sub: 8000PKR";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Select Sub")
                                .setSingleChoiceItems(options, 0 /* checkedItem */, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        which = i;
                                    }
                                })
                                .setPositiveButton(R.string.subscription_prompt_continue, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (which == 0 /* First choice item */) {
                                            if(readyToPurchase)
                                                bp.subscribe(MainActivity.this, SKU_FUNTUBE_WEEKLY);
                                        } else if (which == 1 /* Second choice item */) {
                                            if(readyToPurchase)
                                                bp.subscribe(MainActivity.this, SKU_FUNTUBE_MONTHLY);
                                        }
                                        else if(which==2){
                                            if(readyToPurchase)
                                                bp.subscribe(MainActivity.this, SKU_FUNTUBE_3MONTHS);
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.subscription_prompt_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
                else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened=true;

    }

    // updates UI to reflect model
    public void updateUi() {

        Toasty.success(MainActivity.this, "Updateui called" ,Toasty.LENGTH_LONG).show();
//        ImageView subscribeButton = (ImageView) findViewById(R.id.rasbita_subscribe);
//        if (mSubscribedToDelaroy) {
//            Intent intent = new Intent(this, DashboardActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            // The user does not have rabista subscription"
//            subscribeButton.setImageResource(R.drawable.subscribe);
//        }


    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        //findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
    }

    void complain(String message) {
        Log.e(TAG, "**** Delaroy Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int id) {
        if (id == 0 /* First choice item */) {
            if(readyToPurchase)
                bp.subscribe(MainActivity.this, SKU_FUNTUBE_WEEKLY);
        } else if (id == 1 /* Second choice item */) {
            if(readyToPurchase)
                bp.subscribe(MainActivity.this, SKU_FUNTUBE_MONTHLY);
        }
        else if(id==2){
            if(readyToPurchase)
                bp.purchase(MainActivity.this, "com.funtube.youtube_activation");
        }else if (id == DialogInterface.BUTTON_POSITIVE /* continue button */) {

            String payload = "";


        } else if (id != DialogInterface.BUTTON_NEGATIVE) {
            // There are only four buttons, this should not happen
            Log.e(TAG, "Unknown button clicked in subscription dialog: " + id);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void rateDialog(final boolean close){
        this.rateDialog = new Dialog(this,R.style.Theme_Dialog);

        rateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rateDialog.setCancelable(true);
        rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        final   PrefManager prf= new PrefManager(getApplicationContext());
        rateDialog.setCancelable(false);
        rateDialog.setContentView(R.layout.dialog_rating_app);
        final AppCompatRatingBar AppCompatRatingBar_dialog_rating_app=(AppCompatRatingBar) rateDialog.findViewById(R.id.AppCompatRatingBar_dialog_rating_app);
        final LinearLayout linear_layout_feedback=(LinearLayout) rateDialog.findViewById(R.id.linear_layout_feedback);
        final LinearLayout linear_layout_rate=(LinearLayout) rateDialog.findViewById(R.id.linear_layout_rate);
        final Button buttun_send_feedback=(Button) rateDialog.findViewById(R.id.buttun_send_feedback);
        final Button button_later=(Button) rateDialog.findViewById(R.id.button_later);
        final Button button_never=(Button) rateDialog.findViewById(R.id.button_never);
        final Button button_cancel=(Button) rateDialog.findViewById(R.id.button_cancel);
        button_never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                if (close)
                    finish();
            }
        });
        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (close)
                    finish();
            }
        });
        final EditText edit_text_feed_back=(EditText) rateDialog.findViewById(R.id.edit_text_feed_back);
        buttun_send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prf.setString("NOT_RATE_APP", "TRUE");
                Retrofit retrofit = apiClient.getClient();
                apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport("Application rating feedback",AppCompatRatingBar_dialog_rating_app.getRating()+" star(s) Rating".toString(),edit_text_feed_back.getText().toString());
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful()){
                            Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                        }else{
                            Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                        }
                        rateDialog.dismiss();

                        if (close)
                            finish();

                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                        rateDialog.dismiss();

                        if (close)
                            finish();
                    }
                });
            }
        });
        AppCompatRatingBar_dialog_rating_app.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    if (rating>3){
                        final String appPackageName = getApplication().getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        prf.setString("NOT_RATE_APP", "TRUE");
                        rateDialog.dismiss();
                    }else{
                        linear_layout_feedback.setVisibility(View.VISIBLE);
                        linear_layout_rate.setVisibility(View.GONE);
                    }
                }else{

                }
            }
        });
        rateDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    rateDialog.dismiss();
                    if (close)
                        finish();
                }
                return true;

            }
        });
        rateDialog.show();

    }
}
