package ru.testit.management.windows.differs;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import ru.testit.management.parsers.models.FileInfo;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.utils.SearchAllureUtils;
import ru.testit.management.utils.VirtualFileUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class FileDiffWindow {
    private static final FileDiffWindow INSTANCE = new FileDiffWindow();

    public static FileDiffWindow getInstance() { return INSTANCE; }

    private final TextAttributes addedAttributes;
    private final TextAttributes deletedAttributes;

    private FileDiffWindow() {
        addedAttributes = new TextAttributes();
        addedAttributes.setBackgroundColor(JBColor.GREEN);
        addedAttributes.setFontType(Font.BOLD);

        deletedAttributes = new TextAttributes();
        deletedAttributes.setBackgroundColor(JBColor.RED);
        deletedAttributes.setFontType(Font.BOLD);
        deletedAttributes.setEffectType(EffectType.STRIKEOUT);
        deletedAttributes.setEffectColor(JBColor.BLACK);
    }

    public void show(Project project, DefaultMutableTreeNode root, boolean replaceAll, Map<String, List<MatchInfo>> results) {
        Map<String, FileInfo> replacementFiles = VirtualFileUtils.replaceMatches(root, replaceAll, results);

        if (replacementFiles.isEmpty()) {
            Messages.showInfoMessage(project, "No changes to preview.", "Info");
            return;
        }

        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        replacementFiles.keySet().forEach(fileListModel::addElement);

        JList<String> fileList = new JList<>(fileListModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setSelectedIndex(0);

        EditorFactory editorFactory = EditorFactory.getInstance();
        JPanel editorPanel = new JPanel(new BorderLayout());

        EditorEx[] originalEditor = {null};
        EditorEx[] modifiedEditor = {null};

        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = fileList.getSelectedValue();
                if (selected != null && replacementFiles.containsKey(selected)) {
                    updateEditors(project, editorFactory, editorPanel, originalEditor, modifiedEditor,
                            replacementFiles.get(selected));
                }
            }
        });

        if (fileListModel.size() > 0) {
            FileInfo first = replacementFiles.get(fileListModel.getElementAt(0));
            if (first != null) updateEditors(project, editorFactory, editorPanel, originalEditor, modifiedEditor, first);
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(fileList));
        splitPane.setRightComponent(editorPanel);
        splitPane.setDividerLocation(200);

        JButton confirmButton = new JButton("Replace");

        DialogWrapper dialog = new DialogWrapper(project) {
            {
                init();
                setTitle("Preview Changes");
            }

            @Override
            protected @Nullable JComponent createCenterPanel() {
                JPanel mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(splitPane, BorderLayout.CENTER);
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(confirmButton);
                mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                return mainPanel;
            }

            @Override
            protected Action[] createActions() { return new Action[0]; }

            @Override
            public Dimension getPreferredSize() { return new Dimension(800, 600); }

            @Override
            public void dispose() {
                if (originalEditor[0] != null) editorFactory.releaseEditor(originalEditor[0]);
                if (modifiedEditor[0] != null) editorFactory.releaseEditor(modifiedEditor[0]);
                super.dispose();
            }
        };

        confirmButton.addActionListener(e -> {
            VirtualFileUtils.performReplacements(project, replacementFiles);
            SearchAllureUtils.research();
            dialog.close(DialogWrapper.OK_EXIT_CODE);
        });

        dialog.show();
    }

    private void updateEditors(Project project, EditorFactory editorFactory, JPanel editorPanel,
                               EditorEx[] originalEditor, EditorEx[] modifiedEditor, FileInfo file) {
        if (originalEditor[0] != null) editorFactory.releaseEditor(originalEditor[0]);
        if (modifiedEditor[0] != null) editorFactory.releaseEditor(modifiedEditor[0]);

        originalEditor[0] = (EditorEx) editorFactory.createEditor(editorFactory.createDocument(file.getOldText()), project);
        modifiedEditor[0] = (EditorEx) editorFactory.createEditor(editorFactory.createDocument(file.getNewText()), project);

        for (EditorEx editor : new EditorEx[]{originalEditor[0], modifiedEditor[0]}) {
            editor.setViewer(true);
            editor.setVerticalScrollbarVisible(true);
            editor.getSettings().setLineNumbersShown(true);
            editor.getSettings().setFoldingOutlineShown(false);
            editor.getSettings().setLineMarkerAreaShown(true);
            editor.getSettings().setIndentGuidesShown(true);
        }

        highlightChanges(originalEditor[0], modifiedEditor[0], file);

        editorPanel.removeAll();
        JBSplitter splitter = new JBSplitter(false);
        splitter.setFirstComponent(originalEditor[0].getComponent());
        splitter.setSecondComponent(modifiedEditor[0].getComponent());
        splitter.setProportion(0.5f);
        editorPanel.add(splitter, BorderLayout.CENTER);
        editorPanel.revalidate();
        editorPanel.repaint();
    }

    private void highlightChanges(EditorEx originalEditor, EditorEx modifiedEditor, FileInfo file) {
        originalEditor.getMarkupModel().removeAllHighlighters();
        modifiedEditor.getMarkupModel().removeAllHighlighters();

        for (MatchInfo match : file.getMatches()) {
            originalEditor.getMarkupModel().addRangeHighlighter(
                    match.getStart(), match.getEnd(),
                    HighlighterLayer.SELECTION - 1, deletedAttributes,
                    HighlighterTargetArea.EXACT_RANGE);
        }

        for (var replacement : file.getReplacements()) {
            modifiedEditor.getMarkupModel().addRangeHighlighter(
                    replacement.getStart(), replacement.getEnd(),
                    HighlighterLayer.SELECTION - 1, addedAttributes,
                    HighlighterTargetArea.EXACT_RANGE);
        }

        originalEditor.getScrollingModel().addVisibleAreaListener(e -> {
            Rectangle visibleArea = originalEditor.getScrollingModel().getVisibleArea();
            int centerY = visibleArea.y + visibleArea.height / 2;
            int lineNumber = originalEditor.yToVisualLine(centerY);
            int column = originalEditor.getCaretModel().getVisualPosition().column;
            modifiedEditor.getScrollingModel().scrollTo(new LogicalPosition(lineNumber, column), ScrollType.CENTER);
        });
    }
}
