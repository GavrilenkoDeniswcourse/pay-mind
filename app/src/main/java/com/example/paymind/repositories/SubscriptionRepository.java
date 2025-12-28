package com.example.paymind.repositories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.paymind.DBHelper;
import com.example.paymind.enums.CategoryType;
import com.example.paymind.enums.CurrencyType;
import com.example.paymind.enums.PeriodType;
import com.example.paymind.enums.StatusType;
import com.example.paymind.models.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionRepository {
    private final DBHelper dbHelper;

    public SubscriptionRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM subscriptions ORDER BY renewal_date ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int statusId = cursor.getInt(cursor.getColumnIndexOrThrow("status_id"));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"));
                int currencyId = cursor.getInt(cursor.getColumnIndexOrThrow("currency_id"));
                int periodTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("period_type_id"));

                Subscription subscription = new Subscription();
                subscription.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                subscription.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                subscription.setCategoryId(categoryId);
                subscription.setCost(cursor.getDouble(cursor.getColumnIndexOrThrow("cost")));
                subscription.setCurrencyId(currencyId);
                subscription.setStartDate(cursor.getLong(cursor.getColumnIndexOrThrow("start_date")));
                subscription.setRenewalDate(cursor.getLong(cursor.getColumnIndexOrThrow("renewal_date")));
                subscription.setPeriodTypeId(periodTypeId);
                subscription.setPeriodValue(cursor.getInt(cursor.getColumnIndexOrThrow("period_value")));
                subscription.setAutoRenew(cursor.getInt(cursor.getColumnIndexOrThrow("auto_renew")) == 1);
                subscription.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
                subscription.setStatusId(statusId);
                subscription.setReminderDaysBefore(cursor.getInt(cursor.getColumnIndexOrThrow("reminder_days_before")));
                subscription.setTypeId(cursor.getInt(cursor.getColumnIndexOrThrow("type_id")));
                subscription.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
                subscription.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updated_at")));

                subscription.setCategoryName(CategoryType.fromId(categoryId).getName());
                subscription.setStatusName(StatusType.fromId(statusId).getName());
                subscription.setCurrencySymbol(CurrencyType.fromId(currencyId).getSymbol());
                subscription.setPeriodTypeName(PeriodType.fromId(periodTypeId).getName());

                subscriptions.add(subscription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return subscriptions;
    }

    public List<Subscription> getSubscriptionsByCategory(int categoryId) {
        List<Subscription> subscriptions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT *,subscription_types.name as type_name FROM subscriptions LEFT JOIN subscription_types ON subscriptions.type_id = subscription_types.id WHERE category_id = ? ORDER BY renewal_date ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                int statusId = cursor.getInt(11);
                int currencyId = cursor.getInt(4);
                int periodTypeId = cursor.getInt(7);

                Subscription subscription = new Subscription();
                subscription.setId(cursor.getInt(0));
                subscription.setName(cursor.getString(1));
                subscription.setCategoryId(cursor.getInt(2));
                subscription.setCost(cursor.getDouble(3));
                subscription.setCurrencyId(currencyId);
                subscription.setStartDate(cursor.getLong(5));
                subscription.setRenewalDate(cursor.getLong(6));
                subscription.setPeriodTypeId(periodTypeId);
                subscription.setPeriodValue(cursor.getInt(8));
                subscription.setAutoRenew(cursor.getInt(9) == 1);
                subscription.setNotes(cursor.getString(10));
                subscription.setStatusId(statusId);
                subscription.setReminderDaysBefore(cursor.getInt(12));
                subscription.setTypeId(cursor.getInt(13));
                subscription.setCreatedAt(cursor.getLong(14));
                subscription.setUpdatedAt(cursor.getLong(15));

                subscription.setCategoryName(CategoryType.fromId(categoryId).getName());
                subscription.setStatusName(StatusType.fromId(statusId).getName());
                subscription.setCurrencySymbol(CurrencyType.fromId(currencyId).getSymbol());
                subscription.setPeriodTypeName(PeriodType.fromId(periodTypeId).getName());
                if (cursor.getString(17) != null ) {
                    subscription.setTypeName(cursor.getString(17));
                }
                Log.d("DB_DEBUG", subscription.getId() + " ");
                subscriptions.add(subscription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return subscriptions;
    }

    public boolean deleteSubscription(int subscriptionId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            int rowsDeleted = db.delete("subscriptions", "id = ?",
                    new String[]{String.valueOf(subscriptionId)});
            Log.d("deletedCount", rowsDeleted + " ");
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e("SubscriptionRepository", "Ошибка при удалении подписки", e);
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insertSubscription(Subscription subscription) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", subscription.getName());
        values.put("category_id", subscription.getCategoryId());
        values.put("cost", subscription.getCost());
        values.put("currency_id", subscription.getCurrencyId());
        values.put("start_date", subscription.getStartDate());
        values.put("renewal_date", subscription.getRenewalDate());
        values.put("period_type_id", subscription.getPeriodTypeId());
        values.put("period_value", subscription.getPeriodValue());
        values.put("auto_renew", subscription.isAutoRenew() ? 1 : 0);
        values.put("notes", subscription.getNotes());
        values.put("status_id", subscription.getStatusId());
        values.put("reminder_days_before", subscription.getReminderDaysBefore());
        values.put("type_id", subscription.getTypeId());
        values.put("created_at", subscription.getCreatedAt());
        values.put("updated_at", subscription.getUpdatedAt());

        try {
            long result = db.insert("subscriptions", null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("SubscriptionRepository", "Ошибка при добавлении подписки", e);
            return false;
        } finally {
            db.close();
        }
    }
}
