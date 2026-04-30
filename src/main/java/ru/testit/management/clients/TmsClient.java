package ru.testit.management.clients;

import ru.psb.testit.client.api.AutoTestsApi;
import ru.psb.testit.client.api.AttachmentsApi;
import ru.psb.testit.client.api.ProjectsApi;
import ru.psb.testit.client.api.SectionsApi;
import ru.psb.testit.client.api.TestResultsApi;
import ru.psb.testit.client.api.TestRunsApi;
import ru.psb.testit.client.api.WorkItemsApi;
import ru.psb.testit.client.invoker.ApiClient;
import ru.psb.testit.client.invoker.ApiException;
import ru.psb.testit.client.invoker.Configuration;
import ru.psb.testit.client.model.SectionModel;
import ru.psb.testit.client.model.WorkItemModel;
import ru.psb.testit.client.model.WorkItemShortModel;
import ru.testit.management.windows.settings.TmsSettingsState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class TmsClient {
    private final Logger logger = Logger.getLogger(TmsClient.class.getSimpleName());
    private final TestRunsApi testRunsApi;
    private final AutoTestsApi autoTestsApi;
    private final AttachmentsApi attachmentsApi;
    private final TestResultsApi testResultsApi;
    private final ProjectsApi projectsApi;
    private final WorkItemsApi workItemsApi;
    private final SectionsApi sectionsApi;

    public TmsClient(String url) {
        ApiClient apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(url);
        apiClient.setApiKeyPrefix("PrivateToken");

        testRunsApi = new TestRunsApi(apiClient);
        autoTestsApi = new AutoTestsApi(apiClient);
        attachmentsApi = new AttachmentsApi(apiClient);
        testResultsApi = new TestResultsApi(apiClient);
        projectsApi = new ProjectsApi(apiClient);
        workItemsApi = new WorkItemsApi(apiClient);
        sectionsApi = new SectionsApi(apiClient);

        setToken(TmsSettingsState.getInstance().privateToken);
    }

    public void setToken(String token) {
        ApiClient apiClient = projectsApi.getApiClient();
        apiClient.setApiKey(token);
    }

    public String getSettingsValidationErrorMsg(String projectId, String privateToken) {
        try {
            setToken(privateToken);
            projectsApi.getProjectById(projectId);
            return null;
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    public Iterable<SectionModel> getSections() {
        System.out.println("getSections:");
        long startTime = System.currentTimeMillis();
        Set<SectionModel> sections = new LinkedHashSet<>();
        try {
            String projectId = TmsSettingsState.getInstance().projectId;
            List<SectionModel> result = projectsApi.getSectionsByProjectId(
                    projectId, null, null, null, null, null);
            if (result != null) {
                sections.addAll(result);
            }
        } catch (ApiException e) {
            logger.severe(e.getMessage());
        }
        System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
        return sections;
    }

    public WorkItemModel getWorkItemById(UUID id) {
        System.out.println("getWorkItemById:");
        long startTime = System.currentTimeMillis();
        try {
            WorkItemModel result = workItemsApi.getWorkItemById(id.toString(), null, null);
            System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
            return result;
        } catch (ApiException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    public Iterable<WorkItemShortModel> getWorkItemsBySectionId(UUID sectionId) {
        System.out.println("getWorkItemsBySectionId:");
        long startTime = System.currentTimeMillis();
        if (sectionId == null) return List.of();
        try {
            List<WorkItemShortModel> items = sectionsApi.getWorkItemsBySectionId(
                    sectionId, null, null, null, null, null, null, null, null);
            System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
            return items != null ? items : List.of();
        } catch (ApiException e) {
            logger.severe(e.getMessage());
        }
        return List.of();
    }
}
