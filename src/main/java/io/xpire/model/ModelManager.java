package io.xpire.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.logging.Logger;

import io.xpire.commons.core.GuiSettings;
import io.xpire.commons.core.LogsCenter;
import io.xpire.commons.util.CollectionUtil;
import io.xpire.model.item.XpireItem;
import io.xpire.model.item.Name;
import io.xpire.model.item.sort.XpireMethodOfSorting;
import io.xpire.model.tag.Tag;
import io.xpire.model.tag.TagComparator;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Represents the in-memory model of the xpire data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final Xpire xpire;
    private final UserPrefs userPrefs;
    private final FilteredList<XpireItem> filteredXpireItems;

    /**
     * Initializes a ModelManager with the given xpire and userPrefs.
     */
    public ModelManager(ReadOnlyListView xpire, ReadOnlyUserPrefs userPrefs) {
        super();
        CollectionUtil.requireAllNonNull(xpire, userPrefs);

        logger.fine("Initializing with xpire: " + xpire + " and user prefs " + userPrefs);

        this.xpire = new Xpire(xpire);
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredXpireItems = new FilteredList<>(this.xpire.getItemList());
    }

    public ModelManager() {
        this(new Xpire(), new UserPrefs());
    }

    //=========== UserPrefs =========================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return this.userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return this.userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        this.userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getXpireFilePath() {
        return this.userPrefs.getXpireFilePath();
    }

    @Override
    public void setXpireFilePath(Path xpireFilePath) {
        requireNonNull(xpireFilePath);
        this.userPrefs.setXpireFilePath(xpireFilePath);
    }

    //=========== expiryDateTracker  ================================================================================

    @Override
    public void setXpire(ReadOnlyListView xpire) {
        this.xpire.resetData(xpire);
    }

    @Override
    public ReadOnlyListView getXpire() {
        return this.xpire;
    }

    @Override
    public boolean hasItem(XpireItem xpireItem) {
        requireNonNull(xpireItem);
        return this.xpire.hasItem(xpireItem);
    }

    @Override
    public void deleteItem(XpireItem target) {
        this.xpire.removeItem(target);
    }

    @Override
    public void addItem(XpireItem xpireItem) {
        this.xpire.addItem(xpireItem);
        updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
    }

    @Override
    public void setItem(XpireItem target, XpireItem editedXpireItem) {
        CollectionUtil.requireAllNonNull(target, editedXpireItem);
        this.xpire.setItem(target, editedXpireItem);
    }

    @Override
    public Set<Tag> getAllItemTags() {
        Set<Tag> tagSet = new TreeSet<>(new TagComparator());
        List<XpireItem> xpireItemList = this.xpire.getItemList();
        xpireItemList.forEach(item -> tagSet.addAll(item.getTags()));
        return tagSet;
    }

    @Override
    public Set<Name> getAllItemNames() {
        Set<Name> nameSet = new TreeSet<>(Comparator.comparing(Name::toString));
        List<XpireItem> xpireItemList = this.xpire.getItemList();
        xpireItemList.forEach(item -> nameSet.add(item.getName()));
        return nameSet;
    }


    //=========== Sorted Item List Accessors ========================================================================

    @Override
    public void sortItemList(XpireMethodOfSorting method) {
        requireNonNull(method);
        this.xpire.setMethodOfSorting(method);
    }

    // =========== Filtered Item List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Item} backed by the internal list of
     * {@code versionedXpire}
     */
    @Override
    public ObservableList<XpireItem> getFilteredItemList() {
        return this.filteredXpireItems;
    }

    @Override
    public void updateFilteredItemList(Predicate<XpireItem> predicate) {
        requireNonNull(predicate);
        Predicate<? super XpireItem> p = this.filteredXpireItems.getPredicate();
        if (predicate == PREDICATE_SHOW_ALL_ITEMS || p == null) {
            // a view command or first ever search command
            this.filteredXpireItems.setPredicate(predicate);
        } else {
            // search commands have been executed before
            this.filteredXpireItems.setPredicate(predicate.and(p));
        }
    }

    // =========== Tag Item List Accessors =============================================================

    @Override
    public List<XpireItem> getAllItemList() {
        return this.xpire.getItemList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ModelManager)) {
            return false;
        } else {
            ModelManager other = (ModelManager) obj;
            return this.xpire.equals(other.xpire)
                    && this.userPrefs.equals(other.userPrefs)
                    && this.filteredXpireItems.equals(other.filteredXpireItems);
        }
    }
}
