package ru.testit.management.windows.tools;

public class CheckBoxNode {
    private final String text;
    private boolean selected;

    public CheckBoxNode(String text) {
        this.text = text;
        this.selected = false;
    }

    public CheckBoxNode(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }

    public String getText() { return text; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public String toString() { return text; }
}
