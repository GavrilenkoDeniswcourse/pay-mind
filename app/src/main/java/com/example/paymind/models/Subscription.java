package com.example.paymind.models;

import com.example.paymind.enums.CategoryType;
import com.example.paymind.enums.CurrencyType;
import com.example.paymind.enums.PeriodType;
import com.example.paymind.enums.StatusType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Subscription {
    private int id;
    private String name;
    private int categoryId;
    private double cost;
    private int currencyId;
    private long startDate;
    private long renewalDate;
    private int periodTypeId;
    private int periodValue;
    private boolean autoRenew;
    private String notes;
    private int statusId;
    private int reminderDaysBefore;
    private int typeId;
    private long createdAt;
    private long updatedAt;

    private String categoryName;
    private String statusName;
    private String currencySymbol;
    private String periodTypeName;
    private String typeName;

    public Subscription() {
    }

    public Subscription(int id, String name, int categoryId, double cost,
                        int currencyId, long startDate, long renewalDate,
                        int periodTypeId, int periodValue, boolean autoRenew,
                        String notes, int statusId, int reminderDaysBefore,
                        int typeId, long createdAt, long updatedAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.cost = cost;
        this.currencyId = currencyId;
        this.startDate = startDate;
        this.renewalDate = renewalDate;
        this.periodTypeId = periodTypeId;
        this.periodValue = periodValue;
        this.autoRenew = autoRenew;
        this.notes = notes;
        this.statusId = statusId;
        this.reminderDaysBefore = reminderDaysBefore;
        this.typeId = typeId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(long renewalDate) {
        this.renewalDate = renewalDate;
    }

    public int getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(int periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public int getPeriodValue() {
        return periodValue;
    }

    public void setPeriodValue(int periodValue) {
        this.periodValue = periodValue;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(int reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCategoryName() {
        return this.getCategory().getName();
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatusName() {
        return this.getStatus().getName();
    }

    public void setStatusName(String statusName) {
        this.statusName = this.getStatusName();
    }

    public String getCurrencySymbol() {
        return this.getCurrency().getSymbol();
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getPeriodTypeName() {
        return this.getPeriodType().getName();
    }

    public void setPeriodTypeName(String periodTypeName) {
        this.periodTypeName = periodTypeName;
    }

    public String getFormattedCost() {
        String symbol = (currencySymbol != null) ? currencySymbol : "₽";
        String period = getPeriodDisplay();
        return String.format(Locale.getDefault(), "%.0f %s%s", cost, symbol, period);
    }

    public String getPeriodDisplay() {
        if (periodValue == 1){
            return "/" + getPeriodTypeShortName();
        }
        else {
            return "/" + periodValue + " " + getPeriodTypeShortName();
        }
    }

    private String getPeriodTypeShortName() {
        if (periodTypeName == null) return "";

        switch (periodTypeName.toLowerCase()) {
            case "день":
                return "день";
            case "неделя":
                return "нед";
            case "месяц":
                return "мес";
            case "год":
                return "год";
            default:
                return periodTypeName;
        }
    }

    public String getFormattedRenewalDate() {
        if (renewalDate == 0) return "";

        Date date = new Date(renewalDate * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public StatusType getStatus() {
        return StatusType.fromId(statusId);
    }

    public CurrencyType getCurrency() {
        return CurrencyType.fromId(currencyId);
    }

    public CategoryType getCategory() {
        return CategoryType.fromId(categoryId);
    }

    public PeriodType getPeriodType() {
        return PeriodType.fromId(periodTypeId);
    }

    public Date calculateNextPaymentDate(Date fromDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate.getTime());

        PeriodType periodType = PeriodType.fromId(periodTypeId);

        switch (periodType) {
            case DAY:
                calendar.add(Calendar.DAY_OF_YEAR, periodValue);
                break;
            case WEEK:
                calendar.add(Calendar.WEEK_OF_YEAR, periodValue);
                break;
            case MONTH:
                calendar.add(Calendar.MONTH, periodValue);
                break;
            case YEAR:
                calendar.add(Calendar.YEAR, periodValue);
                break;
        }

        return calendar.getTime();
    }

    public String getNextPaymentDateFormatted() {
        Date nextPayment = calculateNextPaymentDate(new Date(startDate * 1000L));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        return sdf.format(nextPayment);
    }

    public boolean isPending() {
        return getStatus() == StatusType.WAIT;
    }

    public boolean isPaid() {
        return getStatus() == StatusType.PAID;
    }

    public String getDisplayStatus() {
        if (statusName != null) {
            return statusName;
        }

        switch (statusId) {
            case 1:
                return "Ожидает";
            case 2:
                return "Оплачено";
            case 3:
                return "Отменено";
            default:
                return "Неизвестно";
        }
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", statusId=" + statusId +
                ", renewalDate=" + getFormattedRenewalDate() +
                '}';
    }

    public String getNextPaymentDay() {
        if (renewalDate == 0) return "";

        long timeInMillis = (renewalDate < 10000000000L) ? renewalDate * 1000L : renewalDate;
        Date date = new Date(timeInMillis);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public String getPaymentScheduleText() {
        PeriodType periodType = PeriodType.fromId(periodTypeId);

        switch (periodType) {
            case DAY:
                if (periodValue == 1) {
                    return "Ежедневное списание";
                } else {
                    return "Списание каждые " + periodValue + " " + getDayPlural(periodValue);
                }

            case WEEK:
                if (periodValue == 1) {
                    return "Еженедельное списание";
                } else {
                    return "Списание каждые " + periodValue + " " + getWeekPlural(periodValue);
                }

            case MONTH:
                if (periodValue == 1) {
                    String day = getNextPaymentDay();
                    return "Списание " + day + " числа каждого месяца";
                } else {
                    return "Списание каждые " + periodValue + " " + getMonthPlural(periodValue);
                }

            case YEAR:
                if (periodValue == 1) {
                    return "Ежегодное списание";
                } else {
                    return "Списание каждые " + periodValue + " " + getYearPlural(periodValue);
                }

            default:
                return "Списание каждые " + periodValue + " " + periodType.getName().toLowerCase();
        }
    }

    private String getDayPlural(int value) {
        int lastDigit = value % 10;
        int lastTwoDigits = value % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) return "дней";
        if (lastDigit == 1) return "день";
        if (lastDigit >= 2 && lastDigit <= 4) return "дня";
        return "дней";
    }

    private String getWeekPlural(int value) {
        int lastDigit = value % 10;
        int lastTwoDigits = value % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) return "недель";
        if (lastDigit == 1) return "неделя";
        if (lastDigit >= 2 && lastDigit <= 4) return "недели";
        return "недель";
    }

    private String getMonthPlural(int value) {
        int lastDigit = value % 10;
        int lastTwoDigits = value % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) return "месяцев";
        if (lastDigit == 1) return "месяц";
        if (lastDigit >= 2 && lastDigit <= 4) return "месяца";
        return "месяцев";
    }

    private String getYearPlural(int value) {
        int lastDigit = value % 10;
        int lastTwoDigits = value % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) return "лет";
        if (lastDigit == 1) return "год";
        if (lastDigit >= 2 && lastDigit <= 4) return "года";
        return "лет";
    }


    public String getDueDateText() {
        if (renewalDate == 0) {
            return "Дата не указана";
        }

        String day = this.getNextPaymentDay();
        return "До " + day + " числа";
    }

    public String getLastPaymentText() {
        if (startDate == 0) {
            return "Нет данных о платежах";
        }

        String formattedDate = formatDate(startDate);
        return "Последний платеж: " + formattedDate;
    }

    private String formatDate(long timestamp) {
        if (timestamp == 0) return "";

        long timeInMillis = (timestamp < 10000000000L) ? timestamp * 1000L : timestamp;
        Date date = new Date(timeInMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM", new Locale("ru"));
        return sdf.format(date);
    }
}
