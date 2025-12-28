package com.example.paymind;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.paymind.models.Subscription;
import com.example.paymind.repositories.SubscriptionRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

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
        subscriptionRepository = new SubscriptionRepository(dbHelper);

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
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        paymentsList = view.findViewById(R.id.payments_list);

        totalAmountView = view.findViewById(R.id.total_amount);
        remindersCountView = view.findViewById(R.id.reminders_count);
        fabAddSubscription = view.findViewById(R.id.fabAddSubscription);

        refreshSubscriptionsList();

        fabAddSubscription.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddSubscriptionActivity.class);
            addSubscriptionLauncher.launch(intent);
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void refreshSubscriptionsList() {
        paymentsList.removeAllViews();

        List<Subscription> allSubscriptions = subscriptionRepository.getAllSubscriptions();

        int totalAmount = 0;
        int remindersCount = allSubscriptions.size();

        for (int i = 0; i < remindersCount; i++) {
            Subscription subscription = allSubscriptions.get(i);

            totalAmount += (int) subscription.getCost();

            View cardView = createSubscriptionCard(subscription);
            paymentsList.addView(cardView);
        }

        totalAmountView.setText(String.format("%d ₽", totalAmount));
        remindersCountView.setText(getRemindersText(remindersCount));
    }

    private View createSubscriptionCard(Subscription subscription) {
        View cardView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_subscription_card, paymentsList, false);

        TextView tvServiceName = cardView.findViewById(R.id.tvServiceName);
        TextView tvServiceType = cardView.findViewById(R.id.tvServiceType);
        TextView tvStatus = cardView.findViewById(R.id.tvStatus);
        TextView tvDate = cardView.findViewById(R.id.tvDate);
        TextView tvAmount = cardView.findViewById(R.id.tvAmount);
        ImageView ivCalendar = cardView.findViewById(R.id.ivCalendar);

        tvServiceName.setText(subscription.getName());
        tvServiceType.setText(subscription.getCategoryName());

        String statusText = subscription.getDisplayStatus();
        tvStatus.setText(statusText);

        if (subscription.isPending()) {
            tvStatus.setBackgroundResource(R.drawable.status_background_orange);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
        } else {
            tvStatus.setBackgroundResource(R.drawable.status_background_green);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        }

        String formattedDate = formatDate(subscription.getRenewalDate());
        tvDate.setText(formattedDate);

        String currencySymbol = subscription.getCurrencySymbol();
        tvAmount.setText(String.format(Locale.getDefault(), "%.0f %s",
                subscription.getCost(),
                currencySymbol
        ));

        ivCalendar.setImageResource(R.drawable.calendar);

        return cardView;
    }

    private String formatDate(long timestamp) {
        if (timestamp == 0) return "Дата не указана";

        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));
        return sdf.format(date);
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