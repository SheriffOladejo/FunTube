package com.funtube.ui.Activities;

import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.peekandpop.shalskar.peekandpop.PeekAndPop;
import com.funtube.Adapters.PortraitVideoAdapter;
import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.Category;
import com.funtube.model.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AllFullScreenSearchActivity extends AppCompatActivity {

    private String query;

    private Integer page = 0;
    private String language = "0";
    private String order = "created";
    private Boolean loaded=false;
    private Integer type_ads = 0;

    private SwipeRefreshLayout swipe_refreshl_image_category;
    private RecyclerView recycler_view_image_category;
    private List<Status> statusList =new ArrayList<>();
    private List<Category> categoryList =new ArrayList<>();
    private PortraitVideoAdapter portraitVideoAdapter;
    private GridLayoutManager gridLayoutManager;
    private PrefManager prefManager;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private ImageView imageView_empty_category;
    private PeekAndPop peekAndPop;
    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private Boolean native_ads_enabled = false ;
    private String from = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras() ;
        this.prefManager= new PrefManager(getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");

        this.query =  bundle.getString("query");
        setContentView(R.layout.activity_all_full_screen_search);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(query+" Fullscreen videos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        loadStatus();
        showAdsBanner();
        initAction();

    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
        return true;
    }

    public void showAdsBanner() {
        if (!checkSUBSCRIBED()) {
            PrefManager prefManager= new PrefManager(getApplicationContext());
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("FACEBOOK")){
                showFbBanner();
            }
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("ADMOB")){
                showAdmobBanner();
            }
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("BOTH")) {
                if (prefManager.getString("Banner_Ads_display").equals("FACEBOOK")) {
                    prefManager.setString("Banner_Ads_display", "ADMOB");
                    showAdmobBanner();
                } else {
                    prefManager.setString("Banner_Ads_display", "FACEBOOK");
                    showFbBanner();
                }
            }
        }

    }
    public void showAdmobBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.adView);
        final AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        linear_layout_ads.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }
    public void showFbBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.adView);
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, prefManager.getString("ADMIN_BANNER_FACEBOOK_ID"), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
        linear_layout_ads.addView(adView);
        adView.loadAd();

    }
    @Override
    public void onBackPressed(){
        if (from!=null){
            Intent intent = new Intent(AllFullScreenSearchActivity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
            finish();
        }else{
            super.onBackPressed();
            overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        }
        return;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (from!=null){
                    Intent intent = new Intent(AllFullScreenSearchActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                    finish();
                }else{
                    super.onBackPressed();
                    overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                }
                return true;
            case R.id.nav_popular:
                order =  "downloads";
                statusList.clear();
                portraitVideoAdapter.notifyDataSetChanged();
                page = 0;
                item = 0;
                loading=true;
                loadStatus();
                return true;
            case R.id.nav_latest:
                order =  "created";
                statusList.clear();
                portraitVideoAdapter.notifyDataSetChanged();
                page = 0;
                item = 0;
                loading=true;
                loadStatus();
                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }

    public void initView(){

        PrefManager prefManager= new PrefManager(getApplicationContext());

        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
        }
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }

        this.imageView_empty_category=(ImageView) findViewById(R.id.imageView_empty_category);
        this.relative_layout_load_more=(RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.button_try_again =(Button) findViewById(R.id.button_try_again);
        this.swipe_refreshl_image_category=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_image_category);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.recycler_view_image_category=(RecyclerView) findViewById(R.id.recycler_view_image_category);
        this.gridLayoutManager=  new GridLayoutManager(this,2);
        if (native_ads_enabled){
            this.gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((position +1 ) % (lines_beetween_ads + 1 ) == 0) ? 2 : 1;
                }
            });
        }else{
            this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),2,RecyclerView.VERTICAL,false);
        }
        this.peekAndPop = new PeekAndPop.Builder(this)
                .parentViewGroupToDisallowTouchEvents(recycler_view_image_category)
                .peekLayout(R.layout.dialog_view)
                .build();
        portraitVideoAdapter =new PortraitVideoAdapter(statusList,this);
        recycler_view_image_category.setHasFixedSize(true);
        recycler_view_image_category.setAdapter(portraitVideoAdapter);
        recycler_view_image_category.setLayoutManager(gridLayoutManager);
        recycler_view_image_category.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadNextStatus();
                        }
                    }
                }else{

                }
            }
        });

    }
    public void initAction(){
        swipe_refreshl_image_category.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                statusList.clear();
                portraitVideoAdapter.notifyDataSetChanged();
                page = 0;
                item = 0;
                loading=true;
                loadStatus();
            }
        });
    }
    public void loadNextStatus(){
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Status>> call = service.searchFullScreen(order,language,page,query);
        call.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                apiClient.FormatData(AllFullScreenSearchActivity.this,response);

                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            statusList.add(response.body().get(i).setViewType(2));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        statusList.add(new Status().setViewType(11));
                                    }else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")){
                                        statusList.add(new Status().setViewType(6));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")){
                                        if (type_ads == 0) {
                                            statusList.add(new Status().setViewType(11));
                                            type_ads = 1;
                                        }else if (type_ads == 1){
                                            statusList.add(new Status().setViewType(6));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }
                        }
                        portraitVideoAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;

                    }else {

                    }
                    relative_layout_load_more.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                relative_layout_load_more.setVisibility(View.GONE);
            }
        });
    }
    public void loadStatus(){
        swipe_refreshl_image_category.setRefreshing(true);
        imageView_empty_category.setVisibility(View.GONE);
        recycler_view_image_category.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Status>> call = service.searchFullScreen(order,language,page,query);
        call.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                swipe_refreshl_image_category.setRefreshing(false);
                apiClient.FormatData(AllFullScreenSearchActivity.this,response);

                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        statusList.clear();
                        for (int i=0;i<response.body().size();i++){
                            statusList.add(response.body().get(i).setViewType(2));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        statusList.add(new Status().setViewType(11));
                                    }else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")){
                                        statusList.add(new Status().setViewType(6));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")){
                                        if (type_ads == 0) {
                                            statusList.add(new Status().setViewType(11));
                                            type_ads = 1;
                                        }else if (type_ads == 1){
                                            statusList.add(new Status().setViewType(6));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }
                        }
                        portraitVideoAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;
                        imageView_empty_category.setVisibility(View.GONE);
                        recycler_view_image_category.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        imageView_empty_category.setVisibility(View.VISIBLE);
                        recycler_view_image_category.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }


                }else{
                    imageView_empty_category.setVisibility(View.GONE);
                    recycler_view_image_category.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                imageView_empty_category.setVisibility(View.GONE);
                recycler_view_image_category.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
            }
        });
    }
}
