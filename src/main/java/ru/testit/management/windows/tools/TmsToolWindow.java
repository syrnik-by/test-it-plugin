package ru.testit.management.windows.tools;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import org.jdesktop.swingx.JXTree;
import ru.testit.kotlin.client.models.SectionModel;
import ru.testit.kotlin.client.models.WorkItemEntityTypes;
import ru.testit.management.clients.TmsClient;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.utils.MessagesUtils;
import ru.testit.management.windows.differs.FileDiffWindow;
import ru.testit.management.windows.settings.TmsSettingsState;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TmsToolWindow extends SimpleToolWindowPanel {
    private static final TmsToolWindow INSTANCE = new TmsToolWindow();

    public static TmsToolWindow getInstance() { return INSTANCE; }

    private final TmsSettingsState state = TmsSettingsState.getInstance();
    private final TmsClient client = new TmsClient(state.url);
    private Component tree = null;
    private Component search = null;

    private TmsToolWindow() {
        super(true, true);
        showActionsToolbar();
    }

    public void refresh(Project project) {
        if (tree != null) remove(tree);
        if (search != null) remove(search);
        Component label = getSpinnerForRefresh();
        add(label);
        showTree(label, project);
    }

    public void research(Project project, Map<String, List<MatchInfo>> results) {
        if (tree != null) remove(tree);
        if (search != null) remove(search);
        Component label = getSpinnerForResearch();
        add(label);
        showSearchTree(label, project, results);
    }

    private void showActionsToolbar() {
        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(am.getAction("ru.testit.management.SyncProjectAction"));
        group.add(am.getAction("ru.testit.management.OpenSettingsAction"));
        group.add(am.getAction("ru.testit.management.SearchAllureAction"));
        var toolbar = am.createActionToolbar(ActionPlaces.TOOLBAR, group, true);
        toolbar.setTargetComponent(this);
        ApplicationManager.getApplication().invokeLater(() -> setToolbar(toolbar.getComponent()));
    }

    private Component getSpinnerForRefresh() {
        JBLabel label = new JBLabel();
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(MessagesUtils.get("window.tool.spinner.text"));
        return label;
    }

    private Component getSpinnerForResearch() {
        JBLabel label = new JBLabel();
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(MessagesUtils.get("window.tool.spinner.search.text"));
        return label;
    }

    private void showTree(Component label, Project project) {
        Object oldStyle = UIManager.get("Tree.rendererFillBackground");
        try {
            UIManager.put("Tree.rendererFillBackground", false);
            tree = new JBScrollPane(buildTree(project));
            remove(label);
            add(tree);
        } finally {
            UIManager.put("Tree.rendererFillBackground", oldStyle);
        }
    }

    private void showSearchTree(Component label, Project project, Map<String, List<MatchInfo>> results) {
        Object oldStyle = UIManager.get("Tree.rendererFillBackground");
        try {
            UIManager.put("Tree.rendererFillBackground", false);
            search = buildSearchTree(project, results);
            remove(label);
            add(search);
        } finally {
            UIManager.put("Tree.rendererFillBackground", oldStyle);
        }
    }

    private Component buildTree(Project project) {
        JXTree t = new JXTree(getRootTreeNode(project));
        t.setCellRenderer(new TmsCellStyle());
        t.addMouseListener(new TmsMouseListener(project, t));
        return t;
    }

    private Component buildSearchTree(Project project, Map<String, List<MatchInfo>> results) {
        DefaultMutableTreeNode root = getSearchTreeNode(results);
        JXTree t = new JXTree(root);
        t.setCellRenderer(new CheckBoxTreeCellRenderer());
        t.addMouseListener(new SearchAllureMouseListener(project, t, results));

        JButton replaceSelected = new JButton("Replace selected");
        JButton replaceAll = new JButton("Replace all");
        replaceSelected.addActionListener(e -> FileDiffWindow.getInstance().show(project, root, false, results));
        replaceAll.addActionListener(e -> FileDiffWindow.getInstance().show(project, root, true, results));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(replaceSelected);
        buttonPanel.add(replaceAll);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JBScrollPane(t), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private DefaultMutableTreeNode getRootTreeNode(Project project) {
        Iterable<SectionModel> sections = client.getSections();
        SectionModel rootSection = null;
        for (SectionModel s : sections) {
            if (s.getParentId() == null) { rootSection = s; break; }
        }
        return buildChildNode(rootSection, project, sections);
    }

    private DefaultMutableTreeNode buildChildNode(SectionModel parentSection, Project project, Iterable<SectionModel> sections) {
        if (parentSection == null) return null;
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new TmsNodeModel(parentSection.getName(), null));
        for (SectionModel section : sections) {
            if (parentSection.getId().equals(section.getParentId())) {
                node.add(buildChildNode(section, project, sections));
            }
        }
        for (var workItem : client.getWorkItemsBySectionId(parentSection.getId())) {
            TmsNodeModel model = new TmsNodeModel(
                    workItem.getName(), workItem.getGlobalId(),
                    null, null, null,
                    WorkItemEntityTypes.valueOf(workItem.getEntityTypeName()),
                    workItem.getIsAutomated(), workItem.getId()
            );
            node.add(new DefaultMutableTreeNode(model));
        }
        return node;
    }

    private DefaultMutableTreeNode getSearchTreeNode(Map<String, List<MatchInfo>> results) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Allure results");
        for (Map.Entry<String, List<MatchInfo>> entry : results.entrySet()) {
            String filePath = entry.getKey();
            List<MatchInfo> matches = new ArrayList<>(entry.getValue());
            matches.sort(Comparator.comparingInt(MatchInfo::getStart));
            results.put(filePath, matches);
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(filePath);
            for (MatchInfo match : matches) {
                fileNode.add(new DefaultMutableTreeNode(
                        new CheckBoxNode(match.getText() + " (line " + (match.getLineNumber() + 1) + ")")));
            }
            root.add(fileNode);
        }
        return root;
    }
}
