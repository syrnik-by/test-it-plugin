package ru.testit.management.snippet;

import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.utils.StringUtils;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class JunitSnippet {

    private static final String CODE_SNIPPET =
        "    @WorkItemIds(\"globalId\")\n" +
        "    @Test\n" +
        "    public void testName() {\n" +
        "        // See work item [globalId] for detailed steps description\n" +
        "        // Pre:\n" +
        "        //   preconditions\n" +
        "        // Steps:\n" +
        "        //   testSteps\n" +
        "        // Post:\n" +
        "        //   postconditions\n" +
        "    }";

    public static final Function<Long, String> comparator =
        globalId -> "@WorkItemIds(\"" + globalId + "\")";

    public static String getNewSnippetJunit(Object userObject) {
        TmsNodeModel model = (TmsNodeModel) userObject;
        StringBuilder builder = new StringBuilder();

        for (String line : CODE_SNIPPET.lines().toList()) {
            String modifiedLine = line
                .replace("testName", StringUtils.spacesToCamelCase(CodeSnippetUtils.getTestName(model)))
                .replace("globalId", String.valueOf(model.getGlobalId()));

            modifiedLine = CodeSnippetUtils.tryUpdateLineWithSteps(modifiedLine, model);

            if (!modifiedLine.isBlank()) {
                builder.append(modifiedLine).append("\n");
            }
        }

        return builder.toString().stripIndent();
    }
}
