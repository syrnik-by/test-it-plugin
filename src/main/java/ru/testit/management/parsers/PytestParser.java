package ru.testit.management.parsers;

import ru.testit.management.parsers.models.MatchInfo;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PytestParser {
    private static final String ANNOTATION_SEPARATOR = "\\.";
    private static final String EVERYTHING_IN_PARENTHESES = "\\([\\s\\S][^)]{1,}\\)";
    private static final String ALLURE_OBJECT = "allure";
    private static final String ALLURE_METHOD = ALLURE_OBJECT + ANNOTATION_SEPARATOR;
    private static final String IMPORT_ALLURE_OBJECT = "import " + ALLURE_OBJECT;
    private static final String ALLURE_TITLE = ALLURE_METHOD + "title";
    private static final String ALLURE_DESCRIPTION = ALLURE_METHOD + "description";
    private static final String ALLURE_DESCRIPTION_HTML = ALLURE_METHOD + "description_html";
    private static final String ALLURE_LINK = ALLURE_METHOD + "link" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_ISSUE = ALLURE_METHOD + "issue" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_TESTCASE = ALLURE_METHOD + "testcase" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_TAG = ALLURE_METHOD + "tag";
    private static final String ALLURE_LABEL = ALLURE_METHOD + "label";
    private static final String ALLURE_ID = ALLURE_METHOD + "id";
    private static final String ALLURE_EPIC = ALLURE_METHOD + "epic";
    private static final String ALLURE_FEATURE = ALLURE_METHOD + "feature";
    private static final String ALLURE_STORY = ALLURE_METHOD + "story";
    private static final String ALLURE_PARENT_SUITE = ALLURE_METHOD + "parent_suite";
    private static final String ALLURE_SUITE = ALLURE_METHOD + "suite";
    private static final String ALLURE_SUB_SUITE = ALLURE_METHOD + "sub_suite";
    private static final String ALLURE_STEP = ALLURE_METHOD + "step";
    private static final String ALLURE_DYNAMIC = ALLURE_METHOD + "dynamic" + ANNOTATION_SEPARATOR;
    private static final String ALLURE_DYNAMIC_TITLE = ALLURE_DYNAMIC + "title";
    private static final String ALLURE_DYNAMIC_DESCRIPTION = ALLURE_DYNAMIC + "description";
    private static final String ALLURE_DYNAMIC_DESCRIPTION_HTML = ALLURE_DYNAMIC + "description_html";
    private static final String ALLURE_DYNAMIC_LINK = ALLURE_DYNAMIC + "link" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_DYNAMIC_ISSUE = ALLURE_DYNAMIC + "issue" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_DYNAMIC_TESTCASES = ALLURE_DYNAMIC + "testcase" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_DYNAMIC_TAG = ALLURE_DYNAMIC + "tag";
    private static final String ALLURE_DYNAMIC_LABEL = ALLURE_DYNAMIC + "label";
    private static final String ALLURE_DYNAMIC_ID = ALLURE_DYNAMIC + "id";
    private static final String ALLURE_DYNAMIC_EPIC = ALLURE_DYNAMIC + "epic";
    private static final String ALLURE_DYNAMIC_FEATURE = ALLURE_DYNAMIC + "feature";
    private static final String ALLURE_DYNAMIC_STORY = ALLURE_DYNAMIC + "story";
    private static final String ALLURE_DYNAMIC_PARENT_SUITE = ALLURE_DYNAMIC + "parent_suite";
    private static final String ALLURE_DYNAMIC_SUITE = ALLURE_DYNAMIC + "suite";
    private static final String ALLURE_DYNAMIC_SUB_SUITE = ALLURE_DYNAMIC + "sub_suite";
    private static final String ALLURE_DYNAMIC_PARAMETER = ALLURE_DYNAMIC + "parameter";
    private static final String ALLURE_DYNAMIC_ATTACHMENT_WRITE = ALLURE_METHOD + "attach" + EVERYTHING_IN_PARENTHESES;
    private static final String ALLURE_DYNAMIC_ATTACHMENT_READ = ALLURE_METHOD + "attach" + ANNOTATION_SEPARATOR + "file" + EVERYTHING_IN_PARENTHESES;
    private static final String VARIABLE = "[^'\",\\s)]+";
    private static final String VALUE = "'[^']*'|\"[^\"]*\"";
    private static final String ASSIGNMENT = "\\s*=\\s*";

    private static final String LINK_URL_PARAMETER_NAME = "url";
    private static final String LINK_NAME_PARAMETER_NAME = "name";
    private static final String LINK_PARAMETER_WITHOUT_NAME = "(?!" + LINK_URL_PARAMETER_NAME + ASSIGNMENT + "|" + LINK_NAME_PARAMETER_NAME + ASSIGNMENT + ")";
    private static final String LINK_URL_PARAMETER = "(?:\\(\\s*" + LINK_PARAMETER_WITHOUT_NAME + "|(?<=" + LINK_URL_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + LINK_URL_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";
    private static final String LINK_NAME_PARAMETER = "(?:\\(\\s*(?:" + VARIABLE + "|" + VALUE + ")\\s*,\\s*" + LINK_PARAMETER_WITHOUT_NAME + "|(?<=" + LINK_NAME_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + LINK_NAME_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";

    private static final String ATTACHMENT_BODY_PARAMETER_NAME = "body";
    private static final String ATTACHMENT_SOURCE_PARAMETER_NAME = "source";
    private static final String ATTACHMENT_NAME_PARAMETER_NAME = "name";
    private static final String ATTACHMENT_TYPE_PARAMETER_NAME = "attachment_type";
    private static final String ATTACHMENT_EXTENSION_PARAMETER_NAME = "extension";
    private static final String GENERAL_PARAMETER_WITHOUT_NAME = ATTACHMENT_NAME_PARAMETER_NAME + ASSIGNMENT + "|" + ATTACHMENT_TYPE_PARAMETER_NAME + ASSIGNMENT + "|" + ATTACHMENT_EXTENSION_PARAMETER_NAME + ASSIGNMENT;
    private static final String ATTACHMENT_READ_PARAMETER_WITHOUT_NAME = "(?!" + ATTACHMENT_SOURCE_PARAMETER_NAME + ASSIGNMENT + "|" + GENERAL_PARAMETER_WITHOUT_NAME + ")";
    private static final String ATTACHMENT_WRITE_PARAMETER_WITHOUT_NAME = "(?!" + ATTACHMENT_BODY_PARAMETER_NAME + ASSIGNMENT + "|" + GENERAL_PARAMETER_WITHOUT_NAME + ")";
    private static final String ATTACHMENT_SOURCE_PARAMETER = "(?:\\(\\s*" + ATTACHMENT_READ_PARAMETER_WITHOUT_NAME + "|(?<=" + ATTACHMENT_SOURCE_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + ATTACHMENT_SOURCE_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";
    private static final String ATTACHMENT_BODY_PARAMETER = "(?:\\(\\s*" + ATTACHMENT_WRITE_PARAMETER_WITHOUT_NAME + "|(?<=" + ATTACHMENT_BODY_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + ATTACHMENT_BODY_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";
    private static final String ATTACHMENT_NAME_PARAMETER = "(?:\\(\\s*(?:" + VARIABLE + "|" + VALUE + ")\\s*,\\s*" + ATTACHMENT_READ_PARAMETER_WITHOUT_NAME + "|(?<=" + ATTACHMENT_NAME_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + ATTACHMENT_NAME_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";

    private static final String PARAMETER_NAME_PARAMETER_NAME = "name";
    private static final String PARAMETER_VALUE_PARAMETER_NAME = "value";
    private static final String PARAMETER_PARAMETER_WITHOUT_NAME = "(?!" + PARAMETER_NAME_PARAMETER_NAME + ASSIGNMENT + "|" + PARAMETER_VALUE_PARAMETER_NAME + ASSIGNMENT + ")";
    private static final String PARAMETER_NAME_PARAMETER = "(?:\\(\\s*" + PARAMETER_PARAMETER_WITHOUT_NAME + "|(?<=" + PARAMETER_NAME_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + PARAMETER_NAME_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";
    private static final String PARAMETER_VALUE_PARAMETER = "(?:\\(\\s*(?:" + VARIABLE + "|" + VALUE + ")\\s*,\\s*" + PARAMETER_PARAMETER_WITHOUT_NAME + "|(?<=" + PARAMETER_VALUE_PARAMETER_NAME + ")" + ASSIGNMENT + ")(?<" + PARAMETER_VALUE_PARAMETER_NAME + ">" + VARIABLE + "|" + VALUE + ")";

    private static final String ANNOTATION_SEPARATOR_OBJECT = ".";
    private static final String PARAMETERS_SEPARATOR_OBJECT = ", ";
    private static final String TMS_OBJECT = "testit";
    private static final String IMPORT_TMS_OBJECT = "import " + TMS_OBJECT;
    private static final String TMS_METHOD_OBJECT = TMS_OBJECT + ANNOTATION_SEPARATOR_OBJECT;
    private static final String TMS_DISPLAY_NAME = TMS_METHOD_OBJECT + "displayName";
    private static final String TMS_DESCRIPTION = TMS_METHOD_OBJECT + "description";
    private static final String TMS_LABELS = TMS_METHOD_OBJECT + "labels";
    private static final String TMS_STEP = TMS_METHOD_OBJECT + "step";
    private static final String TMS_LINKS = TMS_METHOD_OBJECT + "links";
    private static final String TMS_NAMESPACE = TMS_METHOD_OBJECT + "nameSpace";
    private static final String TMS_CLASSNAME = TMS_METHOD_OBJECT + "className";
    private static final String TMS_ADD_DISPLAY_NAME = TMS_METHOD_OBJECT + "addDisplayName";
    private static final String TMS_ADD_NAMESPACE = TMS_METHOD_OBJECT + "addNameSpace";
    private static final String TMS_ADD_CLASSNAME = TMS_METHOD_OBJECT + "addClassName";
    private static final String TMS_ADD_DESCRIPTION = TMS_METHOD_OBJECT + "addDescription";
    private static final String TMS_ADD_LABELS = TMS_METHOD_OBJECT + "addLabels";
    private static final String TMS_ADD_LINKS = TMS_METHOD_OBJECT + "addLinks";
    private static final String TMS_ADD_PARAMETER = TMS_METHOD_OBJECT + "addParameter";
    private static final String TMS_ADD_ATTACHMENTS = TMS_METHOD_OBJECT + "addAttachments";

    private static final Map<Pattern, Object> patternActions;

    static {
        patternActions = new LinkedHashMap<>();
        patternActions.put(Pattern.compile(IMPORT_ALLURE_OBJECT, Pattern.MULTILINE), IMPORT_TMS_OBJECT);
        patternActions.put(Pattern.compile(ALLURE_TITLE, Pattern.MULTILINE), TMS_DISPLAY_NAME);
        patternActions.put(Pattern.compile(ALLURE_DESCRIPTION, Pattern.MULTILINE), TMS_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_DESCRIPTION_HTML, Pattern.MULTILINE), TMS_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_TAG, Pattern.MULTILINE), TMS_LABELS);
        patternActions.put(Pattern.compile(ALLURE_LABEL, Pattern.MULTILINE), TMS_LABELS);
        patternActions.put(Pattern.compile(ALLURE_ID, Pattern.MULTILINE), TMS_LABELS);
        patternActions.put(Pattern.compile(ALLURE_STEP, Pattern.MULTILINE), TMS_STEP);
        patternActions.put(Pattern.compile(ALLURE_LINK, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_ISSUE, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_TESTCASE, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_PARENT_SUITE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_SUITE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_SUB_SUITE, Pattern.MULTILINE), TMS_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_EPIC, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_FEATURE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_STORY, Pattern.MULTILINE), TMS_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TITLE, Pattern.MULTILINE), TMS_ADD_DISPLAY_NAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_DESCRIPTION, Pattern.MULTILINE), TMS_ADD_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_DESCRIPTION_HTML, Pattern.MULTILINE), TMS_ADD_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_LINK, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ISSUE, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TESTCASES, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TAG, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_LABEL, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ID, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_EPIC, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_FEATURE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_STORY, Pattern.MULTILINE), TMS_ADD_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_PARENT_SUITE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_SUITE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_SUB_SUITE, Pattern.MULTILINE), TMS_ADD_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ATTACHMENT_WRITE, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseWriteAttachMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ATTACHMENT_READ, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseReadAttachMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_PARAMETER, Pattern.MULTILINE), (Function<MatchInfo, String>) PytestParser::parseParameterMethod);
    }

    public static List<Pattern> getPatterns() {
        return new ArrayList<>(patternActions.keySet());
    }

    @SuppressWarnings("unchecked")
    public static String parse(String line, MatchInfo matchInfo) {
        for (Map.Entry<Pattern, Object> entry : patternActions.entrySet()) {
            if (entry.getKey().matcher(line).matches()) {
                Object action = entry.getValue();
                if (action instanceof String) {
                    return (String) action;
                } else if (action instanceof Function) {
                    return ((Function<MatchInfo, String>) action).apply(matchInfo);
                }
            }
        }
        throw new RuntimeException("No matching Allure pattern found in line \"" + line + "\"");
    }

    private static String parseLinkAnnotation(MatchInfo matchInfo) {
        Matcher urlMatcher = Pattern.compile(LINK_URL_PARAMETER).matcher(matchInfo.getText());
        if (!urlMatcher.find()) throw new RuntimeException("Can't get url from annotation " + matchInfo.getText());
        String url = urlMatcher.group(LINK_URL_PARAMETER_NAME);

        Matcher nameMatcher = Pattern.compile(LINK_NAME_PARAMETER).matcher(matchInfo.getText());
        String titleBlock = nameMatcher.find() ? PARAMETERS_SEPARATOR_OBJECT + "title=" + nameMatcher.group(LINK_NAME_PARAMETER_NAME) : "";

        return TMS_LINKS + "(url=" + url + titleBlock + ")";
    }

    private static String parseLinkMethod(MatchInfo matchInfo) {
        Matcher urlMatcher = Pattern.compile(LINK_URL_PARAMETER).matcher(matchInfo.getText());
        if (!urlMatcher.find()) throw new RuntimeException("Can't get url from annotation " + matchInfo.getText());
        String url = urlMatcher.group(LINK_URL_PARAMETER_NAME);

        Matcher nameMatcher = Pattern.compile(LINK_NAME_PARAMETER).matcher(matchInfo.getText());
        String titleBlock = nameMatcher.find() ? PARAMETERS_SEPARATOR_OBJECT + "title=" + nameMatcher.group(LINK_NAME_PARAMETER_NAME) : "";

        return TMS_ADD_LINKS + "(url=" + url + titleBlock + ")";
    }

    private static String parseWriteAttachMethod(MatchInfo matchInfo) {
        Matcher bodyMatcher = Pattern.compile(ATTACHMENT_BODY_PARAMETER).matcher(matchInfo.getText());
        if (!bodyMatcher.find()) throw new RuntimeException("Can't get body from method " + matchInfo.getText());
        String body = bodyMatcher.group(ATTACHMENT_BODY_PARAMETER_NAME);

        Matcher nameMatcher = Pattern.compile(ATTACHMENT_NAME_PARAMETER).matcher(matchInfo.getText());
        String nameBlock = nameMatcher.find() ? PARAMETERS_SEPARATOR_OBJECT + "name=" + nameMatcher.group(ATTACHMENT_NAME_PARAMETER_NAME) : "";

        return TMS_ADD_ATTACHMENTS + "(" + body + PARAMETERS_SEPARATOR_OBJECT + "is_text=True" + nameBlock + ")";
    }

    private static String parseReadAttachMethod(MatchInfo matchInfo) {
        Matcher sourceMatcher = Pattern.compile(ATTACHMENT_SOURCE_PARAMETER).matcher(matchInfo.getText());
        if (!sourceMatcher.find()) throw new RuntimeException("Can't get source from method " + matchInfo.getText());
        String source = sourceMatcher.group(ATTACHMENT_SOURCE_PARAMETER_NAME);

        Matcher nameMatcher = Pattern.compile(ATTACHMENT_NAME_PARAMETER).matcher(matchInfo.getText());
        String nameBlock = nameMatcher.find() ? PARAMETERS_SEPARATOR_OBJECT + "name=" + nameMatcher.group(ATTACHMENT_NAME_PARAMETER_NAME) : "";

        return TMS_ADD_ATTACHMENTS + "(" + source + nameBlock + ")";
    }

    private static String parseParameterMethod(MatchInfo matchInfo) {
        Matcher nameMatcher = Pattern.compile(PARAMETER_NAME_PARAMETER).matcher(matchInfo.getText());
        if (!nameMatcher.find()) throw new RuntimeException("Can't get name from method " + matchInfo.getText());
        Matcher valueMatcher = Pattern.compile(PARAMETER_VALUE_PARAMETER).matcher(matchInfo.getText());
        if (!valueMatcher.find()) throw new RuntimeException("Can't get value from method " + matchInfo.getText());

        return TMS_ADD_PARAMETER + "(name=" + nameMatcher.group(PARAMETER_NAME_PARAMETER_NAME) + PARAMETERS_SEPARATOR_OBJECT + "value=" + valueMatcher.group(PARAMETER_VALUE_PARAMETER_NAME) + ")";
    }
}
