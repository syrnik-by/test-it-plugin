package ru.testit.management.clients

import ru.testit.client.api.AttachmentsApi
import ru.testit.client.api.AutoTestsApi
import ru.testit.client.api.ProjectsApi
import ru.testit.client.api.SectionsApi
import ru.testit.client.api.TestResultsApi
import ru.testit.client.api.TestRunsApi
import ru.testit.client.api.WorkItemsApi
import ru.testit.client.invoker.ApiClient
import ru.testit.client.invoker.ApiException
import ru.testit.client.invoker.Configuration
import ru.testit.client.invoker.auth.ApiKeyAuth
import ru.testit.client.model.SectionModel
import ru.testit.client.model.WorkItemFilterModel
import ru.testit.client.model.WorkItemModel
import ru.testit.client.model.WorkItemSelectModel
import ru.testit.client.model.WorkItemShortModel
import ru.testit.management.windows.settings.TmsSettingsState
import java.util.UUID
import java.util.logging.Logger

class TmsClient(url: String) {
    private val _logger = Logger.getLogger(TmsClient::class.java.simpleName)

    private val apiClient: ApiClient
    private val testRunsApi: TestRunsApi
    private val autoTestsApi: AutoTestsApi
    private val attachmentsApi: AttachmentsApi
    private val testResultsApi: TestResultsApi
    private val projectsApi: ProjectsApi
    private val workItemsApi: WorkItemsApi
    private val sectionsApi: SectionsApi

    init {
        apiClient = Configuration.getDefaultApiClient()
        apiClient.basePath = url
        apiClient.isVerifyingSsl = false

        val auth = apiClient.getAuthentication("Bearer or PrivateToken") as ApiKeyAuth
        auth.apiKeyPrefix = "PrivateToken"
        auth.apiKey = TmsSettingsState.instance.privateToken

        testRunsApi = TestRunsApi(apiClient)
        autoTestsApi = AutoTestsApi(apiClient)
        attachmentsApi = AttachmentsApi(apiClient)
        testResultsApi = TestResultsApi(apiClient)
        projectsApi = ProjectsApi(apiClient)
        workItemsApi = WorkItemsApi(apiClient)
        sectionsApi = SectionsApi(apiClient)
    }

    fun updateToken(token: String) {
        val auth = apiClient.getAuthentication("Bearer or PrivateToken") as ApiKeyAuth
        auth.apiKey = token
    }

    fun getSettingsValidationErrorMsg(projectId: String, privateToken: String): String? {
        return try {
            updateToken(privateToken)
            projectsApi.getProjectById(projectId)
            null
        } catch (exception: ApiException) {
            exception.message
        } catch (exception: Throwable) {
            exception.message
        }
    }

    fun getSections(): Iterable<SectionModel> {
        println("getSections:")
        val startTime = System.currentTimeMillis()
        val sections = mutableListOf<SectionModel>()
        try {
            val result = sectionsApi.getSectionsByProjectId(
                TmsSettingsState.instance.projectId,
                null, null, null, null, null
            )
            if (result != null) sections.addAll(result)
        } catch (exception: ApiException) {
            _logger.severe { exception.message }
        }
        println("Затраченное время: ${System.currentTimeMillis() - startTime} мс")
        return sections
    }

    fun getWorkItemById(id: UUID): WorkItemModel {
        println("getWorkItemById:")
        val startTime = System.currentTimeMillis()
        val result = workItemsApi.getWorkItemById(id.toString(), null, null)
        println("Затраченное время: ${System.currentTimeMillis() - startTime} мс")
        return result
    }

    fun getWorkItemsBySectionId(sectionId: UUID?): Iterable<WorkItemShortModel> {
        println("getWorkItemsBySectionId:")
        val startTime = System.currentTimeMillis()

        if (sectionId == null) return listOf()

        val filter = WorkItemFilterModel().apply {
            sectionIds = setOf(sectionId)
            isDeleted = false
        }
        val request = WorkItemSelectModel().apply {
            this.filter = filter
        }
        return try {
            val result = workItemsApi.apiV2WorkItemsSearchPost(
                null, null, null, null, null, request
            )
            println("Затраченное время: ${System.currentTimeMillis() - startTime} мс")
            result ?: listOf()
        } catch (exception: ApiException) {
            _logger.severe { exception.message }
            listOf()
        }
    }
}
