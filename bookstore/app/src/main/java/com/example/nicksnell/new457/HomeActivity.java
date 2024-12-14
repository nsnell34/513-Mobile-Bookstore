package com.example.nicksnell.new457;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.widget.Toast;

import com.example.nicksnell.new457.database.MySQLiteHelper;


public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";

    public String name;
    private MySQLiteHelper dbHelper;
    public int priceRange = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new MySQLiteHelper(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        RadioGroup priceRangeGroup = findViewById(R.id.priceRangeGroup);

        priceRangeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.range_0_10) {
                    priceRange = 1;
                } else if (checkedId == R.id.range_10_20) {
                    priceRange = 2;
                } else if (checkedId == R.id.range_20_30) {
                    priceRange = 3;
                } else if (checkedId == R.id.range_30_up) {
                    priceRange = 4;
                } else {
                    priceRange = 0;
                }
            }
        });

        TextView tv = findViewById(R.id.txtMsg);
        makeTextViewHyperlink(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("priceRange", priceRange);
                startActivity(intent);
            }
        });
    }

    public void ClearSelections(View view) {
        RadioGroup priceRangeGroup = findViewById(R.id.priceRangeGroup);
        priceRangeGroup.clearCheck();
        priceRange = 0;
    }

    public static void makeTextViewHyperlink( TextView tv ) {
        SpannableStringBuilder ssb = new SpannableStringBuilder( );
        ssb.append( tv.getText( ) );
        ssb.setSpan( new URLSpan("#"), 0, ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        tv.setText( ssb, TextView.BufferType.SPANNABLE );
    }

    public void SignOut(View view){
        Intent intent = new Intent( HomeActivity.this, MainActivity.class );
        startActivity( intent );
    }

    public void ViewAccount(View view){
        Intent intent = new Intent(HomeActivity.this, UserActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void ClearSystem(View view) {

        if ("admin".equals(name)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();

                db.execSQL("DROP TABLE IF EXISTS purchases;");
                db.execSQL("DROP TABLE IF EXISTS customer;");
                db.execSQL("DROP TABLE IF EXISTS book;");

                db.execSQL(MySQLiteHelper.CREATE_TABLE_BOOK);
                db.execSQL(MySQLiteHelper.CREATE_TABLE_CUSTOMER);
                db.execSQL(MySQLiteHelper.CREATE_TABLE_PURCHASES);

                String insertAdmin = "INSERT INTO customer (ID, name, total_spent) VALUES (NULL, 'admin', 0);";
                db.execSQL(insertAdmin);

                db.setTransactionSuccessful();

                Toast.makeText(view.getContext(), "System reset successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("ClearSystem", "Error clearing database: " + e.getMessage(), e);
                Toast.makeText(view.getContext(), "Failed to reset system", Toast.LENGTH_SHORT).show();
            } finally {
                db.endTransaction();
                db.close();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }else {
            Toast.makeText(view.getContext(), "Only admin can clear system", Toast.LENGTH_SHORT).show();
        }
    }

    public void EnterBooks(View view){
        if ("admin".equals(name)) {
            Intent intent = new Intent(HomeActivity.this, AdminActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(view.getContext(), "Only admin can enter books", Toast.LENGTH_SHORT).show();
        }
    }
    public void ListUsers(View view){
        if ("admin".equals(name)) {
            //Toast.makeText(view.getContext(), "ListActivity called", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, ListActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(view.getContext(), "Only admin can list all users", Toast.LENGTH_SHORT).show();
        }
    }
}