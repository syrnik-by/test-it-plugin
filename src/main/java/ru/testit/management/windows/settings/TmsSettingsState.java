package ru.testit.management.windows.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.PlatformUtils;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import ru.testit.management.enums.FrameworkOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ru.testit.settings.TmsSettingsState", storages = @Storage("TestItSettings.xml"))
@Service
@Data
public final class TmsSettingsState implements PersistentStateComponent<TmsSettingsState> {
    public String url = "";
    public String projectId = "";
    public String privateToken = "";
    private String framework = getDefaultFramework();

    public void setFramework(@Nullable String value) {
        if (value == null) {
            this.framework = getDefaultFramework();
        } else {
            this.framework = value;
        }
    }

    private String getDefaultFramework() {
        if (PlatformUtils.isPyCharm()) {
            return FrameworkOption.PYTEST.toString();
        } else if (PlatformUtils.isIntelliJ()) {
            return FrameworkOption.JUNIT.toString();
        } else if (PlatformUtils.isWebStorm()) {
            return FrameworkOption.PLAYWRIGHT.toString();
        } else if (PlatformUtils.isRider()) {
            return FrameworkOption.MSTEST.toString();
        }
        return FrameworkOption.JUNIT.toString();
    }

    @Override
    public @Nullable TmsSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TmsSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static TmsSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(TmsSettingsState.class);
    }
}
