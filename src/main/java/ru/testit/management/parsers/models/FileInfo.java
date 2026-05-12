package ru.testit.management.parsers.models;

import java.util.List;

public class FileInfo {
    private final String filePath;
    private final String oldText;
    private final String newText;
    private final List<MatchInfo> matches;
    private final List<ReplacementInfo> replacements;

    public FileInfo(String filePath, String oldText, String newText,
                    List<MatchInfo> matches, List<ReplacementInfo> replacements) {
        this.filePath = filePath;
        this.oldText = oldText;
        this.newText = newText;
        this.matches = matches;
        this.replacements = replacements;
    }

    public String getFilePath() { return filePath; }
    public String getOldText() { return oldText; }
    public String getNewText() { return newText; }
    public List<MatchInfo> getMatches() { return matches; }
    public List<ReplacementInfo> getReplacements() { return replacements; }
}
