package com.example.paymind;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.paymind.enums.CategoryType;
import com.example.paymind.models.Subscription;
import com.example.paymind.repositories.SubscriptionRepository;

import java.util.List;
import java.util.Locale;

public class SubscriptionsFragment extends Fragment {

    private SubscriptionRepository subscriptionRepository;
    private DBHelper dbHelper;
    private LinearLayout paymentsList;
    private TextView totalAmountView;
    private TextView remindersCountView;

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_subscriptions, container, false);

        paymentsList = view.findViewById(R.id.payments_list);
        totalAmountView = view.findViewById(R.id.total_amount);
        remindersCountView = view.findViewById(R.id.reminders_count);

        dbHelper = new DBHelper(getContext());
        subscriptionRepository = new SubscriptionRepository(dbHelper);

        List<Subscription> subscriptions = subscriptionRepository.getSubscriptionsByCategory(CategoryType.SUBSCRIPTION.getId());
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


        return view;
    }

    private View createSubscriptionCard(Subscription subscription) {
        // Загружаем шаблон карточки (нужно создать item_subscription_card_detailed.xml)
        View cardView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_subscription_detailed_card, paymentsList, false);

        // Находим элементы в карточке
        TextView tvServiceName = cardView.findViewById(R.id.tvServiceName);
        TextView tvCategory = cardView.findViewById(R.id.tvCategory);
        TextView tvStatus = cardView.findViewById(R.id.tvStatus);
        TextView tvDueDate = cardView.findViewById(R.id.tvDueDate);
        TextView tvAmount = cardView.findViewById(R.id.tvAmount);
        Button btnPay = cardView.findViewById(R.id.btnPay);
        Button btnDelete = cardView.findViewById(R.id.btnDelete);

        // Заполняем данные
        tvServiceName.setText(subscription.getName());

        // Категория (можно получить из subscription.getCategoryName())
        tvCategory.setText(subscription.getTypeName());

        // Статус
        String statusText = subscription.getDisplayStatus();
        tvStatus.setText(statusText);

        // Цвет статуса
        if (subscription.isPending()) {
            tvStatus.setBackgroundResource(R.drawable.status_background_orange);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
            btnPay.setVisibility(VISIBLE);
        } else if (subscription.isPaid()) {
            tvStatus.setBackgroundResource(R.drawable.status_background_blue);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
            btnPay.setVisibility(View.GONE);
        }

        // Сумма с символом валюты
        String currencySymbol = subscription.getCurrencySymbol();
        if (currencySymbol == null || currencySymbol.isEmpty()) {
            currencySymbol = "₽";
        }

        String periodText = subscription.getPeriodDisplay();
        tvAmount.setText(String.format(Locale.getDefault(), "%.0f %s%s",
                subscription.getCost(),
                currencySymbol,
                periodText
        ));

        tvDueDate.setText(subscription.getPaymentScheduleText());
        if (subscription.isPaid()) {
            btnPay.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnDelete.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.setMarginStart(0);
            btnDelete.setLayoutParams(params);
        } else {
            btnPay.setVisibility(View.VISIBLE);
        }

        // Обработчики кнопок
        btnPay.setOnClickListener(v -> {
//            onPayButtonClick(subscription);
        });

        btnDelete.setOnClickListener(v -> {
//            onDeleteButtonClick(subscription);
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