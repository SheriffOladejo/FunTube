package com.funtube.ui.Activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.leo.simplearcloader.SimpleArcLoader;
import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.Status;
import com.funtube.ui.fragement.AdsFragment;
import com.funtube.ui.fragement.PlayerFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class PlayerActivity extends AppCompatActivity {



    private PrefManager prefManager;
    private String language;

    Status status;
    public int item = 0;

    private VerticalViewPager view_pager;
    private ViewPagerAdapter adapter;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<Status> statusList = new ArrayList<>();
    private PlayerFragment FirstplayerFragment;
    private SimpleArcLoader simple_arc_loader_exo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        this.prefManager= new PrefManager(getApplicationContext());
        this.language=prefManager.getString("LANGUAGE_DEFAULT");


        Bundle bundle = getIntent().getExtras() ;

        status = new Status();
        status.setId(bundle.getInt("id"));
        status.setTitle(bundle.getString("title"));
        status.setDescription(bundle.getString("description"));
        status.setThumbnail(bundle.getString("thumbnail"));
        status.setUserid(bundle.getInt("userid"));
        status.setUser(bundle.getString("user"));
        status.setUserimage(bundle.getString("userimage"));
        status.setType(bundle.getString("type"));
        status.setOriginal(bundle.getString("original"));
        status.setExtension(bundle.getString("extension"));
        status.setComment(bundle.getBoolean("comment"));
        status.setDownloads(bundle.getInt("downloads"));
        status.setViews(bundle.getInt("views"));
        status.setFont(bundle.getInt("font"));
        status.setTags(bundle.getString("tags"));
        status.setReview(bundle.getBoolean("review"));
        status.setComments( bundle.getInt("comments"));
        status.setCreated(bundle.getString("created"));
        status.setLocal(bundle.getString("local"));

        status.setLike(bundle.getInt("like"));
        status.setLove(bundle.getInt("love"));
        status.setWoow(bundle.getInt("woow"));
        status.setAngry(bundle.getInt("angry"));
        status.setSad(bundle.getInt("sad"));
        status.setHaha(bundle.getInt("haha"));

        status.setKind(bundle.getString("kind"));
        status.setColor( bundle.getString("color"));

        initView();

        item++;


        Bundle bundle1 =new Bundle();
        bundle1.putInt("id",status.getId());
        bundle1.putString("title",status.getTitle());
        bundle1.putString("description",status.getDescription());
        bundle1.putString("thumbnail",status.getThumbnail());
        bundle1.putInt("userid",status.getUserid());
        bundle1.putString("user",status.getUser());
        bundle1.putString("userimage",status.getUserimage());
        bundle1.putString("type",status.getType());
        bundle1.putString("extension",status.getExtension());
        bundle1.putString("original",status.getOriginal());
        bundle1.putBoolean("comment",status.getComment());
        bundle1.putInt("downloads",status.getDownloads());
        bundle1.putInt("views",status.getViews());
        bundle1.putInt("font",status.getFont());
        bundle1.putString("tags",status.getTags());
        bundle1.putBoolean("review",status.getReview());
        bundle1.putInt("comments",status.getComments());
        bundle1.putString("created",status.getCreated());
        bundle1.putString("local",status.getLocal());
        bundle1.putInt("like",status.getLike());
        bundle1.putInt("love",status.getLove());
        bundle1.putInt("woow",status.getWoow());
        bundle1.putInt("angry",status.getAngry());
        bundle1.putInt("sad",status.getSad());
        bundle1.putInt("haha",status.getHaha());
        bundle1.putString("kind",status.getKind());
        bundle1.putString("color",status.getColor());
        bundle1.putBoolean("first",true);

        FirstplayerFragment =  new PlayerFragment();
        FirstplayerFragment.setArguments(bundle1);
        adapter.addFragment(FirstplayerFragment);
        adapter.notifyDataSetChanged();
        loadMore(true);
        showAdsBanner();
    }
    public void initView(){
        simple_arc_loader_exo = (SimpleArcLoader) findViewById(R.id.simple_arc_loader_exo);
        view_pager = (VerticalViewPager) findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(1000);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
        view_pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= mFragmentList.size()-3){
                    loadMore(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void addVideo(Status status1,Boolean first){



            statusList.add(status1);
            item++;
            Bundle bundle =new Bundle();
            bundle.putInt("id",status1.getId());
            bundle.putString("title",status1.getTitle());
            bundle.putString("description",status1.getDescription());
            bundle.putString("thumbnail",status1.getThumbnail());
            bundle.putInt("userid",status1.getUserid());
            bundle.putString("user",status1.getUser());
            bundle.putString("userimage",status1.getUserimage());
            bundle.putString("type",status1.getType());
            bundle.putString("extension",status1.getExtension());
            bundle.putString("original",status1.getOriginal());
            bundle.putBoolean("comment",status1.getComment());
            bundle.putInt("downloads",status1.getDownloads());
            bundle.putInt("views",status1.getViews());
            bundle.putInt("font",status1.getFont());
            bundle.putString("tags",status1.getTags());
            bundle.putBoolean("review",status1.getReview());
            bundle.putInt("comments",status1.getComments());
            bundle.putString("created",status1.getCreated());
            bundle.putString("local",status1.getLocal());
            bundle.putInt("like",status1.getLike());
            bundle.putInt("love",status1.getLove());
            bundle.putInt("woow",status1.getWoow());
            bundle.putInt("angry",status1.getAngry());
            bundle.putInt("sad",status1.getSad());
            bundle.putInt("haha",status1.getHaha());
            bundle.putString("kind",status1.getKind());
            bundle.putString("color",status1.getColor());
            bundle.putBoolean("first",first);
            PlayerFragment playerFragment =  new PlayerFragment();
            playerFragment.setArguments(bundle);
            adapter.addFragment(playerFragment);

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
    private void loadMore(boolean first) {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Status>> call = service.FullScreenByRandom(language);
        call.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            if (response.body().get(i).getId() != status.getId()){
                                boolean exit =  false;
                                for (int j = 0; j < statusList.size(); j++) {
                                    if (response.body().get(i).getId().equals(statusList.get(j).getId())){
                                        exit =  true;
                                    }
                                }
                                if (!exit){
                                    addVideo(response.body().get(i), false);
                                    if (item % 5 == 0){
                                        if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
                                            addFacebookAds();
                                        }
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (first) {
                            FirstplayerFragment.run();
                        }
                        simple_arc_loader_exo.setVisibility(View.GONE);
                    }

                }
            }
            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                adapter.notifyDataSetChanged();
                FirstplayerFragment.run();
                simple_arc_loader_exo.setVisibility(View.GONE);
            }
        });
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
    private void addFacebookAds() {
        AdsFragment facebookAdsFragment =  new AdsFragment();
        adapter.addFragment(facebookAdsFragment);
    }
    @Override
    public void onBackPressed() {
        if (adapter.getItem(view_pager.getCurrentItem()) instanceof  PlayerFragment){
            PlayerFragment f =(PlayerFragment) adapter.getItem(view_pager.getCurrentItem());
            f.onBackPressed();
        }
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        }
    }
}
