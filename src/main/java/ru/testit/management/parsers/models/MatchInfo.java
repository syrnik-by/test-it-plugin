package ru.testit.management.parsers.models;

public class MatchInfo {
    private final String text;
    private final int start;
    private final int end;
    private final int lineNumber;
    private final int column;
    private final String filePath;

    public MatchInfo(String text, int start, int end, int lineNumber, int column, String filePath) {
        this.text = text;
        this.start = start;
        this.end = end;
        this.lineNumber = lineNumber;
        this.column = column;
        this.filePath = filePath;
    }

    public String getText() { return text; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public int getLineNumber() { return lineNumber; }
    public int getColumn() { return column; }
    public String getFilePath() { return filePath; }
}
