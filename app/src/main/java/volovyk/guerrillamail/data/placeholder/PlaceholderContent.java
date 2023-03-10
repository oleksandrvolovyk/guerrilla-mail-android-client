package volovyk.guerrillamail.data.placeholder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import volovyk.guerrillamail.data.model.Email;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<Email> ITEMS = new ArrayList<>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, Email> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(Email item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getFrom(), item);
    }

    private static Email createPlaceholderItem(int position) {
        return new Email("example"+ position +"@example.com",
                "Email " + position + " subject",
                "Body " + position,
                "now");
    }

}