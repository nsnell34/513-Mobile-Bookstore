package com.example.nicksnell.new457;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.nicksnell.new457.database.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends Activity {
    private static final String TAG = "UserActivity";

    private String name;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        db = dbHelper.getReadableDatabase();

        final TextView tv = findViewById(R.id.textView);
        tv.setText(name + "'s Account");

        loadAccountInfo();
    }

    private void loadAccountInfo() {
        String queryCustomer = "SELECT ID, total_spent FROM customer WHERE name = ?";
        Cursor customerCursor = db.rawQuery(queryCustomer, new String[]{name});

        if (customerCursor.moveToFirst()) {
            int customerId = customerCursor.getInt(customerCursor.getColumnIndexOrThrow("ID"));
            double totalSpent = customerCursor.getDouble(customerCursor.getColumnIndexOrThrow("total_spent"));

            List<String> bookTitles = getPurchasedBooks(customerId);
            populateBookList(bookTitles);

            displayTotalSpent(String.format("%.2f", totalSpent));
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
        customerCursor.close();
    }

    private List<String> getPurchasedBooks(int customerId) {
        List<String> bookTitles = new ArrayList<>();
        String queryBooks = "SELECT title FROM purchases WHERE custID = ?";
        Cursor bookCursor = db.rawQuery(queryBooks, new String[]{String.valueOf(customerId)});

        while (bookCursor.moveToNext()) {
            String title = bookCursor.getString(bookCursor.getColumnIndexOrThrow("title"));
            bookTitles.add(title);
        }
        bookCursor.close();

        return bookTitles;
    }

    private void populateBookList(List<String> bookTitles) {
        TextView bookListTextView = findViewById(R.id.bookListTextView);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (final String title : bookTitles) {
            makeTextViewHyperlink(ssb, title, bookListTextView);
        }
        bookListTextView.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    private void makeTextViewHyperlink(SpannableStringBuilder ssb, final String title, final TextView textView) {
        int start = ssb.length();
        ssb.append(title);
        int end = ssb.length();
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(UserActivity.this, BookActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append("\n");
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void displayTotalSpent(String totalSpentString) {
        TextView totalSpentTextView = findViewById(R.id.textView4);
        totalSpentTextView.setText("Total Spent: " + totalSpentString);
    }

    public void onBackButton(View view) {
        Intent intent = new Intent(UserActivity.this, HomeActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
