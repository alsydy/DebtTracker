package com.example.debttracker;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Debt.class}, version = 1, exportSchema = false)
public abstract class DebtDatabase extends RoomDatabase {
    private static DebtDatabase instance;

    public abstract DebtDao debtDao();

    public static synchronized DebtDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DebtDatabase.class, "debt_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // For simplicity, in production use background threads
                    .build();
        }
        return instance;
    }
}

