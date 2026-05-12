package ru.testit.management.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import ru.testit.management.windows.tools.TmsToolWindow;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SyncUtils {
    private static volatile boolean isRefreshing = false;
    private static final Logger logger = Logger.getLogger(SyncUtils.class.getSimpleName());

    public static void refresh() {
        synchronized (SyncUtils.class) {
            if (isRefreshing) return;
            isRefreshing = true;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Test IT");
                    if (toolWindow == null) continue;
                    Content content = toolWindow.getContentManager().getContent(0);
                    if (content == null) continue;
                    TmsToolWindow window = (TmsToolWindow) content.getComponent();
                    window.refresh(project);
                }
//                var project = DataManager.getInstance()
//                        .getDataContextFromFocusAsync()
//                        .then(ctx -> PlatformDataKeys.PROJECT.getData(ctx))
//                        .blockingGet(3, TimeUnit.SECONDS);
//
//                if (project != null) {
//                    TmsToolWindow.getInstance().refresh(project);
//                }
            } catch (Throwable e) {
                logger.severe(e.getMessage());
            } finally {
                isRefreshing = false;
            }
        });
    }
}
