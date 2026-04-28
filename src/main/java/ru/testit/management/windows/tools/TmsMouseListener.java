package ru.testit.management.windows.tools;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class TmsMouseListener implements MouseListener {
    private final Project project;
    private final JTree tree;

    public TmsMouseListener(Project project, JTree tree) {
        this.project = project;
        this.tree = tree;
    }

    @Override
    public void mouseClicked(MouseEvent event) {}

    @Override
    public void mousePressed(MouseEvent event) {
        int row = tree.getClosestRowForLocation(event.getX(), event.getY());
        tree.setSelectionRow(row);

        if (event.isPopupTrigger()) {
            showPopup(event);
        } else if (event.getClickCount() == 2) {
            tryOpenTest();
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (event.isPopupTrigger()) {
            showPopup(event);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}

    private void tryOpenTest() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        TmsNodeModel model = (TmsNodeModel) node.getUserObject();

        OpenFileDescriptor descriptor = null;

        if (model.getFile() != null && model.getLine() != null) {
            descriptor = new OpenFileDescriptor(
                project,
                (VirtualFile) model.getFile(),
                model.getLine(),
                0,
                true
            );
        }

        if (descriptor != null) {
            FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
            descriptor.dispose();
        }
    }

    private void showPopup(MouseEvent event) {
        new TmsPopupMenu(tree, project).show(event.getComponent(), event.getX(), event.getY());
    }
}
