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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.nicksnell.new457.database.MySQLiteHelper;

public class ListActivity extends Activity {
    private static final String TAG = "ListActivity";

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        db = dbHelper.getReadableDatabase();

        loadUserList();
    }

    private void loadUserList() {
        String queryUsers = "SELECT name FROM customer";
        Cursor userCursor = db.rawQuery(queryUsers, null);

        if (userCursor.getCount() > 0) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            TextView userListTextView = findViewById(R.id.userListTextView);

            while (userCursor.moveToNext()) {
                String userName = userCursor.getString(userCursor.getColumnIndexOrThrow("name"));
                makeTextViewHyperlink(ssb, userName, userListTextView);
            }

            userListTextView.setText(ssb, TextView.BufferType.SPANNABLE);
            userListTextView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
        }

        userCursor.close();
    }

    private void makeTextViewHyperlink(SpannableStringBuilder ssb, final String userName, final TextView textView) {
        int start = ssb.length();
        ssb.append(userName);
        int end = ssb.length();

        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(ListActivity.this, UserActivity.class);
                intent.putExtra("name", userName);
                startActivity(intent);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.append("\n");
    }

    public void onBackButton(View view) {
        finish();
    }
}
