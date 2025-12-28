package com.example.paymind.repositories;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.paymind.DBHelper;
import com.example.paymind.models.SubscriptionType;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionTypeRepository {
    private final DBHelper dbHelper;

    public SubscriptionTypeRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<SubscriptionType> getAllSubscriptionTypes() {
        List<SubscriptionType> subscriptionTypes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM subscription_types";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SubscriptionType subscriptionType = new SubscriptionType();
                subscriptionType.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                subscriptionType.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));


                subscriptionTypes.add(subscriptionType);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return subscriptionTypes;
    }
}
