package ru.testit.management.windows.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import ru.testit.management.utils.MessagesUtils;
import ru.testit.management.utils.SyncUtils;

import javax.swing.JComponent;

public class TmsSettingsFactory implements Configurable {
    private final TmsSettingsState state = TmsSettingsState.getInstance();
    private TmsSettingsWindow window;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return MessagesUtils.get("window.settings.name");
    }

    @Override
    public @Nullable JComponent createComponent() {
        window = new TmsSettingsWindow();
        return window.getPanel();
    }

    @Override
    public boolean isModified() {
        boolean modified = !window.getUrl().getText().equals(state.url);
        modified |= !window.getProjectId().getText().equals(state.projectId);
        modified |= !window.getPrivateToken().getText().equals(state.privateToken);
        modified |= !String.valueOf(window.getFrameworkComboBox().getSelectedItem()).equals(state.getFramework());
        return modified;
    }

    @Override
    public void apply() {
        state.url = window.getUrl().getText();
        state.projectId = window.getProjectId().getText();
        state.privateToken = window.getPrivateToken().getText();
        state.setFramework(String.valueOf(window.getFrameworkComboBox().getSelectedItem()));
        SyncUtils.refresh();
    }

    @Override
    public void reset() {
        window.getUrl().setText(state.url);
        window.getProjectId().setText(state.projectId);
        window.getPrivateToken().setText(state.privateToken);
        window.getFrameworkComboBox().setSelectedItem(state.getFramework());
    }

    @Override
    public void disposeUIResources() {
        window = null;
    }
}
