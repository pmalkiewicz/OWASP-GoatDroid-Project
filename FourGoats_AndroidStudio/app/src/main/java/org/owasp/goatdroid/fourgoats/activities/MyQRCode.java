package org.owasp.goatdroid.fourgoats.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.owasp.goatdroid.fourgoats.R;
import org.owasp.goatdroid.fourgoats.base.BaseActivity;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class MyQRCode extends BaseActivity {

    private static final int WIDTH = 512;
    String userName;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_qr_code);
        userName = getIntent().getExtras().getString("userName");
        imageView = (ImageView) findViewById(R.id.QRImageView);
        try {
            Bitmap bitmap = generateQRCode(userName);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap generateQRCode(String str) throws WriterException {
        BitMatrix result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

}
