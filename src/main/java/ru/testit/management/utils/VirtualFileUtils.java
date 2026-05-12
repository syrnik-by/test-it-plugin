package ru.testit.management.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import ru.testit.management.parsers.models.FileInfo;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.parsers.models.ReplacementInfo;
import ru.testit.management.windows.tools.CheckBoxNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class VirtualFileUtils {
    public static final Set<VirtualFile> projectJavaFiles = new HashSet<>();

    public static void refresh(Project project) {
        long startTime = System.currentTimeMillis();
        projectJavaFiles.clear();

        ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () ->
            projectJavaFiles.addAll(
                FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.projectScope(project))
            )
        );

        long endTime = System.currentTimeMillis();
        System.out.println("Затраченное на refresh index время: " + (endTime - startTime) + " мс");
    }

    public static Map<String, FileInfo> replaceMatches(
            DefaultMutableTreeNode root,
            boolean replaceAll,
            Map<String, List<MatchInfo>> results) {
        Map<String, FileInfo> allUpdateFiles = new LinkedHashMap<>();

        Collections.list(root.children()).forEach(fileNode ->
            allUpdateFiles.putAll(replaceInFile((DefaultMutableTreeNode) fileNode, replaceAll, results))
        );

        return allUpdateFiles;
    }

    private static Map<String, FileInfo> replaceInFile(
            DefaultMutableTreeNode fileNode,
            boolean replaceAll,
            Map<String, List<MatchInfo>> results) {
        Map<String, FileInfo> updateFiles = new LinkedHashMap<>();
        String filePath = fileNode.getUserObject().toString();
        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (virtualFile == null) return updateFiles;

        var document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document == null) return updateFiles;

        String originalText = document.getText();
        StringBuilder textBuilder = new StringBuilder(originalText);
        List<AbstractMap.SimpleEntry<int[], String>> replacements = new ArrayList<>();
        List<MatchInfo> matchesInfos = new ArrayList<>();
        List<ReplacementInfo> replacementInfos = new ArrayList<>();
        int[] offsetAdjustment = {0};

        Collections.list(fileNode.children()).forEach(matchNodeObj -> {
            DefaultMutableTreeNode matchNode = (DefaultMutableTreeNode) matchNodeObj;
            CheckBoxNode checkBoxNode;
            if (matchNode.getUserObject() instanceof CheckBoxNode) {
                checkBoxNode = (CheckBoxNode) matchNode.getUserObject();
            } else {
                checkBoxNode = null;
            }
            if (checkBoxNode == null) return;
            int index = fileNode.getIndex(matchNode);

            if (!replaceAll && !checkBoxNode.isSelected()) return;

            List<MatchInfo> fileResults = results.get(filePath);
            if (fileResults == null || index >= fileResults.size()) return;
            MatchInfo matchInfo = fileResults.get(index);

            int start = matchInfo.getStart();
            int end = matchInfo.getEnd();
            int adjustedStart = start + offsetAdjustment[0];

            if (start >= 0 && end <= textBuilder.length()) {
                String originalMatch = textBuilder.substring(start, end);
                String replacement = ParsingAnnotationsUtils.parse(originalMatch, matchInfo);
                offsetAdjustment[0] += replacement.length() - (end - start);

                matchesInfos.add(matchInfo);
                replacementInfos.add(new ReplacementInfo(
                    replacement, adjustedStart, adjustedStart + replacement.length(), filePath
                ));
                replacements.add(new AbstractMap.SimpleEntry<>(new int[]{start, end}, replacement));
            }
        });

        replacements.sort((a, b) -> Integer.compare(b.getKey()[0], a.getKey()[0]));
        for (var entry : replacements) {
            textBuilder.replace(entry.getKey()[0], entry.getKey()[1], entry.getValue());
        }

        if (!replacements.isEmpty()) {
            updateFiles.put(filePath, new FileInfo(filePath, originalText, textBuilder.toString(), matchesInfos, replacementInfos));
        }

        return updateFiles;
    }

    public static void performReplacements(Project project, Map<String, FileInfo> replacements) {
        WriteCommandAction.runWriteCommandAction(project, () ->
            replacements.forEach((filePath, fileInfo) -> {
                VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
                if (vf != null) {
                    var doc = FileDocumentManager.getInstance().getDocument(vf);
                    if (doc != null) {
                        doc.setText(fileInfo.getNewText());
                        FileDocumentManager.getInstance().saveDocument(doc);
                    }
                }
            })
        );
    }

    public static void openFileAtMatch(Project project, String filePath, MatchInfo match) {
        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + filePath);
        if (virtualFile == null) return;

        if (match.getLineNumber() != -1) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, match.getLineNumber(), match.getColumn());
            descriptor.navigate(true);
        } else {
            Messages.showInfoMessage(project, "Match not found in file.", "Info");
        }
    }
}
