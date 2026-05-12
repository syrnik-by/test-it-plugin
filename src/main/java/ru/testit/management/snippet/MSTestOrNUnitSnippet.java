package ru.testit.management.snippet;

import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.utils.StringUtils;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class MSTestOrNUnitSnippet {

    private static final String CODE_SNIPPET =
        "    [ExternalId(\"externalId\")]\n" +
        "    [DisplayName(\"displayName_\")]\n" +
        "    [Title(\"title_\")]\n" +
        "    [Tms.Adapter.Attributes.Description(\"description\")]\n" +
        "    [WorkItemIds(\"globalId\")]\n" +
        "    [TestMethod]\n" +
        "    public void testName()\n" +
        "    {\n" +
        "        // See work item [globalId] for detailed steps description\n" +
        "        // Pre:\n" +
        "        //   preconditions\n" +
        "        // Steps:\n" +
        "        //   testSteps\n" +
        "        // Post:\n" +
        "        //   postconditions\n" +
        "    }";

    public static final Function<Long, String> comparator =
        globalId -> "[WorkItemIds(\"" + globalId + "\")]";

    public static String getNewSnippetMSTestOrNUnit(Object userObject) {
        TmsNodeModel model = (TmsNodeModel) userObject;
        StringBuilder builder = new StringBuilder();
        String testName = CodeSnippetUtils.getTestName(model);

        for (String line : CODE_SNIPPET.lines().toList()) {
            String modifiedLine = line
                .replace("testName", StringUtils.spacesToCamelCase(testName))
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
