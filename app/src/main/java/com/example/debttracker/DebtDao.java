package com.example.debttracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY date DESC")
    List<Debt> getAllDebts();

    @Query("SELECT * FROM debts WHERE id = :id")
    Debt getDebtById(int id);

    @Query("SELECT SUM(amount) FROM debts WHERE isOwedToMe = 1 AND isPaid = 0")
    double getTotalOwedToMe();

    @Query("SELECT SUM(amount) FROM debts WHERE isOwedToMe = 0 AND isPaid = 0")
    double getTotalIOwe();

    @Insert
    void insertDebt(Debt debt);

    @Update
    void updateDebt(Debt debt);

    @Delete
    void deleteDebt(Debt debt);

    @Query("SELECT * FROM debts WHERE personName LIKE :name")
    List<Debt> searchDebtsByName(String name);
}

