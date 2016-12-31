package org.owasp.goatdroid.fourgoats.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;


public class ScanQRCode extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_code);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            try {
                Uri uri = Uri.parse(scanResult.getContents());
                if (!uri.getScheme().equals("fourgoats"))
                    return;

                if (uri.getHost().equals("viewprofile")) {
                    String username = uri.getQueryParameter("username");
                    Intent profileIntent = new Intent(this,
                            ViewProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userName", username);
                    profileIntent.putExtras(bundle);
                    startActivity(profileIntent);
                }
            } catch (Exception ex) {
                
            }
        }
    }
}
