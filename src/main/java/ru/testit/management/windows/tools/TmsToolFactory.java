package ru.testit.management.windows.tools;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ru.testit.management.utils.SyncUtils;

import javax.swing.JComponent;

public class TmsToolFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        addContent(TmsToolWindow.getInstance(), toolWindow);
        SyncUtils.refresh();
    }

    private void addContent(JComponent component, ToolWindow toolWindow) {
        Content content = ContentFactory.getInstance().createContent(component, null, false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
    }
}
