package ru.testit.management.clients;

import ru.testit.management.windows.settings.TmsSettingsState;

import java.util.List;
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
    private final ProjectSectionsApi projectSectionsApi;

    public TmsClient(String url) {
        testRunsApi = new TestRunsApi(url);
        init(testRunsApi);
        autoTestsApi = new AutoTestsApi(url);
        init(autoTestsApi);
        attachmentsApi = new AttachmentsApi(url);
        init(attachmentsApi);
        testResultsApi = new TestResultsApi(url);
        init(testResultsApi);
        projectsApi = new ProjectsApi(url);
        init(projectsApi);
        workItemsApi = new WorkItemsApi(url);
        init(workItemsApi);
        projectSectionsApi = new ProjectSectionsApi(url);
        init(projectSectionsApi);
    }

    public void init(ApiClient client) {
        init(client, TmsSettingsState.getInstance().privateToken);
    }

    public void init(ApiClient client, String token) {
        client.getApiKeyPrefix().put("Authorization", "PrivateToken");
        client.getApiKey().put("Authorization", token);
        client.setVerifyingSsl(false);
    }

    public String getSettingsValidationErrorMsg(String projectId, String privateToken) {
        try {
            if (projectsApi.getApiKey().getOrDefault("Authorization", "").isEmpty()) {
                projectsApi.getApiKey().put("Authorization", privateToken);
            }
            projectsApi.getProjectById(projectId);
            return null;
        } catch (Throwable e) {
            return e.getMessage();
        }
    }

    public Iterable<SectionModel> getSections() {
        System.out.println("getSections:");
        long startTime = System.currentTimeMillis();
        java.util.Set<SectionModel> sections = new java.util.LinkedHashSet<>();
        try {
            sections.addAll(projectSectionsApi.getSectionsByProjectId(
                    TmsSettingsState.getInstance().projectId, null, null, null, null, null));
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }
        System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
        return sections;
    }

    public WorkItemModel getWorkItemById(UUID id) {
        System.out.println("getWorkItemById:");
        long startTime = System.currentTimeMillis();
        WorkItemModel result = workItemsApi.getWorkItemById(id.toString(), null, null);
        System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
        return result;
    }

    public Iterable<WorkItemShortApiResult> getWorkItemsBySectionId(UUID sectionId) {
        System.out.println("getWorkItemsBySectionId:");
        long startTime = System.currentTimeMillis();
        if (sectionId == null) return List.of();

        WorkItemFilterApiModel filter = new WorkItemFilterApiModel();
        filter.setSectionIds(java.util.Set.of(sectionId));
        filter.setIsDeleted(false);
        WorkItemSelectApiModel request = new WorkItemSelectApiModel();
        request.setFilter(filter);

        try {
            List<WorkItemShortApiResult> items = workItemsApi.apiV2WorkItemsSearchPost(request);
            System.out.println("Затраченное время: " + (System.currentTimeMillis() - startTime) + " мс");
            return items;
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }
        return List.of();
    }
}
