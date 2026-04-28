package ru.testit.management.windows.tools;

import com.intellij.openapi.project.Project;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.utils.VirtualFileUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SearchAllureMouseListener implements MouseListener {
    private final Project project;
    private final JTree tree;
    private final Map<String, List<MatchInfo>> results;

    public SearchAllureMouseListener(Project project, JTree tree, Map<String, List<MatchInfo>> results) {
        this.project = project;
        this.tree = tree;
        this.results = results;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        TreePath path = tree.getPathForLocation(event.getX(), event.getY());
        if (path == null) return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node == null) return;

        Object userObject = node.getUserObject();

        if (userObject instanceof CheckBoxNode checkBoxNode) {
            checkBoxNode.setSelected(!checkBoxNode.isSelected());
            tree.repaint();
        }

        if (event.getClickCount() == 2) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            int index = parent.getIndex(node);
            String filePath = parent.getUserObject().toString();
            List<MatchInfo> matchList = results.get(filePath);
            if (matchList == null || index >= matchList.size()) return;
            MatchInfo matchInfo = matchList.get(index);
            VirtualFileUtils.openFileAtMatch(project, filePath, matchInfo);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {}

    @Override
    public void mouseReleased(MouseEvent event) {}

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}
}
