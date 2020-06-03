package com.funtube.ui.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.funtube.Provider.PrefManager;
import com.funtube.R;
import com.funtube.api.apiClient;
import com.funtube.api.apiRest;
import com.funtube.model.ApiResponse;

import java.net.URL;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ActivityRequestAccountVerification extends AppCompatActivity {

    ProgressBar progressBar;
    TextView no_data_textview;
    LinearLayout ll;
    EditText urlEdit;
    ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_account_verification);

        progressBar = findViewById(R.id.progress_bar);
        no_data_textview = findViewById(R.id.no_data_textview);
        urlEdit = findViewById(R.id.url);
        ll = findViewById(R.id.ll);
        no_data_textview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Processing...");
        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(urlEdit.getText().toString().equals("")){

                }
                else{
                    apply();
                }
            }
        });
    }

    private void apply(){
        dialog.show();
        PrefManager prf= new PrefManager(getApplicationContext());
        String username = prf.getString("NAME_USER");
        String url = urlEdit.getText().toString().trim();
        try{
            new URL(url);
            String message = "Account Verification Request.\nPhoto URL: " + url;
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
                Call<ApiResponse> call = service.addSupport(username,message,username);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if(response.isSuccessful()){
                        dialog.dismiss();
                        Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        dialog.dismiss();
                        Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    dialog.dismiss();
                    Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            dialog.dismiss();
            Toasty.error(getApplicationContext(), "Malformed URL", Toast.LENGTH_SHORT).show();
        }

    }
}
