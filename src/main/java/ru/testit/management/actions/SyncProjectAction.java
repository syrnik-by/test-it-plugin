package ru.testit.management.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ru.testit.management.utils.SyncUtils;

public class SyncProjectAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent ignored) {
        SyncUtils.refresh();
    }
}
