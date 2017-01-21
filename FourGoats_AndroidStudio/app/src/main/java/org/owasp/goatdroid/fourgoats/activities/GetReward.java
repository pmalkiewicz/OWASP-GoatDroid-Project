package org.owasp.goatdroid.fourgoats.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.misc.Utils;
import org.owasp.goatdroid.fourgoats.rest.rewards.RewardsRequest;

import java.util.concurrent.TimeUnit;


public class GetReward extends BaseActivity {

    String mRewardname;
    ImageView qrImageView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_reward);
        context = getApplicationContext();

        mRewardname = getIntent().getStringExtra("rewardName");

        new RedeemReward().execute();
    }

    private void showCoupon() {
        UserInfoDBHelper profileUIDH = new UserInfoDBHelper(this);
        String userName = profileUIDH.getUserName();
        profileUIDH.close();

        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();

        qrImageView = (ImageView) findViewById(R.id.getRewardImageView);
        final TextView timeLeftTextView = (TextView) findViewById(R.id.timeLeftTextView);

        try {
            JSONObject json = new JSONObject();
            json.put("username", userName);
            json.put("rewardname", mRewardname);
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

    private class RedeemReward extends
            AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... params) {

            RewardsRequest rest = new RewardsRequest(context);
            boolean result = false;
            UserInfoDBHelper uidh = new UserInfoDBHelper(context);
            try {
                int j = 0;
                String sessionToken = uidh.getSessionToken();
                result = rest.redeemReward(sessionToken, mRewardname);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                uidh.close();
            }
            return result;
        }

        public void onPostExecute(Boolean result) {
            if (result)
                showCoupon();
            else {
                Toast.makeText(context, "Reward not available!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
