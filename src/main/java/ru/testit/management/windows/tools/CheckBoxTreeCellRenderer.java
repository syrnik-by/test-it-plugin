package ru.testit.management.windows.tools;

import com.intellij.icons.AllIcons;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class CheckBoxTreeCellRenderer implements TreeCellRenderer {
    private final JCheckBox checkBox = new JCheckBox();
    private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                   boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (userObject instanceof CheckBoxNode) {
            CheckBoxNode checkBoxNode = (CheckBoxNode) userObject;
            checkBox.setText(checkBoxNode.getText());
            checkBox.setSelected(checkBoxNode.isSelected());
            checkBox.setOpaque(false);
            checkBox.setIcon(AllIcons.General.Information);
            return checkBox;
        }

        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
