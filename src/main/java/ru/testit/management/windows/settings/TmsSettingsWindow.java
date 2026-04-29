package ru.testit.management.windows.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import ru.testit.management.clients.TmsClient;
import ru.testit.management.enums.FrameworkOption;
import ru.testit.management.utils.MessagesUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TmsSettingsWindow {

    public final JPanel panel;

    public JBTextField projectIdField;
    public ComboBox<String> frameworkComboBox;
    public JBTextField privateTokenField;
    public JBTextField urlField;

    private final TmsSettingsState _state = TmsSettingsState.getInstance();
    private JLabel _verifyLabel;

    public TmsSettingsWindow() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Browser link
        JLabel linkLabel = new JLabel(
                "<html><a href='" + MessagesUtils.get("window.settings.instruction.link.url") + "'>" +
                MessagesUtils.get("window.settings.instruction.link.text") + "</a></html>"
        );
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(linkLabel, gbc);
        row++;

        // --- Group: Connection ---
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JBLabel("--- " + MessagesUtils.get("window.settings.group.connection.name") + " ---"), gbc);
        row++;

        // URL
        gbc.gridwidth = 1; gbc.weightx = 0.3;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JBLabel(MessagesUtils.get("window.settings.group.connection.url.name")), gbc);
        urlField = new JBTextField(_state.getUrl());
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(urlField, gbc);
        row++;

        // Project ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JBLabel(MessagesUtils.get("window.settings.group.connection.projectId.name")), gbc);
        projectIdField = new JBTextField(_state.getProjectId());
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(projectIdField, gbc);
        row++;

        // Private Token
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JBLabel(MessagesUtils.get("window.settings.group.connection.token.name")), gbc);
        privateTokenField = new JBTextField(_state.getPrivateToken());
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(privateTokenField, gbc);
        row++;

        // Verify button + label
        JButton verifyButton = new JButton(MessagesUtils.get("window.settings.group.connection.verify.button.text"));
        _verifyLabel = new JBLabel(MessagesUtils.get("window.settings.group.connection.verify.label.end.text"));

        verifyButton.addActionListener(e -> {
            _verifyLabel.setText(MessagesUtils.get("window.settings.group.connection.verify.label.start.text"));
            ApplicationManager.getApplication().invokeLater(() ->
                verifySettings(
                        projectIdField.getText(),
                        privateTokenField.getText(),
                        getValidUrl(urlField.getText()),
                        _verifyLabel
                )
            );
        });

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(verifyButton, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(_verifyLabel, gbc);
        row++;

        // --- Group: Settings ---
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 1.0;
        panel.add(new JBLabel("--- Settings ---"), gbc);
        row++;

        // Framework
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JBLabel("Framework"), gbc);

        List<String> options = Arrays.stream(FrameworkOption.values())
                .map(Object::toString)
                .toList();
        frameworkComboBox = new ComboBox<>(options.toArray(new String[0]));
        frameworkComboBox.setSelectedItem(_state.getFramework());
        frameworkComboBox.addItemListener(it -> {
            if (it.getStateChange() == ItemEvent.SELECTED) {
                _state.setFramework((String) it.getItem());
            }
        });

        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(frameworkComboBox, gbc);
        row++;

        // Vertical filler
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);
    }

    public void apply() {
        _state.setUrl(getValidUrl(urlField.getText()));
        _state.setProjectId(projectIdField.getText());
        _state.setPrivateToken(privateTokenField.getText());
    }

    public void reset() {
        urlField.setText(_state.getUrl());
        projectIdField.setText(_state.getProjectId());
        privateTokenField.setText(_state.getPrivateToken());
        frameworkComboBox.setSelectedItem(_state.getFramework());
    }

    public boolean isModified() {
        return !urlField.getText().equals(_state.getUrl())
                || !projectIdField.getText().equals(_state.getProjectId())
                || !privateTokenField.getText().equals(_state.getPrivateToken());
    }

    private String getValidUrl(String text) {
        if (text == null) return "";
        return text.endsWith("/") ? text.substring(0, text.length() - 1) : text;
    }

    private void verifySettings(String projectId, String privateToken, String url, JLabel verifyLabel) {
        String errorMsg = getUrlValidationErrorMsg(url);
        if (errorMsg != null) { verifyLabel.setText(errorMsg); return; }

        errorMsg = getProjectIdValidationErrorMsg(projectId);
        if (errorMsg != null) { verifyLabel.setText(errorMsg); return; }

        if (privateToken == null || privateToken.isBlank()) {
            verifyLabel.setText(MessagesUtils.get("window.settings.validation.token.error.text"));
            return;
        }

        errorMsg = new TmsClient(url).getSettingsValidationErrorMsg(
                projectId != null ? projectId : "",
                privateToken
        );
        verifyLabel.setText(errorMsg != null
                ? errorMsg
                : MessagesUtils.get("window.settings.validation.success.text"));
    }

    private String getUrlValidationErrorMsg(String url) {
        if (url == null || url.isBlank()) {
            return MessagesUtils.get("window.settings.validation.url.error.text");
        }
        try {
            new URL(url);
        } catch (Throwable ignored) {
            return MessagesUtils.get("window.settings.validation.url.error.text");
        }
        return null;
    }

    private String getProjectIdValidationErrorMsg(String projectId) {
        if (projectId == null || projectId.isBlank()) {
            return MessagesUtils.get("window.settings.validation.projectId.error.text");
        }
        try {
            UUID.fromString(projectId);
        } catch (Throwable ignored) {
            return MessagesUtils.get("window.settings.validation.projectId.error.text");
        }
        return null;
    }
}
