package com.example.paymind;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.paymind.enums.CategoryType;
import com.example.paymind.enums.CurrencyType;
import com.example.paymind.enums.PeriodType;
import com.example.paymind.enums.StatusType;
import com.example.paymind.models.Subscription;
import com.example.paymind.models.SubscriptionType;
import com.example.paymind.repositories.SubscriptionRepository;
import com.example.paymind.repositories.SubscriptionTypeRepository;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddSubscriptionActivity extends AppCompatActivity {
    private TextView title;
    private EditText etServiceName;
    private EditText etCost;
    private MaterialAutoCompleteTextView actvCurrency;
    private MaterialAutoCompleteTextView actvPeriodType;
    private EditText etReminderDays;
    private EditText etPeriodValue;
    private Button btnStartDate;
    private EditText etRenewalDate;
    private CheckBox cbAutoRenew;
    private EditText etNotes;
    private Button btnSave;
    private Button btnCancel;
    private MaterialAutoCompleteTextView actvSubscriptionType;
    private List<SubscriptionType> subscriptionTypes;
    private DBHelper dbHelper;
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionTypeRepository subscriptionTypeRepository;

    private Calendar startDateCalendar;
    private Calendar renewalDateCalendar;
    private SimpleDateFormat dateFormatter;
    private TextView typeLabel;
    private TextInputLayout typeSelect;
    private TextView tvCategoryLabel;
    private TextInputLayout tilCategory;
    private MaterialAutoCompleteTextView actvCategory;

    private int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_subscription);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryId = getIntent().getIntExtra("category_id", 0);
        title = findViewById(R.id.title);

        if (categoryId == CategoryType.SUBSCRIPTION.getId()) {
            title.setText("Добавить подписку");
        } else if (categoryId == CategoryType.SERVICE.getId()) {
            title.setText("Создать услугу");
        }

        dbHelper = new DBHelper(this);
        subscriptionRepository = new SubscriptionRepository(dbHelper);
        subscriptionTypeRepository = new SubscriptionTypeRepository(dbHelper);

        initViews();
        setupAutoCompleteTextViews();
        setupDatePickers();
        setupListeners();
    }

    private void initViews() {
        etServiceName = findViewById(R.id.etServiceName);
        etCost = findViewById(R.id.etCost);
        actvCurrency = findViewById(R.id.spinnerCurrency);
        actvPeriodType = findViewById(R.id.spinnerPeriodType);
        etReminderDays = findViewById(R.id.etReminderDays);
        etPeriodValue = findViewById(R.id.etPeriodValue);
        btnStartDate = findViewById(R.id.btnStartDate);
        etRenewalDate = findViewById(R.id.etRenewalDate);
        cbAutoRenew = findViewById(R.id.cbAutoRenew);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        actvSubscriptionType = findViewById(R.id.spinnerSubscriptionType);

        tvCategoryLabel = findViewById(R.id.category_label);
        tilCategory = findViewById(R.id.category_select);
        actvCategory = findViewById(R.id.spinnerSubscriptionCategory);

        typeLabel = findViewById(R.id.type_label);
        typeSelect = findViewById(R.id.type_select);

        if (categoryId != 0) {
            tvCategoryLabel.setVisibility(GONE);
            tilCategory.setVisibility(GONE);
        } else {
            tvCategoryLabel.setVisibility(VISIBLE);
            tilCategory.setVisibility(VISIBLE);

            List<String> categories = new ArrayList<>();
            categories.add(CategoryType.SUBSCRIPTION.getName());
            categories.add(CategoryType.SERVICE.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, categories);
            actvCategory.setAdapter(adapter);

            actvCategory.setOnItemClickListener((parent, view, position, id) -> {
                String selected = actvCategory.getText().toString();
                if (selected.equals(CategoryType.SUBSCRIPTION.getName())) {
                    categoryId = CategoryType.SUBSCRIPTION.getId();
                    actvSubscriptionType.setVisibility(VISIBLE);
                    typeLabel.setVisibility(VISIBLE);
                    typeSelect.setVisibility(VISIBLE);
                } else {
                    categoryId = CategoryType.SERVICE.getId();
                    actvSubscriptionType.setVisibility(GONE);
                    typeLabel.setVisibility(GONE);
                    typeSelect.setVisibility(GONE);
                }
            });
        }
        if (categoryId == CategoryType.SERVICE.getId()) {
            actvSubscriptionType.setVisibility(GONE);
            typeLabel.setVisibility(GONE);
            typeSelect.setVisibility(GONE);
        }

        startDateCalendar = Calendar.getInstance();
        renewalDateCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        updateDateButtons();
        calculateRenewalDate();
    }

    private void setupAutoCompleteTextViews() {
        if (categoryId == 0) {
            actvCategory.setText(CategoryType.SUBSCRIPTION.getName(), false);
            categoryId = CategoryType.SUBSCRIPTION.getId();
        }
        if (categoryId == CategoryType.SUBSCRIPTION.getId()) {
            subscriptionTypes = subscriptionTypeRepository.getAllSubscriptionTypes();

            List<String> typeNames = new ArrayList<>();
            for (SubscriptionType type : subscriptionTypes) {
                typeNames.add(type.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, typeNames);
            actvSubscriptionType.setAdapter(adapter);

            if (!typeNames.isEmpty()) {
                actvSubscriptionType.setText(typeNames.get(0), false);
            }
        }

        List<String> currencies = new ArrayList<>();
        for (CurrencyType currency : CurrencyType.values()) {
            currencies.add(currency.getCode());
        }
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, currencies);
        actvCurrency.setAdapter(currencyAdapter);

        actvCurrency.setText(currencies.get(2), false);
        actvCurrency.setThreshold(1);

        List<String> periods = new ArrayList<>();
        for (PeriodType period : PeriodType.values()) {
            periods.add(period.getName());
        }
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, periods);
        actvPeriodType.setAdapter(periodAdapter);

        actvPeriodType.setText(periods.get(2), false);
        actvPeriodType.setThreshold(1);

        actvPeriodType.setOnItemClickListener((parent, view, position, id) -> {
            calculateRenewalDate();
        });

        etReminderDays.setText("3");
    }

    private void setupDatePickers() {
        btnStartDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        startDateCalendar.set(year, month, dayOfMonth);
                        updateDateButtons();
                        calculateRenewalDate();
                    },
                    startDateCalendar.get(Calendar.YEAR),
                    startDateCalendar.get(Calendar.MONTH),
                    startDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveSubscription());
        btnCancel.setOnClickListener(v -> finish());

        etPeriodValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateRenewalDate();
            }
        });
    }

    private void updateDateButtons() {
        btnStartDate.setText(dateFormatter.format(startDateCalendar.getTime()));
    }

    private void calculateRenewalDate() {
        if (startDateCalendar == null) return;

        renewalDateCalendar.setTime(startDateCalendar.getTime());

        try {
            int periodValue = 1;
            String periodValueStr = etPeriodValue.getText().toString().trim();
            if (!periodValueStr.isEmpty()) {
                periodValue = Integer.parseInt(periodValueStr);
                if (periodValue <= 0) periodValue = 1;
            }

            String periodTypeStr = actvPeriodType.getText().toString().trim();
            if (!periodTypeStr.isEmpty()) {
                switch (periodTypeStr.toLowerCase()) {
                    case "день":
                        renewalDateCalendar.add(Calendar.DAY_OF_MONTH, periodValue);
                        break;
                    case "неделя":
                        renewalDateCalendar.add(Calendar.WEEK_OF_YEAR, periodValue);
                        break;
                    case "месяц":
                        renewalDateCalendar.add(Calendar.MONTH, periodValue);
                        break;
                    case "год":
                        renewalDateCalendar.add(Calendar.YEAR, periodValue);
                        break;
                    default:
                        renewalDateCalendar.add(Calendar.MONTH, periodValue);
                }
            } else {
                renewalDateCalendar.add(Calendar.MONTH, periodValue);
            }

            etRenewalDate.setText(dateFormatter.format(renewalDateCalendar.getTime()));

        } catch (Exception e) {
            etRenewalDate.setText(dateFormatter.format(startDateCalendar.getTime()));
        }
    }

    private void saveSubscription() {
        if (etServiceName.getText().toString().trim().isEmpty()) {
            etServiceName.setError("Введите название сервиса");
            etServiceName.requestFocus();
            return;
        }

        if (etCost.getText().toString().trim().isEmpty()) {
            etCost.setError("Введите стоимость");
            etCost.requestFocus();
            return;
        }

        try {
            Subscription subscription = new Subscription();

            if (categoryId == CategoryType.SUBSCRIPTION.getId()) {
                String selectedName = actvSubscriptionType.getText().toString().trim();
                int typeId = 1;
                for (SubscriptionType type : subscriptionTypes) {
                    if (type.getName().equals(selectedName)) {
                        typeId = type.getId();
                        break;
                    }
                }
                subscription.setTypeId(typeId);
            }

            subscription.setName(etServiceName.getText().toString().trim());
            subscription.setCategoryId(categoryId);

            double cost = Double.parseDouble(etCost.getText().toString());
            if (cost <= 0) {
                etCost.setError("Стоимость должна быть больше 0");
                etCost.requestFocus();
                return;
            }
            subscription.setCost(cost);

            String selectedCurrency = actvCurrency.getText().toString().trim();
            int currencyId = getCurrencyIdFromSelection(selectedCurrency);
            subscription.setCurrencyId(currencyId);

            subscription.setStartDate(startDateCalendar.getTimeInMillis() / 1000);
            subscription.setRenewalDate(renewalDateCalendar.getTimeInMillis() / 1000);

            String selectedPeriod = actvPeriodType.getText().toString().trim();
            int periodTypeId = getPeriodTypeIdFromName(selectedPeriod);
            subscription.setPeriodTypeId(periodTypeId);

            String periodValueStr = etPeriodValue.getText().toString().trim();
            int periodValue = periodValueStr.isEmpty() ? 1 : Integer.parseInt(periodValueStr);
            if (periodValue <= 0) periodValue = 1;
            subscription.setPeriodValue(periodValue);

            subscription.setAutoRenew(cbAutoRenew.isChecked());

            subscription.setNotes(etNotes.getText().toString());

            subscription.setStatusId(StatusType.PAID.getId());

            String reminderDaysStr = etReminderDays.getText().toString().trim();
            int reminderDays = reminderDaysStr.isEmpty() ? 3 : Integer.parseInt(reminderDaysStr);
            subscription.setReminderDaysBefore(reminderDays);

            long currentTime = System.currentTimeMillis();
            subscription.setCreatedAt(currentTime);
            subscription.setUpdatedAt(currentTime);

            boolean success = subscriptionRepository.insertSubscription(subscription);
            if (success) {
                Toast.makeText(this, "Подписка добавлена", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Ошибка при добавлении подписки", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка в формате числа: проверьте стоимость и период", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int getCurrencyIdFromSelection(String selection) {
        if (selection == null || selection.isEmpty()) {
            return CurrencyType.RUB.getId();
        }

        String[] parts = selection.split(" - ");
        if (parts.length >= 2) {
            String code = parts[1].trim();
            for (CurrencyType currency : CurrencyType.values()) {
                if (currency.getCode().equals(code)) {
                    return currency.getId();
                }
            }
        }
        return CurrencyType.RUB.getId();
    }

    private int getPeriodTypeIdFromName(String name) {
        if (name == null || name.isEmpty()) {
            return PeriodType.MONTH.getId();
        }

        for (PeriodType period : PeriodType.values()) {
            if (period.getName().equalsIgnoreCase(name)) {
                return period.getId();
            }
        }
        return PeriodType.MONTH.getId();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}