package org.owasp.goatdroid.fourgoatsvenue.activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.goatdroid.fourgoatsvenue.R;
import org.owasp.goatdroid.fourgoatsvenue.misc.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ScanCouponActivity extends AppCompatActivity {

    TextView mUsernameTextView;
    TextView mRewardnameTextView;
    TextView mTimestampTextView;
    TextView mStatusTextView;
    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_coupon);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

        mUsernameTextView = (TextView) findViewById(R.id.username_text_view);
        mRewardnameTextView = (TextView) findViewById(R.id.rewardname_text_view);
        mTimestampTextView = (TextView) findViewById(R.id.timestamp_text_view);
        mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        mLayout = findViewById(R.id.activity_scan_coupon);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            try {
                JSONObject json = new JSONObject(scanResult.getContents());

                String userName = json.getString("username");
                String rewardName = json.getString("rewardname");

                //time since epoch in s
                Long timestamp = json.getLong("timestamp") * 1000L;

                Date date = new Date(timestamp);

                DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Etc/UTC+1"));
                String formatted = format.format(date);

                mUsernameTextView.setText("Username: " + userName);
                mRewardnameTextView.setText("Reward name: " + rewardName);
                mTimestampTextView.setText("Timestamp: " + formatted);

                if (verifyTimestamp(timestamp)) {
                    mLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGreen));
                    mStatusTextView.setText("Status: valid");
                } else {
                    mLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkOrange));
                    mStatusTextView.setText("Status: overdue");
                }

            } catch (JSONException ex) {
                mLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkRed));
                mStatusTextView.setText("Status: invalid");
            }
        }
    }

    private boolean verifyTimestamp(Long timestamp) {
        long currentTimestamp = System.currentTimeMillis();
        long diff = currentTimestamp - timestamp;
        long diffSeconds = diff/1000;
        return diffSeconds <= Constants.OVERDUE_TIME_SECONDS;
    }


}
