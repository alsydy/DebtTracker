package com.example.debttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DebtAdapter.OnDebtClickListener {
    private RecyclerView rvDebts;
    private DebtAdapter debtAdapter;
    private TextView tvTotalOwedToMe, tvTotalIOwe;
    private FloatingActionButton fabAddDebt;
    
    private DebtDatabase database;
    private DebtDao debtDao;
    private List<Debt> debtList;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadDebts();
    }

    private void initViews() {
        rvDebts = findViewById(R.id.rv_debts);
        tvTotalOwedToMe = findViewById(R.id.tv_total_owed_to_me);
        tvTotalIOwe = findViewById(R.id.tv_total_i_owe);
        fabAddDebt = findViewById(R.id.fab_add_debt);
        
        decimalFormat = new DecimalFormat("#0.00");
    }

    private void initDatabase() {
        database = DebtDatabase.getInstance(this);
        debtDao = database.debtDao();
    }

    private void setupRecyclerView() {
        debtList = new ArrayList<>();
        debtAdapter = new DebtAdapter(this, debtList);
        debtAdapter.setOnDebtClickListener(this);
        
        rvDebts.setLayoutManager(new LinearLayoutManager(this));
        rvDebts.setAdapter(debtAdapter);
    }

    private void setupClickListeners() {
        fabAddDebt.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddDebtActivity.class);
            startActivity(intent);
        });
    }

    private void loadDebts() {
        debtList = debtDao.getAllDebts();
        debtAdapter.updateDebts(debtList);
        updateSummary();
    }

    private void updateSummary() {
        double totalOwedToMe = debtDao.getTotalOwedToMe();
        double totalIOwe = debtDao.getTotalIOwe();
        
        tvTotalOwedToMe.setText(decimalFormat.format(totalOwedToMe));
        tvTotalIOwe.setText(decimalFormat.format(totalIOwe));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDebts(); // Refresh data when returning from other activities
    }

    @Override
    public void onDebtClick(Debt debt) {
        Intent intent = new Intent(this, DebtDetailsActivity.class);
        intent.putExtra("debt_id", debt.getId());
        startActivity(intent);
    }

    @Override
    public void onDebtLongClick(Debt debt) {
        showDebtOptionsDialog(debt);
    }

    private void showDebtOptionsDialog(Debt debt) {
        String[] options;
        if (debt.isPaid()) {
            options = new String[]{getString(R.string.mark_as_unpaid), getString(R.string.edit), getString(R.string.delete)};
        } else {
            options = new String[]{getString(R.string.mark_as_paid), getString(R.string.edit), getString(R.string.delete)};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(debt.getPersonName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Toggle paid status
                            debt.setPaid(!debt.isPaid());
                            debtDao.updateDebt(debt);
                            loadDebts();
                            break;
                        case 1: // Edit
                            Intent intent = new Intent(MainActivity.this, AddDebtActivity.class);
                            intent.putExtra("debt_id", debt.getId());
                            startActivity(intent);
                            break;
                        case 2: // Delete
                            showDeleteConfirmationDialog(debt);
                            break;
                    }
                });
        builder.show();
    }

    private void showDeleteConfirmationDialog(Debt debt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف هذا الدين؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    debtDao.deleteDebt(debt);
                    loadDebts();
                })
                .setNegativeButton("إلغاء", null);
        builder.show();
    }
}

