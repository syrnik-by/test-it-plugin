package ru.testit.management.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ru.testit.management.utils.SearchAllureUtils;

public class SearchAllureAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent ignored) {
        SearchAllureUtils.research();
    }
}
