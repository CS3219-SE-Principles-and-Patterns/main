package io.xpire.model.item;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;

import io.xpire.commons.util.CollectionUtil;
import io.xpire.model.item.exceptions.DuplicateItemException;
import io.xpire.model.item.exceptions.ItemNotFoundException;
import io.xpire.model.item.sort.MethodOfSorting;
import io.xpire.model.item.sort.SortingMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;


/**
 * A list of items that enforces uniqueness between its elements and does not allow nulls.
 * An item is considered unique by comparing using {@code Item#isSameItem(Item)}. As such, adding and updating of
 * items uses Item#isSameItem(Item) for equality so as to ensure that the item being added or updated is
 * unique in terms of identity in the SortedUniqueItemList. However, the removal of a item uses Item#equals(Object) so
 * as to ensure that the item with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see XpireItem#isSameItem(XpireItem)
 */
public class SortedUniqueItemList implements SortedUniqueList<XpireItem> {

    private final ObservableList<XpireItem> internalList = FXCollections.observableArrayList();
    private SortingMethod<XpireItem> methodOfSorting = new MethodOfSorting("name");
    private final SortedList<XpireItem> sortedInternalList = new SortedList<>(internalList, methodOfSorting.getComparator());
    private final ObservableList<XpireItem> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(this.sortedInternalList);



    /**
     * Returns true if the list contains an equivalent item as the given argument.
     */
    @Override
    public boolean contains(XpireItem toCheck) {
        requireNonNull(toCheck);
        return this.internalList.stream().anyMatch(toCheck::isSameItem);
    }

    /**
     * Adds an item to the list.
     * The item must not already exist in the list.
     */
    @Override
    public void add(XpireItem toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateItemException();
        }
        this.internalList.add(toAdd);
        methodOfSorting = new MethodOfSorting("name");
    }

    /**
     * Replaces the item { @code target} in the list with {@code editedItem}.
     * {@code target} must exist in the list.
     * The item identity of {@code editedItem} must not be the same as another existing item in the list.
     */
    @Override
    public void setItem(XpireItem target, XpireItem editedXpireItem) {
        CollectionUtil.requireAllNonNull(target, editedXpireItem);

        int index = this.internalList.indexOf(target);
        if (index == -1) {
            throw new ItemNotFoundException();
        }

        if (!target.isSameItem(editedXpireItem) && contains(editedXpireItem)) {
            throw new DuplicateItemException();
        }

        this.internalList.set(index, editedXpireItem);
    }

    /**
     * Removes the equivalent item from the list.
     * The item must exist in the list.
     */
    public void remove(XpireItem toRemove) {
        requireNonNull(toRemove);
        if (!this.internalList.remove(toRemove)) {
            throw new ItemNotFoundException();
        }
    }

    @Override
    public void setItems(SortedUniqueList<XpireItem> replacement) {
        requireNonNull(replacement);
        SortedUniqueItemList replacementList = (SortedUniqueItemList) replacement;
        this.internalList.setAll(replacementList.sortedInternalList);
    }

    /**
     * Replaces the contents of this list with {@code items}.
     * {@code items} must not contain duplicate items.
     */
    @Override
    public void setItems(List<XpireItem> xpireItems) {
        CollectionUtil.requireAllNonNull(xpireItems);
        if (!itemsAreUnique(xpireItems)) {
            throw new DuplicateItemException();
        }
        this.internalList.setAll(xpireItems);
    }

    /**
     * Set method of sorting.
     */
    @Override
    public void setMethodOfSorting(SortingMethod<XpireItem> method) {
        this.methodOfSorting = method;
        this.sortedInternalList.setComparator(methodOfSorting.getComparator());
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    @Override
    public ObservableList<XpireItem> asUnmodifiableObservableList() {
        return this.internalUnmodifiableList;
    }

    @Override
    public Iterator<XpireItem> iterator() {
        return this.sortedInternalList.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof SortedUniqueItemList)) {
            return false;
        } else {
            SortedUniqueItemList other = (SortedUniqueItemList) obj;
            return this.internalUnmodifiableList.equals(other.internalUnmodifiableList);
        }
    }

    @Override
    public int hashCode() {
        return this.internalUnmodifiableList.hashCode();
    }

    /**
     * Returns true if {@code items} contains only unique items.
     */
    private boolean itemsAreUnique(List<XpireItem> xpireItems) {
        return xpireItems.size() == xpireItems.stream().distinct().count();
    }
}
