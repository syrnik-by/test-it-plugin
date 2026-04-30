package ru.testit.management.windows.tools;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import ru.testit.management.clients.TmsClient;
import ru.testit.management.utils.ClipboardUtils;
import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.utils.MessagesUtils;
import ru.testit.management.utils.VirtualFileUtils;
import ru.testit.management.windows.settings.TmsSettingsState;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class TmsPopupMenu extends JBPopupMenu {

    private final TmsSettingsState state = TmsSettingsState.getInstance();

    public TmsPopupMenu(JTree tree, Project project) {
        JBMenuItem copyItem = new JBMenuItem(
                MessagesUtils.get("window.tool.popup.copy.text"),
                AllIcons.Actions.Copy
        );

        addCopyAction(copyItem, tree, project);
        add(copyItem);
    }

    // TODO: depends on property
    private void addCopyAction(JMenuItem item, JTree tree, Project project) {
        item.addActionListener(e -> {
            Object component = tree.getLastSelectedPathComponent();
            if (component == null) return;

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) component;
            TmsNodeModel model = (TmsNodeModel) node.getUserObject();

            if (node.isLeaf() && model.getId() != null) {
                var fullModel = new TmsClient(state.url).getWorkItemById(model.getId());

                model.setPreconditions(fullModel.getPreconditionSteps());
                model.setSteps(fullModel.getSteps());
                model.setPostconditions(fullModel.getPostconditionSteps());

                long startTime = System.currentTimeMillis();
                TmsNodeModel updatedModel = getModelWithFileLineModified(model, project);
                long endTime = System.currentTimeMillis();
                System.out.println("Затраченное на индексацию строки время:  " + (endTime - startTime) + " мс");

                node.setUserObject(updatedModel);
                ClipboardUtils.copyToClipboard(CodeSnippetUtils.getNewSnippet(node.getUserObject()));
                showSimpleNotification(project);
            }
        });
    }

    private TmsNodeModel getModelWithFileLineModified(TmsNodeModel model, Project project) {
        Long globalId = model.getGlobalId();
        if (globalId == null) return model;

        VirtualFileUtils.refresh(project);

        System.out.println("Всего файлов с нужным ext: " + VirtualFileUtils.projectJavaFiles.size());

        for (var file : VirtualFileUtils.projectJavaFiles) {
            List<String> lines = new ArrayList<>();

            ApplicationManager.getApplication().runReadAction(() -> {
                var document = FileDocumentManager.getInstance().getDocument(file);
                if (document != null) {
                    document.getCharsSequence().toString().lines().forEach(lines::add);
                }
            });

            Integer line = findTestByGlobalId(lines, globalId);
            if (line != null) {
                model.setFile(file);
                model.setLine(line);
                break;
            }
        }

        return model;
    }

    private Integer findTestByGlobalId(List<String> lines, long globalId) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(CodeSnippetUtils.getComparator().apply(globalId))) {
                return i;
            }
        }
        return null;
    }

    private void showSimpleNotification(Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("TestItPluginNotifications")
                .createNotification(
                        "Copy",
                        "Successful item copy",
                        NotificationType.INFORMATION
                )
                .notify(project);
    }
}