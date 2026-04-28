package ru.testit.management.utils;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import ru.testit.management.windows.tools.TmsToolWindow;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SyncUtils {
    private static volatile boolean isRefreshing = false;
    private static final Logger logger = Logger.getLogger(SyncUtils.class.getSimpleName());

    public static void refresh() {
        synchronized (TmsToolWindow.getInstance()) {
            if (isRefreshing) return;
            isRefreshing = true;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                var project = DataManager.getInstance()
                        .getDataContextFromFocusAsync()
                        .then(ctx -> PlatformDataKeys.PROJECT.getData(ctx))
                        .blockingGet(3, TimeUnit.SECONDS);

                if (project != null) {
                    TmsToolWindow.getInstance().refresh(project);
                }
            } catch (Throwable e) {
                logger.severe(e.getMessage());
            } finally {
                isRefreshing = false;
            }
        });
    }
}
