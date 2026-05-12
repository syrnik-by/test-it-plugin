package ru.testit.management.snippet;

import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class TestCafeSnippet {

    private static final String CODE_SNIPPET =
        "    test.meta({\n" +
        "        externalId: 'externalId',\n" +
        "        displayName: 'displayName_',\n" +
        "        title: 'title_',\n" +
        "        description: 'description',\n" +
        "        workItemIds: ['globalId'],\n" +
        "    })('testName', async t => {\n" +
        "        // See work item [globalId] for detailed steps description\n" +
        "        // Pre:\n" +
        "        //   preconditions\n" +
        "        // Steps:\n" +
        "        //   testSteps\n" +
        "        // Post:\n" +
        "        //   postconditions\n" +
        "    });";

    public static final Function<Long, String> comparator =
        globalId -> "workItemIds: ['" + globalId + "'],";

    public static String getNewSnippetTestCafe(Object userObject) {
        TmsNodeModel model = (TmsNodeModel) userObject;
        StringBuilder builder = new StringBuilder();
        String testName = CodeSnippetUtils.getTestName(model);

        for (String line : CODE_SNIPPET.lines().toList()) {
            String modifiedLine = line
                .replace("testName", testName)
                .replace("globalId", String.valueOf(model.getGlobalId()))
                .replace("title_", testName)
                .replace("displayName_", testName);

            modifiedLine = CodeSnippetUtils.tryUpdateLineWithSteps(modifiedLine, model);

            if (!modifiedLine.isBlank()) {
                builder.append(modifiedLine).append("\n");
            }
        }

        return builder.toString().stripIndent();
    }
}
