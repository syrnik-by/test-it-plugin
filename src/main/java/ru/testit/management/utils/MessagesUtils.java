package ru.testit.management.utils;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

@NonNls
public class MessagesUtils extends DynamicBundle {
    private static final String BUNDLE = "messages.TmsBundle";
    private static final MessagesUtils INSTANCE = new MessagesUtils();

    private MessagesUtils() {
        super(BUNDLE);
    }

    public static String get(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
