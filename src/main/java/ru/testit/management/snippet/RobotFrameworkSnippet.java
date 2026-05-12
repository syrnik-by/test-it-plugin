package ru.testit.management.snippet;

import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.utils.StringUtils;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class RobotFrameworkSnippet {

    private static final String CODE_SNIPPET =
        "    testName\n" +
        "        [Tags]  testit.externalID:externalId\n" +
        "        ...     testit.displayName:displayName_\n" +
        "        ...     testit.title:title_\n" +
        "        ...     testit.description:description\n" +
        "        ...     testit.workitemsID:globalId\n" +
        "        # See work item [globalId] for detailed steps description\n" +
        "        # Pre:\n" +
        "        #   preconditions\n" +
        "        # Steps:\n" +
        "        #   testSteps\n" +
        "        # Post:\n" +
        "        #   postconditions";

    public static final Function<Long, String> comparator =
        globalId -> "testit.workitemsID:" + globalId;

    public static String getNewSnippetRobotFramework(Object userObject) {
        TmsNodeModel model = (TmsNodeModel) userObject;
        StringBuilder builder = new StringBuilder();
        String testName = CodeSnippetUtils.getTestName(model);

        for (String line : CODE_SNIPPET.lines().toList()) {
            String modifiedLine = line
                .replace("testName", testName)
                .replace("globalId", String.valueOf(model.getGlobalId()))
                .replace("title_", StringUtils.spacesToSnakeCase(testName))
                .replace("displayName_", StringUtils.spacesToSnakeCase(testName));

            modifiedLine = CodeSnippetUtils.tryUpdateLineWithSteps(modifiedLine, model);

            if (!modifiedLine.isBlank()) {
                builder.append(modifiedLine).append("\n");
            }
        }

        return builder.toString().stripIndent();
    }
}
