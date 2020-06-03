package com.funtube.ui.Activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funtube.Adapters.Transactiondapter;
import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.ApiResponse;
import com.funtube.model.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EarningActivity extends AppCompatActivity {

    private RecyclerView recycler_view_transaction_earning_activity;
    private List<Transaction> transactionList = new ArrayList<>();
    private Transactiondapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView text_view_earning_date_activity;
    private TextView text_view_earning_amount_earning_activity;
    private TextView text_view_earning_points_earning_activity;
    private TextView text_view_earning_usd_to_points_activity;
    private RelativeLayout relative_layout_history_payout_earning_actiivty;
    private RelativeLayout relative_layout_request_payout_earning_actiivty;
    private TextView text_view_code_earning_actiivty;
    private RelativeLayout relative_layout_copy_code_earning_actiivty;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private RelativeLayout relative_layout_load_more;
    private SwipeRefreshLayout swipe_refreshl_earning_activity;
    private Integer page= 0;
    private Integer item= 0;
    private Button button_load_more;
    private ProgressDialog register_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning);
        initView();
        initAction();
        LoadTransactions();
        getData();
    }

    private void initAction() {
        this.relative_layout_history_payout_earning_actiivty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(EarningActivity.this,PayoutsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.relative_layout_request_payout_earning_actiivty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EarningActivity.this,RequestActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.relative_layout_copy_code_earning_actiivty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code", text_view_code_earning_actiivty.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                Toasty.success(getApplicationContext(),getResources().getString(R.string.reference_code_copied)).show();
            }
        });
        swipe_refreshl_earning_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                transactionList.clear();
                page = 0;
                item = 0;
                loading=true;
                LoadTransactions();
                getData();
            }
        });
    }

    public void initView(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.my_earning));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.swipe_refreshl_earning_activity =  (SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_earning_activity);
        this.relative_layout_load_more =  (RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.recycler_view_transaction_earning_activity =  (RecyclerView) findViewById(R.id.recycler_view_transaction_earning_activity);

        this.text_view_code_earning_actiivty =  (TextView) findViewById(R.id.text_view_code_earning_actiivty);
        this.text_view_earning_date_activity =  (TextView) findViewById(R.id.text_view_earning_date_activity);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        this.text_view_earning_date_activity.setText(dateFormat.format(date));
        this.text_view_earning_amount_earning_activity =  (TextView) findViewById(R.id.text_view_earning_amount_earning_activity);
        this.text_view_earning_points_earning_activity =  (TextView) findViewById(R.id.text_view_earning_points_earning_activity);
        this.text_view_earning_usd_to_points_activity =  (TextView) findViewById(R.id.text_view_earning_usd_to_points_activity);

        this.relative_layout_history_payout_earning_actiivty =  (RelativeLayout) findViewById(R.id.relative_layout_history_payout_earning_actiivty);
        this.relative_layout_request_payout_earning_actiivty =  (RelativeLayout) findViewById(R.id.relative_layout_request_payout_earning_actiivty);
        this.relative_layout_copy_code_earning_actiivty =  (RelativeLayout) findViewById(R.id.relative_layout_copy_code_earning_actiivty);
        this.button_load_more =  (Button) findViewById(R.id.button_load_more);


        this.adapter =  new Transactiondapter(transactionList,getApplicationContext());
        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view_transaction_earning_activity.setHasFixedSize(true);
        recycler_view_transaction_earning_activity.setAdapter(adapter);
        recycler_view_transaction_earning_activity.setLayoutManager(linearLayoutManager);


    }

    public void LoadTransactions(){
        swipe_refreshl_earning_activity.setRefreshing(true);
        final PrefManager prefManager = new PrefManager(this);
        Integer id_user = 0;
        String key_user= "";
        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
            id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
            key_user=  prefManager.getString("TOKEN_USER");
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Transaction>> call = service.userTransaction(id_user,key_user);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                swipe_refreshl_earning_activity.setRefreshing(false);
                apiClient.FormatData(EarningActivity.this,response);

                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        transactionList.clear();
                        for (int i=0;i<response.body().size();i++){
                            transactionList.add(response.body().get(i));
                        }
                        adapter.notifyDataSetChanged();
                        recycler_view_transaction_earning_activity.setNestedScrollingEnabled(false);
                        page++;
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
            }
        });
    }
    private void getData() {
        PrefManager prefManager= new PrefManager(getApplicationContext());
        Integer follower= -1;
        Integer id_user = 0;
        String key_user= "";
        if (prefManager.getString("LOGGED").toString().equals("TRUE")) {
            id_user=  Integer.parseInt(prefManager.getString("ID_USER"));
            key_user=  prefManager.getString("TOKEN_USER");
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.userEarning(id_user,key_user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiClient.FormatData(EarningActivity.this,response);
                if (response.isSuccessful()){

                    for (int i=0;i<response.body().getValues().size();i++){
                        if (response.body().getValues().get(i).getName().equals("earning")){
                            text_view_earning_amount_earning_activity.setText(response.body().getValues().get(i).getValue());
                        }
                        if (response.body().getValues().get(i).getName().equals("points")){
                            text_view_earning_points_earning_activity.setText(response.body().getValues().get(i).getValue());
                        }
                        if (response.body().getValues().get(i).getName().equals("equals")){
                            text_view_earning_usd_to_points_activity.setText(response.body().getValues().get(i).getValue());
                        }
                        if (response.body().getValues().get(i).getName().equals("code")){
                            text_view_code_earning_actiivty.setText(response.body().getValues().get(i).getValue());
                        }
                    }

                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        LoadTransactions();
    }
}
