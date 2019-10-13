package io.xpire.model.item;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import io.xpire.commons.util.CollectionUtil;
import io.xpire.commons.util.DateUtil;
import io.xpire.model.tag.Tag;
import io.xpire.model.tag.TagComparator;

/**
 * Represents a Item in Xpire.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Item {
    // Identity fields
    private final Name name;
    private final ExpiryDate expiryDate;

    // Data fields
    private Set<Tag> tags = new TreeSet<>(new TagComparator());
    private ReminderThreshold reminderThreshold = new ReminderThreshold("0");

    /**
     * Every field must be present and not null.
     */
    public Item(Name name, ExpiryDate expiryDate, Set<Tag> tags) {
        CollectionUtil.requireAllNonNull(name, expiryDate, tags);
        this.name = name;
        this.expiryDate = expiryDate;
        this.tags.addAll(tags);
    }

    /**
     * Every field must be present and not null.
     * Tags are optional.
     */
    public Item(Name name, ExpiryDate expiryDate) {
        CollectionUtil.requireAllNonNull(name, expiryDate);
        this.name = name;
        this.expiryDate = expiryDate;
    }

    public Name getName() {
        return this.name;
    }

    public ExpiryDate getExpiryDate() {
        return this.expiryDate;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(this.tags);
    }

    /**
     * Sets and overrides the tags.
     *
     * @param tags tags.
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Returns the reminder threshold.
     *
     * @return {@Code ReminderThreshold} object.
     */
    public ReminderThreshold getReminderThreshold() {
        return this.reminderThreshold;
    }

    /**
     * Sets and overrides the reminder threshold.
     *
     * @param reminderThreshold reminder threshold.
     */
    public void setReminderThreshold(ReminderThreshold reminderThreshold) {
        this.reminderThreshold = reminderThreshold;
    }

    /**
     * Returns true if both items of the same name have at least one other identity field that is the same.
     * This defines a weaker notion of equality between two items.
     */
    public boolean isSameItem(Item other) {
        if (other == this) {
            return true;
        } else {
            return other != null
                    && this.name.equals(other.name)
                    && this.expiryDate.equals(other.expiryDate);
        }
    }

    /**
     * Returns true if both items have the same identity and data fields.
     * This defines a stronger notion of equality between two items.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof Item)) {
            return false;
        } else {
            Item other = (Item) obj;
            return this.name.equals(other.name)
                    && this.expiryDate.equals(other.expiryDate)
                    && this.tags.equals(other.tags)
                    && this.reminderThreshold.equals(other.reminderThreshold);
        }
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(this.name, this.expiryDate, this.tags, this.reminderThreshold);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (!this.getTags().isEmpty()) {
            builder.append(this.name).append("\n")
                    .append(String.format("Expiry date: %s (%s)\n",
                            this.expiryDate, this.expiryDate.getStatus(DateUtil.getCurrentDate())))
                    .append("Tags: ");
        } else {
            builder.append(this.name).append("\n")
                    .append(String.format("Expiry date: %s (%s)\n",
                            this.expiryDate, this.expiryDate.getStatus(DateUtil.getCurrentDate())));
        }
        this.getTags().forEach(builder::append);
        return builder.toString();
    }
}
