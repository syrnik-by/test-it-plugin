package ru.testit.management.snippet;

import ru.testit.management.utils.CodeSnippetUtils;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class MochaSnippet {

    private static final String CODE_SNIPPET =
        "    it(\"testName\", function () {\n" +
        "        this.externalId = \"externalId\";\n" +
        "        this.displayName = \"displayName_\";\n" +
        "        this.title = \"title_\";\n" +
        "        this.description = \"description\";\n" +
        "        this.workItemsIds = [\"globalId\"];\n" +
        "        // See work item [globalId] for detailed steps description\n" +
        "        // Pre:\n" +
        "        //   preconditions\n" +
        "        // Steps:\n" +
        "        //   testSteps\n" +
        "        // Post:\n" +
        "        //   postconditions\n" +
        "    });";

    public static final Function<Long, String> comparator =
        globalId -> "this.workItemsIds = [\"" + globalId + "\"];";

    public static String getNewSnippetMocha(Object userObject) {
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
