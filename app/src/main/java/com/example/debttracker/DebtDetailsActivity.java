package com.example.debttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;

public class DebtDetailsActivity extends AppCompatActivity {
    private TextView tvPersonName, tvAmount, tvDescription, tvDate, tvType, tvStatus;
    
    private DebtDatabase database;
    private DebtDao debtDao;
    private Debt debt;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_details);

        initViews();
        initDatabase();
        loadDebtDetails();
    }

    private void initViews() {
        tvPersonName = findViewById(R.id.tv_person_name);
        tvAmount = findViewById(R.id.tv_amount);
        tvDescription = findViewById(R.id.tv_description);
        tvDate = findViewById(R.id.tv_date);
        tvType = findViewById(R.id.tv_type);
        tvStatus = findViewById(R.id.tv_status);
        
        decimalFormat = new DecimalFormat("#0.00");
    }

    private void initDatabase() {
        database = DebtDatabase.getInstance(this);
        debtDao = database.debtDao();
    }

    private void loadDebtDetails() {
        int debtId = getIntent().getIntExtra("debt_id", -1);
        if (debtId != -1) {
            debt = debtDao.getDebtById(debtId);
            if (debt != null) {
                displayDebtDetails();
            }
        }
    }

    private void displayDebtDetails() {
        tvPersonName.setText(debt.getPersonName());
        tvAmount.setText(decimalFormat.format(debt.getAmount()));
        tvDescription.setText(debt.getDescription());
        tvDate.setText(debt.getDate());
        
        if (debt.isOwedToMe()) {
            tvType.setText(getString(R.string.owed_to_me));
            tvAmount.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvType.setText(getString(R.string.i_owe));
            tvAmount.setTextColor(getResources().getColor(R.color.red));
        }
        
        if (debt.isPaid()) {
            tvStatus.setText(getString(R.string.paid));
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        } else {
            tvStatus.setText(getString(R.string.unpaid));
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }
        
        setTitle(debt.getPersonName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.debt_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editDebt();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_toggle_paid:
                togglePaidStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editDebt() {
        Intent intent = new Intent(this, AddDebtActivity.class);
        intent.putExtra("debt_id", debt.getId());
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف هذا الدين؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    debtDao.deleteDebt(debt);
                    finish();
                })
                .setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void togglePaidStatus() {
        debt.setPaid(!debt.isPaid());
        debtDao.updateDebt(debt);
        displayDebtDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDebtDetails(); // Refresh data when returning from edit
    }
}

