package com.example.nicksnell.new457;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicksnell.new457.database.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends Activity {

    private static final String TAG = "StoreActivity";

    private String name;
    private MySQLiteHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        int priceRange = intent.getIntExtra("priceRange", 0);

        dbHelper = new MySQLiteHelper(this);

        fetchAndDisplayBooks(priceRange);
    }

    private void fetchAndDisplayBooks(int priceRange) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> books = new ArrayList<>();

        String query = "SELECT * FROM book WHERE price BETWEEN ? AND ? " +
                "AND ISBN NOT IN (SELECT ISBN FROM purchases WHERE custID = (SELECT ID FROM customer WHERE name = ?))";
        String[] args;

        switch (priceRange) {
            case 1:
                args = new String[]{"0", "10", name};
                break;
            case 2:
                args = new String[]{"10", "20", name};
                break;
            case 3:
                args = new String[]{"20", "30", name};
                break;
            case 4:
                query = "SELECT * FROM book WHERE price >= ? " +
                        "AND ISBN NOT IN (SELECT ISBN FROM purchases WHERE custID = (SELECT ID FROM customer WHERE name = ?))";
                args = new String[]{"30", name};
                break;
            default:
                query = "SELECT * FROM book WHERE ISBN NOT IN (SELECT ISBN FROM purchases WHERE custID = (SELECT ID FROM customer WHERE name = ?))";
                args = new String[]{name};
                break;
        }

        Cursor cursor = db.rawQuery(query, args);

        while (cursor.moveToNext()) {
            String bookISBN = cursor.getString(cursor.getColumnIndexOrThrow("ISBN"));
            String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String bookPrice = cursor.getString(cursor.getColumnIndexOrThrow("price"));

            books.add(bookISBN);
            books.add(bookTitle);
            books.add(bookPrice);
        }
        cursor.close();

        populateBookList(books);
    }

    private void populateBookList(List<String> books) {
        LinearLayout bookListLayout = findViewById(R.id.bookListLayout);
        bookListLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < books.size(); i += 3) {
            String bookISBN = books.get(i);
            final String bookTitle = books.get(i + 1);
            String bookPrice = books.get(i + 2);

            View bookItemView = inflater.inflate(R.layout.book_item_layout, bookListLayout, false);

            CheckBox checkbox = bookItemView.findViewById(R.id.checkbox);
            TextView bookTitleTextView = bookItemView.findViewById(R.id.bookTitle);

            checkbox.setChecked(false);
            bookTitleTextView.setText(bookTitle);
            bookTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StoreActivity.this, BookActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("title", bookTitle);
                    startActivity(intent);
                }
            });

            bookListLayout.addView(bookItemView);
        }
    }

    public void onBackButton(View view) {
        Intent intent = new Intent(StoreActivity.this, HomeActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    public void onPurchaseButton(View view) {
        LinearLayout bookListLayout = findViewById(R.id.bookListLayout);
        List<String> selectedBooks = new ArrayList<>();

        for (int i = 0; i < bookListLayout.getChildCount(); i++) {
            View bookItemView = bookListLayout.getChildAt(i);
            CheckBox checkbox = bookItemView.findViewById(R.id.checkbox);
            TextView bookTitleTextView = bookItemView.findViewById(R.id.bookTitle);

            if (checkbox.isChecked()) {
                String bookTitle = bookTitleTextView.getText().toString();
                selectedBooks.add(bookTitle);
            }
        }

        if (selectedBooks.isEmpty()) {
            Toast.makeText(this, "Please select at least one book to purchase", Toast.LENGTH_SHORT).show();
        } else {
            savePurchases(selectedBooks);
            Toast.makeText(this, "Purchase Successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePurchases(List<String> selectedBooks) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        double totalPrice = 0.0;

        for (String title : selectedBooks) {

            Cursor cursor = db.rawQuery("SELECT price FROM book WHERE title = ?", new String[]{title});
            if (cursor.moveToFirst()) {
                double price = cursor.getDouble(0);
                totalPrice += price;
            }
            cursor.close();
            db.execSQL("INSERT INTO purchases (custID, ISBN, quantity, title) VALUES ((SELECT ID FROM customer WHERE name = ?), (SELECT ISBN FROM book WHERE title = ?), 1, ?)",
                    new Object[]{name, title, title});
        }
        db.execSQL("UPDATE customer SET total_spent = total_spent + ? WHERE name = ?", new Object[]{totalPrice, name});
    }
}