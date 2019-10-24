package io.xpire.testutil;

import io.xpire.model.ListView;
import io.xpire.model.item.XpireItem;

/**
 * A utility class to help with building ExpiryDateTracker objects.
 * Example usage: <br>
 *     {@code ExpiryDateTracker edt = new ExpiryDateTrackerBuilder().withItem("Fruit Jam").build();}
 */
public class ExpiryDateTrackerBuilder {

    private ListView xpire;

    public ExpiryDateTrackerBuilder() {
        xpire = new ListView();
    }

    public ExpiryDateTrackerBuilder(ListView xpire) {
        this.xpire = xpire;
    }

    /**
     * Adds a new {@code Item} to the {@code ExpiryDateTracker} that we are building.
     */
    public ExpiryDateTrackerBuilder withItem(XpireItem xpireItem) {
        xpire.addItem(xpireItem);
        return this;
    }

    public ListView build() {
        return xpire;
    }
}
