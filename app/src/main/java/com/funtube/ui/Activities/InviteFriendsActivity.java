package com.funtube.ui.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InviteFriendsActivity extends AppCompatActivity {

    private String user_code;
    private TextView textView;
    private TextView textView_noData;
    private LinearLayout linearLayout_copy;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reference_code_activity);

        linearLayout_copy = findViewById(R.id.linearLayout_copy_reference_code);
        relativeLayout = findViewById(R.id.relativeLayout_reference_code_fragment);
        textView_noData = findViewById(R.id.textView_noDataFound_reference_code);
        progressBar = findViewById(R.id.progressbar_reference_code);
        relativeLayout.setVisibility(View.GONE);
        textView = findViewById(R.id.textView_reference_code);
        progressBar.setVisibility(View.VISIBLE);
        textView_noData.setText("No Data");
        textView_noData.setVisibility(View.GONE);

        linearLayout_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", ref);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(InviteFriendsActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });
        getData();
    }

    String ref = "";
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
                apiClient.FormatData(InviteFriendsActivity.this,response);
                if (response.isSuccessful()){
                    for (int i=0;i<response.body().getValues().size();i++){
                        if (response.body().getValues().get(i).getName().equals("code")){
                            textView.setText(response.body().getValues().get(i).getValue());
                            ref = response.body().getValues().get(i).getValue();
                            progressBar.setVisibility(View.GONE);
                            textView_noData.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                textView_noData.setText("No Data");
            }
        });
    }
}
