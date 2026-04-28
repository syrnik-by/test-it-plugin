package ru.testit.management.windows.tools;

import ru.testit.kotlin.client.models.WorkItemEntityTypes;
import ru.testit.management.icons.TmsIcons;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TmsCellStyle extends DefaultTreeCellRenderer {

    public TmsCellStyle() {
        setBorderSelectionColor(null);
        setBackgroundSelectionColor(null);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                   boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        TmsNodeModel model = (TmsNodeModel) node.getUserObject();
        Long globalId = model.getGlobalId();
        String name = model.getName() != null ? model.getName() : "";

        if (globalId == null && expanded) {
            this.setIcon(getOpenIcon());
            this.setText(name);
        } else if (globalId == null) {
            this.setIcon(getClosedIcon());
            this.setText(name);
        } else {
            setWorkItemIcon(model);
            this.setText("<html><i>" + globalId + "</i> " + name + "</html>");
        }

        return this;
    }

    private void setWorkItemIcon(TmsNodeModel model) {
        WorkItemEntityTypes type = model.getEntityTypeName();
        if (WorkItemEntityTypes.CheckLists.equals(type)) {
            this.setIcon(getCheckListIcon(model.isAutomated()));
        } else if (WorkItemEntityTypes.SharedSteps.equals(type)) {
            this.setIcon(getSharedStepIcon(model.isAutomated()));
        } else {
            this.setIcon(getTestCaseIcon(model.isAutomated()));
        }
    }

    private Icon getCheckListIcon(boolean isAutomated) {
        return isAutomated ? TmsIcons.CheckListAutomated : TmsIcons.CheckList;
    }

    private Icon getSharedStepIcon(boolean isAutomated) {
        return isAutomated ? TmsIcons.SharedStepAutomated : TmsIcons.SharedStep;
    }

    private Icon getTestCaseIcon(boolean isAutomated) {
        return isAutomated ? TmsIcons.TestCaseAutomated : TmsIcons.TestCase;
    }
}
