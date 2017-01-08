package org.owasp.goatdroid.fourgoats.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.misc.Utils;

import java.util.concurrent.TimeUnit;


public class GetReward extends BaseActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_reward);

        String rewardName = getIntent().getStringExtra("rewardName");
        String timeEarned = getIntent().getStringExtra("timeEarned");
        UserInfoDBHelper profileUIDH = new UserInfoDBHelper(this);
        String userName = profileUIDH.getUserName();
        profileUIDH.close();
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();

        final ImageView qrImageView = (ImageView) findViewById(R.id.getRewardImageView);
        final TextView timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);

        try {
            JSONObject json = new JSONObject();
            json.put("username", userName);
            json.put("rewardName", rewardName);
            json.put("timestamp", timestamp);
            String payload = json.toString();
            qrImageView.setImageBitmap(Utils.generateQRCode(payload, 512));
        } catch (Exception e) {
            e.printStackTrace();
        }

        new CountDownTimer(90000, 1000) {
            public void onTick(long millisUntilFinished) {
                String text = "Time left: " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                );
                timeLeftTextView.setText(text);
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }
}
