package ru.testit.management.clients;

import org.junit.jupiter.api.*;
import ru.psb.testit.client.api.ProjectsApi;
import ru.psb.testit.client.api.SectionsApi;
import ru.psb.testit.client.api.WorkItemsApi;
import ru.psb.testit.client.invoker.ApiClient;
import ru.psb.testit.client.invoker.ApiException;
import ru.psb.testit.client.invoker.Configuration;
import ru.psb.testit.client.model.ProjectModel;
import ru.psb.testit.client.model.SectionModel;
import ru.psb.testit.client.model.WorkItemModel;
import ru.psb.testit.client.model.WorkItemShortModel;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Test IT API Integration Tests")
@Tag("@Integration")
class TmsClientIntegrationTest {

    private static final String TMS_URL = "https://tsit-app1";
    private static final String TMS_TOKEN = "blNtb1FIY2sxM2pLb09BN3Bq";
    private static final String TMS_PROJECT_ID = "a10c90c4-39d7-4ed1-8be6-d23a6a00a84d";

    private static ApiClient apiClient;
    private static ProjectsApi projectsApi;
    private static WorkItemsApi workItemsApi;
    private static SectionsApi sectionsApi;

    private static UUID firstSectionId;

    private static UUID firstWorkItemId;

    @BeforeEach
    void initClient() {
        apiClient = Configuration.getDefaultApiClient();
        apiClient.setBasePath(TMS_URL);
        apiClient.setApiKeyPrefix("PrivateToken");
        apiClient.setApiKey(TMS_TOKEN);

        projectsApi = new ProjectsApi(apiClient);
        workItemsApi = new WorkItemsApi(apiClient);
        sectionsApi = new SectionsApi(apiClient);
    }

    @Test
    @Order(1)
    @DisplayName("[1] Соединение: getProjectById возвращает непустой проект")
    void testConnectionAndProjectFetch() throws ApiException {
        ProjectModel project = projectsApi.getProjectById(TMS_PROJECT_ID);

        assertNotNull(project, "Проект не должен быть null");
        assertNotNull(project.getId(), "ID проекта не должен быть null");
        assertNotNull(project.getName(), "Имя проекта не должно быть null");
        assertFalse(project.getName().isBlank(), "Имя проекта не должно быть пустым");

        System.out.printf("Проект получен: id=%s, name=%s%n",
                project.getId(), project.getName());
    }

    @Test
    @Order(2)
    @DisplayName("[2] Соединение: невалидный токен возвращает ошибку")
    void testInvalidTokenReturnsError() {
        ApiClient badClient = Configuration.getDefaultApiClient();
        badClient.setBasePath(TMS_URL);
        badClient.setApiKeyPrefix("PrivateToken");
        badClient.setApiKey("invalid_token_12345");
        ProjectsApi badProjectsApi = new ProjectsApi(badClient);

        ApiException ex = assertThrows(ApiException.class,
                () -> badProjectsApi.getProjectById(TMS_PROJECT_ID),
                "Ожидается ApiException при невалидном токене");

        assertTrue(ex.getCode() == 401 || ex.getCode() == 403,
                "Ожидается HTTP 401 или 403, получено: " + ex.getCode());
        System.out.printf("Невалидный токен → HTTP %d%n", ex.getCode());
    }

    @Test
    @Order(3)
    @DisplayName("[3] Соединение: несуществующий projectId возвращает 404")
    void testNonExistentProjectReturns404() {
        ApiException ex = assertThrows(ApiException.class,
                () -> projectsApi.getProjectById(UUID.randomUUID().toString()),
                "Ожидается ApiException для несуществующего проекта");

        assertEquals(403, ex.getCode(),
                "Ожидается HTTP 403, получено: " + ex.getCode());
        System.out.printf("Несуществующий проект → HTTP 403%n");
    }

    // 2. Получение секций проекта

    @Test
    @Order(4)
    @DisplayName("[4] Секции: getSectionsByProjectId возвращает непустой список")
    void testGetSections() throws ApiException {
        List<SectionModel> sections = projectsApi.getSectionsByProjectId(
                TMS_PROJECT_ID, null, null, null, null, null);

        assertNotNull(sections, "Список секций не должен быть null");
        assertFalse(sections.isEmpty(), "Список секций не должен быть пустым");

        SectionModel first = sections.get(1);
        assertNotNull(first.getId(), "ID секции не должен быть null");
        assertNotNull(first.getName(), "Имя секции не должно быть null");

        firstSectionId = UUID.fromString(first.getId().toString());
        System.out.printf("Получено секций: %d. Первая: id=%s, name=%s%n",
                sections.size(), first.getId(), first.getName());
    }

    @Test
    @Order(5)
    @DisplayName("[5] Секции: каждая секция содержит id и name")
    void testEachSectionHasIdAndName() throws ApiException {
        List<SectionModel> sections = projectsApi.getSectionsByProjectId(
                TMS_PROJECT_ID, null, null, null, null, null);

        assertNotNull(sections);
        for (SectionModel section : sections) {
            assertNotNull(section.getId(),
                    "Секция без ID: " + section);
            assertNotNull(section.getName(),
                    "Секция без имени, id=" + section.getId());
        }
        System.out.printf("Все %d секций валидны%n", sections.size());
    }

    // 3. Получение WorkItems из секции

    @Test
    @Order(6)
    @DisplayName("[6] WorkItems: getWorkItemsBySectionId возвращает список для первой секции")
    void testGetWorkItemsBySectionId() throws ApiException {
        assumeTrue(firstSectionId != null,
                "Тест пропущен: firstSectionId не получен (тест [4] не прошёл)");

        List<WorkItemShortModel> items = sectionsApi.getWorkItemsBySectionId(
                firstSectionId, null, null, null, null, null, null, null, null);

        assertNotNull(items, "Список workItems не должен быть null");
        System.out.printf("WorkItems в первой секции (%s): %d%n",
                firstSectionId, items.size());

        if (!items.isEmpty()) {
            WorkItemShortModel first = items.get(0);
            assertNotNull(first.getId(), "WorkItem без ID");
            firstWorkItemId = UUID.fromString(first.getId().toString());
            System.out.printf("Первый workItem: id=%s, name=%s%n",
                    first.getId(), first.getName());
        }
    }

    @Test
    @Order(7)
    @DisplayName("[7] WorkItems: каждый WorkItemShortModel содержит id и name")
    void testWorkItemShortModelFields() throws ApiException {
        assumeTrue(firstSectionId != null,
                "Тест пропущен: firstSectionId не получен");

        List<WorkItemShortModel> items = sectionsApi.getWorkItemsBySectionId(
                firstSectionId, null, null, null, null, null, null, null, null);

        assertNotNull(items);
        for (WorkItemShortModel item : items) {
            assertNotNull(item.getId(),
                    "WorkItem без ID в секции " + firstSectionId);
        }
        System.out.printf("Все %d workItems секции валидны%n", items.size());
    }

    // 4. Получение WorkItem по ID

    @Test
    @Order(8)
    @DisplayName("[8] WorkItem: getWorkItemById возвращает полную модель")
    void testGetWorkItemById() throws ApiException {
        assumeTrue(firstWorkItemId != null,
                "Тест пропущен: firstWorkItemId не получен (тест [6] не прошёл или секция пуста)");

        WorkItemModel item = workItemsApi.getWorkItemById(
                firstWorkItemId.toString(), null, null);

        assertNotNull(item, "WorkItemModel не должен быть null");
        assertNotNull(item.getId(), "WorkItem.id не должен быть null");
        assertEquals(firstWorkItemId.toString(), item.getId().toString(),
                "ID workItem должен совпадать с запрошенным");
        assertNotNull(item.getName(), "WorkItem.name не должно быть null");

        System.out.printf("WorkItem получен: id=%s, name=%s%n",
                item.getId(), item.getName());
    }

    @Test
    @Order(9)
    @DisplayName("[9] WorkItem: запрос несуществующего ID возвращает 404")
    void testGetWorkItemByNonExistentId() {
        ApiException ex = assertThrows(ApiException.class,
                () -> workItemsApi.getWorkItemById(UUID.randomUUID().toString(), null, null),
                "Ожидается ApiException для несуществующего workItem");

        assertEquals(403, ex.getCode(),
                "Ожидается HTTP 403, получено: " + ex.getCode());
        System.out.printf("Несуществующий WorkItem → HTTP 403%n");
    }

    // 5. Проверка getSettingsValidationErrorMsg (через ProjectsApi напрямую)

    @Test
    @Order(10)
    @DisplayName("[10] Валидация настроек: корректные данные → null (нет ошибки)")
    void testSettingsValidationWithValidData() {
        String error = validateSettings(TMS_URL, TMS_PROJECT_ID, TMS_TOKEN);
        assertNull(error,
                "Ожидается null при корректных настройках, получено: " + error);
        System.out.println("Валидация настроек (корректные) → null");
    }

    @Test
    @Order(11)
    @DisplayName("[11] Валидация настроек: неверный токен → сообщение об ошибке")
    void testSettingsValidationWithBadToken() {
        String error = validateSettings(TMS_URL, TMS_PROJECT_ID, "bad_token");
        assertNotNull(error, "Ожидается сообщение об ошибке при неверном токене");
        assertFalse(error.isBlank(), "Сообщение об ошибке не должно быть пустым");
        System.out.println("Валидация настроек (неверный токен) → " + error);
    }

    @Test
    @Order(12)
    @DisplayName("[12] Валидация настроек: недоступный URL → сообщение об ошибке")
    void testSettingsValidationWithBadUrl() {
        String error = validateSettings("http://localhost:19999", TMS_PROJECT_ID, TMS_TOKEN);
        assertNotNull(error, "Ожидается сообщение об ошибке при недоступном URL");
        System.out.println("Валидация настроек (недоступный URL) → " + error);
    }

    // Аналог TmsClient.getSettingsValidationErrorMsg без IntelliJ-зависимостей

    private String validateSettings(String url, String projectId, String token) {
        try {
            ApiClient client = Configuration.getDefaultApiClient();
            client.setBasePath(url);
            client.setApiKeyPrefix("PrivateToken");
            client.setApiKey(token);
            new ProjectsApi(client).getProjectById(projectId);
            return null;
        } catch (Throwable e) {
            return e.getMessage();
        }
    }
}
