package com.example.debttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddDebtActivity extends AppCompatActivity {
    private TextInputEditText etPersonName, etAmount, etDescription;
    private RadioGroup rgDebtType;
    private RadioButton rbOwedToMe, rbIOwe;
    private Button btnSave, btnCancel;
    
    private DebtDatabase database;
    private DebtDao debtDao;
    private Debt editingDebt;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);

        initViews();
        initDatabase();
        checkIfEditing();
        setupClickListeners();
    }

    private void initViews() {
        etPersonName = findViewById(R.id.et_person_name);
        etAmount = findViewById(R.id.et_amount);
        etDescription = findViewById(R.id.et_description);
        rgDebtType = findViewById(R.id.rg_debt_type);
        rbOwedToMe = findViewById(R.id.rb_owed_to_me);
        rbIOwe = findViewById(R.id.rb_i_owe);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void initDatabase() {
        database = DebtDatabase.getInstance(this);
        debtDao = database.debtDao();
    }

    private void checkIfEditing() {
        int debtId = getIntent().getIntExtra("debt_id", -1);
        if (debtId != -1) {
            isEditing = true;
            editingDebt = debtDao.getDebtById(debtId);
            populateFields();
            setTitle("تعديل الدين");
        } else {
            setTitle("إضافة دين جديد");
        }
    }

    private void populateFields() {
        if (editingDebt != null) {
            etPersonName.setText(editingDebt.getPersonName());
            etAmount.setText(String.valueOf(editingDebt.getAmount()));
            etDescription.setText(editingDebt.getDescription());
            
            if (editingDebt.isOwedToMe()) {
                rbOwedToMe.setChecked(true);
            } else {
                rbIOwe.setChecked(true);
            }
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveDebt());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveDebt() {
        String personName = etPersonName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(personName)) {
            etPersonName.setError("يرجى إدخال اسم الشخص");
            etPersonName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("يرجى إدخال المبلغ");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("يجب أن يكون المبلغ أكبر من صفر");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("يرجى إدخال مبلغ صحيح");
            etAmount.requestFocus();
            return;
        }

        boolean isOwedToMe = rbOwedToMe.isChecked();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (isEditing && editingDebt != null) {
            // Update existing debt
            editingDebt.setPersonName(personName);
            editingDebt.setAmount(amount);
            editingDebt.setDescription(description);
            editingDebt.setOwedToMe(isOwedToMe);
            debtDao.updateDebt(editingDebt);
            Toast.makeText(this, "تم تحديث الدين بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            // Create new debt
            Debt newDebt = new Debt(personName, amount, description, currentDate, isOwedToMe);
            debtDao.insertDebt(newDebt);
            Toast.makeText(this, "تم إضافة الدين بنجاح", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}

