package io.xpire.model.item;

import static io.xpire.logic.CommandParserItemUtil.VALID_EXPIRY_DATE_KIWI;
import static io.xpire.logic.CommandParserItemUtil.VALID_NAME_APPLE;
import static io.xpire.logic.CommandParserItemUtil.VALID_NAME_BANANA;
import static io.xpire.logic.CommandParserItemUtil.VALID_TAG_DRINK;
import static io.xpire.testutil.Assert.assertThrows;
import static io.xpire.testutil.TypicalItems.EXPIRED_APPLE;
import static io.xpire.testutil.TypicalItems.KIWI;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.xpire.testutil.XpireItemBuilder;

public class XpireItemTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        XpireItem xpireItem = new XpireItemBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> xpireItem.getTags().remove(0));
    }

    @Test
    public void isSameItem() {
        // same object -> returns true
        assertTrue(KIWI.isSameItem(KIWI));

        // null -> returns false
        assertFalse(KIWI.isSameItem(null));

        // different expiry date -> returns false
        XpireItem editedKiwi = new XpireItemBuilder(KIWI).withExpiryDate("02/02/2020").build();
        assertFalse(KIWI.isSameItem(editedKiwi));

        // different name -> returns false
        editedKiwi = new XpireItemBuilder(KIWI).withName(VALID_NAME_APPLE).build();
        assertFalse(KIWI.isSameItem(editedKiwi));

        // same name, same expiry date , different attributes -> returns true
        editedKiwi = new XpireItemBuilder(KIWI).withExpiryDate(VALID_EXPIRY_DATE_KIWI)
                                            .withTags(VALID_TAG_DRINK).build();
        assertTrue(KIWI.isSameItem(editedKiwi));

    }

    @Test
    public void equals() {
        // same values -> returns true
        XpireItem kiwiCopy = new XpireItemBuilder(KIWI).build();
        assertTrue(KIWI.equals(kiwiCopy));

        // same object -> returns true
        assertTrue(KIWI.equals(KIWI));

        // null -> returns false
        assertFalse(KIWI.equals(null));

        // different type -> returns false
        assertFalse(KIWI.equals(5));

        // different xpireItem -> returns false
        assertFalse(KIWI.equals(EXPIRED_APPLE));

        // different name -> returns false
        XpireItem editedKiwi = new XpireItemBuilder(KIWI).withName(VALID_NAME_BANANA).build();
        assertFalse(KIWI.equals(editedKiwi));

        // different expiry date -> returns false
        editedKiwi = new XpireItemBuilder(KIWI).withExpiryDate("01/01/2020").build();
        assertFalse(KIWI.equals(editedKiwi));

        // different tags -> returns false
        //        XpireItem duck = new XpireItemBuilder(DUCK).withTags("Poultry").build();
        //        for (Tag tag: duck.getTags()) {
        //            System.out.println("the one with poultry: " + tag);
        //        }
        //        for (Tag tag: DUCK.getTags()) {
        //            System.out.println("the one without poultry: " + tag);
        //        }
        //        System.out.println(DUCK.getTags().equals(duck.getTags()));
        //        System.out.println(DUCK.equals(duck));
        //        editedKiwi = new XpireItemBuilder(KIWI).withTags(VALID_TAG_DRINK).build();
        //        for (Tag tag: editedKiwi.getTags()) {
        //            System.out.println("edited item: " + tag);
        //        }
        //        assertFalse(KIWI.equals(editedKiwi));
    }
}