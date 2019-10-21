package io.xpire.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.xpire.commons.exceptions.IllegalValueException;
import io.xpire.model.ToBuyItem;
import io.xpire.model.item.Name;
import io.xpire.model.tag.Tag;
import io.xpire.model.tag.TagComparator;

/**
 * Jackson-friendly version of {@link ToBuyItem}.
 */
public class JsonAdaptedToBuyItem {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Item's %s field is missing!";
    private final String name;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedToBuyItem} with the given item details.
     */
    @JsonCreator
    public JsonAdaptedToBuyItem(@JsonProperty("name") String name,
                           @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code ToBuyItem} into this class for Jackson use.
     */
    public JsonAdaptedToBuyItem(ToBuyItem source) {
        this.name = source.getName().toString();
        this.tags.addAll(source
                .getTags()
                .stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted item object into the model's {@code ToBuyItem} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted item.
     */
    public ToBuyItem toModelType() throws IllegalValueException {

        if (this.name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(this.name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(this.name);

        final List<Tag> itemTags = new ArrayList<>();
        for (JsonAdaptedTag tag : this.tags) {
            itemTags.add(tag.toModelType());
        }
        final Set<Tag> modelTags = new TreeSet<>(new TagComparator());
        modelTags.addAll(itemTags);

        ToBuyItem item = new ToBuyItem(modelName, modelTags);
        return item;
    }
}
