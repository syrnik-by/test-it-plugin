package ru.testit.management.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.BaseProjectDirectories;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.windows.tools.TmsToolWindow;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAllureUtils {
    private static volatile boolean isResearching = false;
    private static final Logger logger = Logger.getLogger(SearchAllureUtils.class.getSimpleName());

    public static void research() {
        synchronized (SearchAllureUtils.class) {
            if (isResearching) return;
            isResearching = true;
        }

        ApplicationManager.getApplication().runReadAction(() -> {
            try {
//                var project = DataManager.getInstance()
//                        .getDataContextFromFocusAsync()
//                        .then(ctx -> PlatformDataKeys.PROJECT.getData(ctx))
//                        .blockingGet(3, TimeUnit.SECONDS);

                for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                    List<Pattern> regexPatterns = ParsingAnnotationsUtils.getAllPatterns();
                    if (regexPatterns.isEmpty()) return;
                    Map<String, List<MatchInfo>> currentResults = getResults(project, regexPatterns);

                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Test IT");
                    if (toolWindow == null) continue;
                    Content content = toolWindow.getContentManager().getContent(0);
                    if (content == null) continue;
                    TmsToolWindow window = (TmsToolWindow) content.getComponent();
                    window.research(project, currentResults);
                }
            } catch (Throwable e) {
                logger.severe(e.getMessage());
            } finally {
                isResearching = false;
            }
        });
    }

    private static Map<String, List<MatchInfo>> getResults(Project project, List<Pattern> patterns) {
        Map<String, List<MatchInfo>> results = new LinkedHashMap<>();
        for (Pattern pattern : patterns) {
            for (VirtualFile root : BaseProjectDirectories.getBaseDirectories(project)) {
                Map<String, List<MatchInfo>> matches = searchFiles(root, pattern);
                for (Map.Entry<String, List<MatchInfo>> entry : matches.entrySet()) {
                    results.merge(entry.getKey(), entry.getValue(), (existing, added) -> {
                        List<MatchInfo> joined = new ArrayList<>(existing);
                        joined.addAll(added);
                        return joined;
                    });
                }
            }
        }
        return results;
    }

    private static Map<String, List<MatchInfo>> searchFiles(VirtualFile file, Pattern pattern) {
        Map<String, List<MatchInfo>> results = new LinkedHashMap<>();
        if (file.isDirectory()) {
            for (VirtualFile child : file.getChildren()) {
                results.putAll(searchFiles(child, pattern));
            }
        } else {
            try {
                var doc = FileDocumentManager.getInstance().getDocument(file);
                if (doc == null) return results;
                String content = doc.getText();
                Matcher matcher = pattern.matcher(content);
                List<MatchInfo> matchList = new ArrayList<>();
                while (matcher.find()) {
                    int[] lc = calculateLineAndColumn(content, matcher.start());
                    matchList.add(new MatchInfo(
                            matcher.group(), matcher.start(), matcher.end(),
                            lc[0], lc[1], file.getPath()
                    ));
                }
                if (!matchList.isEmpty()) results.put(file.getPath(), matchList);
            } catch (Exception e) {
                System.out.println("Error processing file " + file.getPath() + ": " + e.getMessage());
            }
        }
        return results;
    }

    private static int[] calculateLineAndColumn(String content, int position) {
        if (position < 0 || position > content.length()) return new int[]{-1, -1};
        String before = content.substring(0, position);
        int lineNumber = (int) before.chars().filter(c -> c == '\n').count();
        int lastNl = before.lastIndexOf('\n');
        int column = (lastNl == -1) ? position + 1 : position - lastNl;
        return new int[]{lineNumber, column};
    }
}
