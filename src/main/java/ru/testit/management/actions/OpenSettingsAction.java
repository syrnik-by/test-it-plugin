package ru.testit.management.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import ru.testit.management.utils.MessagesUtils;

public class OpenSettingsAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        ShowSettingsUtil.getInstance().showSettingsDialog(
            event.getProject(),
            MessagesUtils.get("action.settings.name")
        );
    }
}
