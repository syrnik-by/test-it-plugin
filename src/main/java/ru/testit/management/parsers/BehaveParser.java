package ru.testit.management.parsers;

import ru.testit.management.parsers.models.MatchInfo;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BehaveParser {
    private static final String ANNOTATION_SEPARATOR = "\\.";
    private static final String KEY_VALUE_SEPARATOR = ":";
    private static final String EVERYTHING_AFTER_ANNOTATION = "[^\\s]{1,}";
    private static final String ALLURE_OBJECT = "allure";
    private static final String ALLURE_METHOD = ALLURE_OBJECT + ANNOTATION_SEPARATOR;
    private static final String ALLURE_LINK_LABEL_NAME = "link";
    private static final String ALLURE_ISSUE_LABEL_NAME = "issue";
    private static final String ALLURE_TMS_LABEL_NAME = "tms";
    private static final String ALLURE_LINK = ALLURE_METHOD + ALLURE_LINK_LABEL_NAME + EVERYTHING_AFTER_ANNOTATION;
    private static final String ALLURE_ISSUE = ALLURE_METHOD + ALLURE_ISSUE_LABEL_NAME + EVERYTHING_AFTER_ANNOTATION;
    private static final String ALLURE_TESTCASE = ALLURE_METHOD + ALLURE_TMS_LABEL_NAME + EVERYTHING_AFTER_ANNOTATION;
    private static final String ALLURE_EPIC_LABEL_NAME = "epic";
    private static final String ALLURE_STORY_LABEL_NAME = "story";
    private static final String ALLURE_PARENT_SUITE_LABEL_NAME = "parentSuite";
    private static final String ALLURE_SUITE_LABEL_NAME = "suite";
    private static final String ALLURE_SUB_SUITE_LABEL_NAME = "subSuite";
    private static final String ALLURE_PACKAGE_LABEL_NAME = "package";
    private static final String ALLURE_TEST_CLASS_LABEL_NAME = "testClass";
    private static final String ALLURE_TEST_METHOD_LABEL_NAME = "testMethod";
    private static final String LABEL_OTHER_FUNCTIONS_NAMES = "(?!" + ALLURE_EPIC_LABEL_NAME + "|" +
            ALLURE_STORY_LABEL_NAME + "|" + ALLURE_PARENT_SUITE_LABEL_NAME + "|" + ALLURE_SUITE_LABEL_NAME + "|" +
            ALLURE_SUB_SUITE_LABEL_NAME + "|" + ALLURE_PACKAGE_LABEL_NAME + "|" + ALLURE_TEST_CLASS_LABEL_NAME + "|" +
            ALLURE_TEST_METHOD_LABEL_NAME + ")";
    private static final String ALLURE_LABEL = ALLURE_METHOD + "label" + "[" + ANNOTATION_SEPARATOR + "|" + KEY_VALUE_SEPARATOR + "]";
    private static final String ALLURE_EPIC = ALLURE_LABEL + ALLURE_EPIC_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_STORY = ALLURE_LABEL + ALLURE_STORY_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_PARENT_SUITE = ALLURE_LABEL + ALLURE_PARENT_SUITE_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_SUITE = ALLURE_LABEL + ALLURE_SUITE_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_SUB_SUITE = ALLURE_LABEL + ALLURE_SUB_SUITE_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_PACKAGE = ALLURE_LABEL + ALLURE_PACKAGE_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_TEST_CLASS = ALLURE_LABEL + ALLURE_TEST_CLASS_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_TEST_METHOD = ALLURE_LABEL + ALLURE_TEST_METHOD_LABEL_NAME + KEY_VALUE_SEPARATOR;
    private static final String ALLURE_OTHER_FUNCTIONS_LABELS = ALLURE_LABEL + LABEL_OTHER_FUNCTIONS_NAMES + EVERYTHING_AFTER_ANNOTATION;

    private static final String LINK_NAME_ANNOTATION_NAME = "name";
    private static final String LINK_URL_ANNOTATION_NAME = "url";
    private static final String LINK_NAME_ANNOTATION = "(\\.(?!" + ALLURE_LINK_LABEL_NAME + "|" + ALLURE_ISSUE_LABEL_NAME + "|" + ALLURE_TMS_LABEL_NAME + ")(?<" + LINK_NAME_ANNOTATION_NAME + ">[^\\s.:]+))?:";
    private static final String LINK_URL_ANNOTATION = ":(?<" + LINK_URL_ANNOTATION_NAME + ">\\S+)";
    private static final String LABEL_VALUE_ANNOTATION_NAME = "value";
    private static final String LABEL_VALUE_ANNOTATION = "label[.|:](?<" + LABEL_VALUE_ANNOTATION_NAME + ">\\S+)";

    private static final String IMPORT_ALLURE_OBJECT = "import " + ALLURE_OBJECT;
    private static final String EVERYTHING_IN_PARENTHESES = "\\([\\s\\S][^)]{1,}\\)";
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

    private static final String TMS_LABELS = "Labels=";
    private static final String TMS_LINKS = "Links=";
    private static final String TMS_NAMESPACE = "NameSpace=";
    private static final String TMS_CLASSNAME = "ClassName=";
    private static final String TMS_DISPLAY_NAME = "DisplayName=";
    private static final String TMS_OBJECT = "testit";
    private static final String METHOD_SEPARATOR_OBJECT = ".";
    private static final String ANNOTATION_SEPARATOR_OBJECT = ",";
    private static final String PARAMETERS_SEPARATOR_OBJECT = ", ";
    private static final String TMS_METHOD_OBJECT = TMS_OBJECT + METHOD_SEPARATOR_OBJECT;
    private static final String IMPORT_TMS_OBJECT = "import " + TMS_OBJECT;
    private static final String TMS_STEP = TMS_METHOD_OBJECT + "step";
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
        patternActions.put(Pattern.compile(ALLURE_OTHER_FUNCTIONS_LABELS, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseOtherFunctionsLabels);
        patternActions.put(Pattern.compile(ALLURE_STEP, Pattern.MULTILINE), TMS_STEP);
        patternActions.put(Pattern.compile(ALLURE_LINK, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_ISSUE, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_TESTCASE, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkAnnotation);
        patternActions.put(Pattern.compile(ALLURE_PARENT_SUITE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_SUITE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_SUB_SUITE, Pattern.MULTILINE), TMS_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_EPIC, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_STORY, Pattern.MULTILINE), TMS_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_PACKAGE, Pattern.MULTILINE), TMS_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_TEST_CLASS, Pattern.MULTILINE), TMS_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_TEST_METHOD, Pattern.MULTILINE), TMS_DISPLAY_NAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TITLE, Pattern.MULTILINE), TMS_ADD_DISPLAY_NAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_DESCRIPTION, Pattern.MULTILINE), TMS_ADD_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_DESCRIPTION_HTML, Pattern.MULTILINE), TMS_ADD_DESCRIPTION);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_LINK, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ISSUE, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TESTCASES, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseLinkMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_TAG, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_LABEL, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ID, Pattern.MULTILINE), TMS_ADD_LABELS);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_EPIC, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_FEATURE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_STORY, Pattern.MULTILINE), TMS_ADD_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_PARENT_SUITE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_SUITE, Pattern.MULTILINE), TMS_ADD_NAMESPACE);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_SUB_SUITE, Pattern.MULTILINE), TMS_ADD_CLASSNAME);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ATTACHMENT_WRITE, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseWriteAttachMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_ATTACHMENT_READ, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseReadAttachMethod);
        patternActions.put(Pattern.compile(ALLURE_DYNAMIC_PARAMETER, Pattern.MULTILINE), (Function<MatchInfo, String>) BehaveParser::parseParameterMethod);
    }

    public static List<Pattern> getPatterns() {
        return new ArrayList<>(patternActions.keySet());
    }

    @SuppressWarnings("unchecked")
    public static String parse(String line, MatchInfo matchInfo) {
        for (Map.Entry<Pattern, Object> entry : patternActions.entrySet()) {
            if (entry.getKey().matcher(line).matches()) {
                Object action = entry.getValue();
                if (action instanceof String) return (String) action;
                if (action instanceof Function) return ((Function<MatchInfo, String>) action).apply(matchInfo);
            }
        }
        throw new RuntimeException("No matching Allure pattern found in line \"" + line + "\"");
    }

    private static String parseOtherFunctionsLabels(MatchInfo matchInfo) {
        Matcher m = Pattern.compile(LABEL_VALUE_ANNOTATION).matcher(matchInfo.getText());
        if (!m.find()) throw new RuntimeException("Can't get value from annotation " + matchInfo.getText());
        return TMS_LABELS + m.group(LABEL_VALUE_ANNOTATION_NAME);
    }

    private static String parseLinkAnnotation(MatchInfo matchInfo) {
        Matcher urlMatcher = Pattern.compile(LINK_URL_ANNOTATION).matcher(matchInfo.getText());
        if (!urlMatcher.find()) throw new RuntimeException("Can't get url from annotation " + matchInfo.getText());
        String url = urlMatcher.group(LINK_URL_ANNOTATION_NAME);

        Matcher nameMatcher = Pattern.compile(LINK_NAME_ANNOTATION).matcher(matchInfo.getText());
        String titleBlock = nameMatcher.find() && nameMatcher.group(LINK_NAME_ANNOTATION_NAME) != null
                ? ANNOTATION_SEPARATOR_OBJECT + "\"title\":\"" + nameMatcher.group(LINK_NAME_ANNOTATION_NAME) + "\"" : "";
        return TMS_LINKS + "{\"url\":\"" + url + "\"" + titleBlock + "}";
    }

    private static String parseLinkMethod(MatchInfo matchInfo) {
        Matcher urlMatcher = Pattern.compile(LINK_URL_PARAMETER).matcher(matchInfo.getText());
        if (!urlMatcher.find()) throw new RuntimeException("Can't get url from method " + matchInfo.getText());
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
