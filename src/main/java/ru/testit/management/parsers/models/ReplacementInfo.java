package ru.testit.management.parsers.models;

public class ReplacementInfo {
    private final String text;
    private final int start;
    private final int end;
    private final String filePath;

    public ReplacementInfo(String text, int start, int end, String filePath) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.filePath = filePath;
    }

    public String getText() { return text; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public String getFilePath() { return filePath; }
}
