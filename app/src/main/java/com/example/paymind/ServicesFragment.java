package com.example.paymind;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.paymind.enums.CategoryType;
import com.example.paymind.models.Subscription;
import com.example.paymind.repositories.SubscriptionRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServicesFragment extends Fragment {

    private SubscriptionRepository subscriptionRepository;
    private DBHelper dbHelper;
    private LinearLayout paymentsList;
    private TextView totalAmountView;
    private TextView remindersCountView;
    private FloatingActionButton fabAddSubscription;
    private ActivityResultLauncher<Intent> addSubscriptionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getContext());
        subscriptionRepository = new SubscriptionRepository(requireContext().getApplicationContext(), dbHelper);

        addSubscriptionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        refreshSubscriptionsList();
                    }
                }
        );
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_services, container, false);

        paymentsList = view.findViewById(R.id.payments_list);
        totalAmountView = view.findViewById(R.id.total_amount);
        remindersCountView = view.findViewById(R.id.reminders_count);
        fabAddSubscription = view.findViewById(R.id.fabAddSubscription);

        refreshSubscriptionsList();

        fabAddSubscription.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddSubscriptionActivity.class);
            intent.putExtra("category_id", CategoryType.SERVICE.getId());
            addSubscriptionLauncher.launch(intent);
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void refreshSubscriptionsList() {
        paymentsList.removeAllViews();

        List<Subscription> subscriptions = subscriptionRepository.getSubscriptionsByCategory(CategoryType.SERVICE.getId());
        Log.d("subs", subscriptions.toString());
        int totalAmount = 0;
        int remindersCount = subscriptions.size();
        for (int i = 0; i < remindersCount; i++) {
            Subscription subscription = subscriptions.get(i);

            totalAmount += (int) subscription.getCost();

            View cardView = createSubscriptionCard(subscription);
            paymentsList.addView(cardView);
        }

        totalAmountView.setText(String.format("%d ₽", totalAmount));
        remindersCountView.setText(getRemindersText(remindersCount));
    }

    @SuppressLint("DefaultLocale")
    private View createSubscriptionCard(Subscription subscription) {
        View cardView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_subscription_detailed_card, paymentsList, false);

        TextView tvServiceName = cardView.findViewById(R.id.tvServiceName);
        TextView tvCategory = cardView.findViewById(R.id.tvCategory);
        TextView tvStatus = cardView.findViewById(R.id.tvStatus);
        TextView tvDueDate = cardView.findViewById(R.id.tvDueDate);
        TextView tvAmount = cardView.findViewById(R.id.tvAmount);
        Button btnPay = cardView.findViewById(R.id.btnPay);
        Button btnDelete = cardView.findViewById(R.id.btnDelete);

        tvServiceName.setText(subscription.getName());

        String currencySymbol = subscription.getCurrencySymbol();
        if (currencySymbol == null || currencySymbol.isEmpty()) {
            currencySymbol = "₽";
        }

        tvCategory.setText((String.format("Сумма: %.0f %s",
                subscription.getCost(),
                currencySymbol
        )));

        String statusText = subscription.getDisplayStatus();
        tvStatus.setText(statusText);

        if (subscription.isPending()) {
            tvStatus.setBackgroundResource(R.drawable.status_background_orange);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
            btnPay.setVisibility(VISIBLE);
        } else if (subscription.isPaid()) {
            tvStatus.setBackgroundResource(R.drawable.status_background_blue);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
            btnPay.setVisibility(View.GONE);
        }

        tvAmount.setText(subscription.getDueDateText());

        tvDueDate.setText(subscription.getLastPaymentText());
        if (subscription.isPaid()) {
            btnPay.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnDelete.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.setMarginStart(0);
            btnDelete.setLayoutParams(params);
        } else {
            btnPay.setVisibility(View.VISIBLE);
        }

        btnPay.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            Date nowDate = new Date(now);

            Date nextRenewalDate = subscription.calculateNextPaymentDate(nowDate);
            long nextRenewalDateMillis = nextRenewalDate.getTime();

            String nextDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(new Date(nextRenewalDateMillis));

            new AlertDialog.Builder(requireContext())
                    .setTitle("Оплата услуги")
                    .setMessage("Подтвердите оплату услуги:\n\n" +
                            "Название: " + subscription.getName() + "\n" +
                            "Сумма: " + subscription.getCost() + " " +
                            subscription.getCurrencySymbol() + "\n" +
                            "Следующая оплата: " + nextDate)
                    .setPositiveButton("Оплатить", (dialog, which) -> {
                        boolean success = subscriptionRepository.markAsPaid(subscription);
                        if (success) {
                            Toast.makeText(getContext(),
                                    "✓ Подписка оплачена\nСледующая оплата: " + nextDate,
                                    Toast.LENGTH_LONG).show();

                            refreshSubscriptionsList();
                        } else {
                            Toast.makeText(getContext(),
                                    "✗ Ошибка при оплате подписки",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удаление услуги")
                    .setMessage("Вы уверены, что хотите удалить услугу \"" +
                            subscription.getName() + "\"?")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        boolean success = subscriptionRepository.deleteSubscription(subscription.getId());
                        if (success) {
                            Toast.makeText(getContext(),
                                    "Услуга удалена",
                                    Toast.LENGTH_SHORT).show();

                            refreshSubscriptionsList();
                        } else {
                            Toast.makeText(getContext(),
                                    "Ошибка при удалении",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        return cardView;
    }

    private String getRemindersText(int count) {
        int lastDigit = count % 10;
        int lastTwoDigits = count % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return count + " напоминаний";
        }

        switch (lastDigit) {
            case 1:
                return count + " напоминание";
            case 2:
            case 3:
            case 4:
                return count + " напоминания";
            default:
                return count + " напоминаний";
        }
    }
}