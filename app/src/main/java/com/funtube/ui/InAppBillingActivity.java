package com.funtube.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.funtube.R;

public class InAppBillingActivity extends AppCompatActivity {

    static final String ITEM_SKU="com.funtube";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_app_billing_layout);

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhAb6Dz3l8s65xNK6j8i+EcSJ0lL+JTX/RJyOnpW1PWI4Jfujjvk2k2379i2V7GUNLWEcA53Qrk7oZ5Qn/ybhDqIqwp6qHAAw2Qz93KSdOtKc41lDtcBhJYN2ab+eL2ppytkAl+XIwnoSW36/NssAm4Iq7enjSCZpBoQqTtw0lYJ9qhdOLRicBzOlFIDr35ZbZzsaLtsBul2UD607puyH724y62NqDTvFMOOfdkB3ofxPXbUedcNvbE7sNd3L+7R8oB1lnzjkA+aCR3pvS2dVHdBQDg2TNnt1NNI9uOBu+4kw5wFlBmmEZp2jH+ZyvLVDEr9llZ0NyU0pcrzEpNPKKQIDAQAB";

    }


}
