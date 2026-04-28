package ru.testit.management.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class ClipboardUtils {
    public static void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }
}
