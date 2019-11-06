package io.xpire.model.item;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import io.xpire.commons.util.AppUtil;
import io.xpire.commons.util.DateUtil;

/**
 * Represents an XpireItem's expiry date in xpire.
 * Guarantees: immutable; is valid as declared in {@link #isValidFormatExpiryDate(String)} (String)}
 */
public class ExpiryDate {
    public static final String DATE_FORMAT = "d/M/yyyy";
    public static final String MESSAGE_CONSTRAINTS_LOWER = "Only expiry dates that have not yet passed are accepted";
    public static final String MESSAGE_CONSTRAINTS_OUTDATED = "This expiry date is too outdated.";
    public static final String MESSAGE_CONSTRAINTS_UPPER = "Only expiry dates strictly within 100 years are accepted";

    public static final String MESSAGE_CONSTRAINTS_FORMAT =
            "Expiry dates should only contain numbers, in the format " + DATE_FORMAT;
    private static final String EXPIRED = "Expired!";
    private static final String DAYS_LEFT = "%d day%s left";
    private static final LocalDate CENTURY_LATER = DateUtil.getCurrentDate().plusYears(100);
    private final LocalDate date;


    /**
     * Constructs a {@code ExpiryDate}.
     *
     * @param expiryDate A valid expiryDate.
     */
    public ExpiryDate(String expiryDate) {
        requireNonNull(expiryDate);
        AppUtil.checkArgument(isValidFormatExpiryDate(expiryDate), MESSAGE_CONSTRAINTS_FORMAT);
        this.date = DateUtil.convertStringToDate(expiryDate, DATE_FORMAT);
    }

    /**
     * Returns true if a given string is a valid expiry date with format d/M/yyyy.
     */
    public static boolean isValidFormatExpiryDate(String date) {
        return DateUtil.convertStringToDate(date, DATE_FORMAT) != null;
    }

    //@@author febee99
    /**
     * Returns true if a given string is a valid expiry date that has not yet passed.
     */
    public static boolean isValidLowerRangeExpiryDate(String date) {
        LocalDate d = DateUtil.convertStringToDate(date, DATE_FORMAT);
        return d.isAfter(DateUtil.getCurrentDate());
    }

    //@@author xiaoyu-nus
    /**
     * Returns true if a given string is a valid expiry date within a hundred years.
     */
    public static boolean isValidUpperRangeExpiryDate(String date) {
        LocalDate d = DateUtil.convertStringToDate(date, DATE_FORMAT);
        return d.isBefore(CENTURY_LATER);
    }

    /**
     * Checks if an item is expired.
     * @return true if item is expired; false otherwise.
     */
    public boolean isExpired() {
        return Long.parseLong(getStatus()) <= 0;
    }


    public String getStatus(LocalDate current) {
        long offset = DateUtil.getOffsetDays(current, this.date);
        return offset > 0 ? String.format(DAYS_LEFT, offset, offset == 1 ? "" : "s") : EXPIRED;
    }

    public String getStatus() {
        long offset = DateUtil.getOffsetDays(DateUtil.getCurrentDate(), this.date);
        return String.valueOf(offset);
    }

    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return DateUtil.convertDateToString(this.date, DATE_FORMAT);
    }

    public String toStringWithCountdown() {
        return String.format("%s (%s)", this.toString(), this.getStatus(DateUtil.getCurrentDate()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ExpiryDate)) {
            return false;
        } else {
            ExpiryDate other = (ExpiryDate) obj;
            return this.date.equals(other.date);
        }
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
