package com.example.nicksnell.new457;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class PurchaseBook extends AsyncTask<Void, Void, String> {
    private static final String TAG = "PurchaseBook";

    private List<String> selectedBooks;
    private String name;
    private String password;

    public PurchaseBook(String name, String password, List<String> selectedBooks) {
        this.name = name;
        this.password = password;
        this.selectedBooks = selectedBooks;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            StringBuilder bookData = new StringBuilder();
            for (String bookTitle : selectedBooks) {
                bookData.append(URLEncoder.encode(bookTitle, "UTF-8")).append(",");
            }
            // Remove the last comma
            if (bookData.length() > 0) {
                bookData.setLength(bookData.length() - 1);
            }

            String link = "http://undcemcs02.und.edu/~nicholas.s.snell/457/Bookstore/PurchaseBook.php";
            link += "?name=" + URLEncoder.encode(name, "UTF-8");
            link += "&password=" + URLEncoder.encode(password, "UTF-8");
            link += "&books=" + URLEncoder.encode(bookData.toString(), "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Log.d(TAG, sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Server Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null && result.equals("1")) {
            Log.d(TAG, "purchase successful");

        } else {
            Log.d(TAG, "purchase failed");

        }
    }
}
