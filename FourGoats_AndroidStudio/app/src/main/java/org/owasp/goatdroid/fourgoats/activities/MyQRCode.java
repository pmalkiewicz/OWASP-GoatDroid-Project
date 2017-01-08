package org.owasp.goatdroid.fourgoats.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;
import org.owasp.goatdroid.fourgoats.misc.Utils;

public class MyQRCode extends BaseActivity {

    private static final int WIDTH = 512;
    String userName;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_qr_code);
        userName = getIntent().getExtras().getString("userName");
        imageView = (ImageView) findViewById(R.id.myQRCodeImageView);
        try {
            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme("fourgoats")
                    .authority("viewprofile")
                    .encodedQuery("username=" + userName);

            Bitmap bitmap = Utils.generateQRCode(uriBuilder.build().toString(), WIDTH);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
