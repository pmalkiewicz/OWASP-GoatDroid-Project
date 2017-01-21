/**
 * OWASP GoatDroid Project
 * <p>
 * This file is part of the Open Web Application Security Project (OWASP)
 * GoatDroid project. For details, please see
 * https://www.owasp.org/index.php/Projects/OWASP_GoatDroid_Project
 * <p>
 * Copyright (c) 2012 - The OWASP Foundation
 * <p>
 * GoatDroid is published by OWASP under the GPLv3 license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 *
 * @author Jack Mannino (Jack.Mannino@owasp.org https://www.owasp.org/index.php/User:Jack_Mannino)
 * @author Walter Tighzert
 * @created 2012
 */
package org.owasp.goatdroid.fourgoats.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.activities.GetReward;
import org.owasp.goatdroid.fourgoats.activities.Login;
import org.owasp.goatdroid.fourgoats.db.UserInfoDBHelper;
import org.owasp.goatdroid.fourgoats.rest.rewards.RewardsRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRewards extends Fragment {

    Context context;
    ListView listView;
    TextView noRewardsTextView;
    ArrayList<HashMap<String, String>> mRewardData;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_rewards, container, false);
        listView = (ListView) v.findViewById(R.id.myRewardsListView);
        noRewardsTextView = (TextView) v.findViewById(R.id.noRewardsTextView);
        GetMyRewards myRewards = new GetMyRewards();
        myRewards.execute(null, null);
        return v;
    }

    public String[] bindListView(ArrayList<HashMap<String, String>> rewardData) {

        ArrayList<String> rewardArray = new ArrayList<String>();

        for (HashMap<String, String> reward : rewardData) {
            if (reward.get("rewardName") != null
                    && reward.get("rewardDescription") != null
                    && reward.get("timeEarned") != null) {
                rewardArray.add(reward.get("rewardName") + "\n"
                        + reward.get("rewardDescription") + "\nEarned On: "
                        + reward.get("timeEarned"));
            }
        }
        String[] rewards = new String[rewardArray.size()];
        rewards = rewardArray.toArray(rewards);
        return rewards;
    }

    private class GetMyRewards extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... params) {

            ArrayList<HashMap<String, String>> rewardData;
            UserInfoDBHelper uidh = new UserInfoDBHelper(context);
            String sessionToken = uidh.getSessionToken();
            uidh.close();
            RewardsRequest rest = new RewardsRequest(context);
            try {
                if (sessionToken.equals("")) {
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    return new String[0];

                } else {
                    rewardData = rest.getMyRewards(sessionToken);
                    if (rewardData.size() > 1) {
                        mRewardData = rewardData;
                        return bindListView(rewardData);
                    } else {
                        return new String[0];
                    }
                }
            } catch (Exception e) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                return new String[0];
            }
        }

        public void onPostExecute(String[] rewards) {

            if (getActivity() != null) {
                if (rewards.length > 0) {
                    noRewardsTextView.setVisibility(View.GONE);
                    listView.setAdapter(new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, rewards));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            HashMap<String, String> reward = mRewardData.get(i+1);
                                            Intent intent = new Intent(context, GetReward.class);
                                            intent.putExtra("rewardName", reward.get("rewardName"));
                                            intent.putExtra("timeEarned", reward.get("timeEarned"));
                                            startActivity(intent);
                                        }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Do you want to redeem the coupon?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    });
                } else
                    noRewardsTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}