package ru.testit.management.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import ru.testit.management.utils.SyncUtils;
import ru.testit.management.windows.tools.TmsToolWindow;

public class SyncProjectAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;
        ToolWindow toolWindow =
                ToolWindowManager.getInstance(project).getToolWindow("Test IT");
        if (toolWindow == null) return;
        Content content = toolWindow.getContentManager().getContent(0);
        if (content == null) return;
        TmsToolWindow window = (TmsToolWindow) content.getComponent();
        window.refresh(project);
    }
}
