package com.example.paymind;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.paymind.seeders.SettingsSeeder;
import com.example.paymind.seeders.SubscriptionSeeder;
import com.example.paymind.seeders.SubscriptionTypeSeeder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "paymind.db";
    private static String DB_LOCATION;
    private static final int DB_VERSION = 1;

    private final Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_LOCATION = context.getApplicationInfo().dataDir + "/databases/";

        copyDB();

        SQLiteDatabase db = getWritableDatabase();
        checkAndSeedDatabase(db);
        db.close();
    }

    private boolean checkDB() {
        File fileDB = new File(DB_LOCATION + DB_NAME);
        return fileDB.exists();
    }

    private void copyDB() {
        if (!checkDB()) {
            this.getReadableDatabase();
            try {
                copyDBFile();
            } catch (IOException e) {
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream inputStream = myContext.getAssets().open(DB_NAME);
        OutputStream outputStream = new FileOutputStream(DB_LOCATION + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void checkAndSeedDatabase(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM subscription_types", null);

        //Для перезапуска сидеров
        /*
       db.beginTransaction();
        try {
            db.delete("subscription_types", null, null);
            db.delete("settings", null, null);
            db.delete("subscriptions", null, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        } */


        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        Log.d("count", "ew" + count);
        if (count == 0) {
            Log.d("DBHelper", "БД пустая, запускаем сидеры...");
            SubscriptionTypeSeeder subscriptionTypeSeeder = new SubscriptionTypeSeeder(db);
            subscriptionTypeSeeder.run();

            SettingsSeeder settingsSeeder = new SettingsSeeder(db);
            settingsSeeder.run();

            SubscriptionSeeder subscriptionSeeder = new SubscriptionSeeder(db);
            subscriptionSeeder.run();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
