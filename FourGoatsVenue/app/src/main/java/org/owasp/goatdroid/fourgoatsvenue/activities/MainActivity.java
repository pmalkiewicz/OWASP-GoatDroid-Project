package org.owasp.goatdroid.fourgoatsvenue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.owasp.goatdroid.fourgoatsvenue.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void scanCoupon(View view) {
        Intent intent = new Intent(MainActivity.this, ScanCouponActivity.class);
        startActivity(intent);
    }
}
