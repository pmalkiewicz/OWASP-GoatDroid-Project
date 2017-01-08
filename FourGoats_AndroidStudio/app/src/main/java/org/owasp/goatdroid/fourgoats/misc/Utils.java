/**
 * OWASP GoatDroid Project
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * GoatDroid project. For details, please see
 * https://www.owasp.org/index.php/Projects/OWASP_GoatDroid_Project
 *
 * Copyright (c) 2012 - The OWASP Foundation
 * 
 * GoatDroid is published by OWASP under the GPLv3 license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jack Mannino (Jack.Mannino@owasp.org https://www.owasp.org/index.php/User:Jack_Mannino)
 * @author Walter Tighzert
 * @created 2012
 */
package org.owasp.goatdroid.fourgoats.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class Utils {

	static public void makeToast(Context context, String message, int duration) {

		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	static public String getCurrentDateTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new java.text.SimpleDateFormat(
				Constants.DATE_FORMAT);
		return df.format(cal.getTime());
	}

	static public String getDestinationInfo(Context context) {

		String destinationInfo = "";
		SharedPreferences prefs = context.getSharedPreferences(
				"destination_info", Context.MODE_WORLD_READABLE);
		destinationInfo += prefs.getString("host", "");
		destinationInfo += ":";
		destinationInfo += prefs.getString("port", "");
		return destinationInfo;
	}

	static public HashMap<String, String> getDestinationInfoMap(Context context) {

		HashMap<String, String> map = new HashMap<String, String>();
		SharedPreferences prefs = context.getSharedPreferences(
				"destination_info", Context.MODE_WORLD_READABLE);
		map.put("host", prefs.getString("host", ""));
		map.put("port", prefs.getString("port", ""));
		return map;

	}

	static public void writeDestinationInfo(Context context, String host,
			String port) {

		SharedPreferences destinationInfo = context.getSharedPreferences(
				"destination_info", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = destinationInfo.edit();
		editor.putString("host", host);
		editor.putString("port", port);
		editor.commit();
	}

	static public HashMap<String, String> getProxyMap(Context context) {

		HashMap<String, String> map = new HashMap<String, String>();
		SharedPreferences prefs = context.getSharedPreferences("proxy_info",
				Context.MODE_WORLD_READABLE);
		map.put("proxyHost", prefs.getString("proxyHost", ""));
		map.put("proxyPort", prefs.getString("proxyPort", ""));
		return map;

	}

	static public Bitmap generateQRCode(String str, int size) throws WriterException {
		BitMatrix result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, size, size, null);

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
		bitmap.setPixels(pixels, 0, size, 0, 0, w, h);
		return bitmap;
	}
}
