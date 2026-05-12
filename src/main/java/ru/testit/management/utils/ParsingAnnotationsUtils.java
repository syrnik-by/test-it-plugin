package ru.testit.management.utils;

import ru.testit.management.enums.FrameworkOption;
import ru.testit.management.parsers.BehaveParser;
import ru.testit.management.parsers.PytestParser;
import ru.testit.management.parsers.RobotFrameworkParser;
import ru.testit.management.parsers.models.MatchInfo;
import ru.testit.management.windows.settings.TmsSettingsState;

import java.util.List;
import java.util.regex.Pattern;

public class ParsingAnnotationsUtils {

    public static List<Pattern> getAllPatterns() {
        String framework = TmsSettingsState.getInstance().getFramework();
        if (FrameworkOption.PYTEST.toString().equals(framework)) {
            return PytestParser.getPatterns();
        } else if (FrameworkOption.ROBOTFRAMEWORK.toString().equals(framework)) {
            return RobotFrameworkParser.getPatterns();
        } else if (FrameworkOption.BEHAVE.toString().equals(framework)) {
            return BehaveParser.getPatterns();
        } else {
            return PytestParser.getPatterns();
        }
    }

    public static String parse(String allureCode, MatchInfo matchInfo) {
        String framework = TmsSettingsState.getInstance().getFramework();
        if (FrameworkOption.PYTEST.toString().equals(framework)) {
            return PytestParser.parse(allureCode, matchInfo);
        } else if (FrameworkOption.ROBOTFRAMEWORK.toString().equals(framework)) {
            return RobotFrameworkParser.parse(allureCode, matchInfo);
        } else if (FrameworkOption.BEHAVE.toString().equals(framework)) {
            return BehaveParser.parse(allureCode, matchInfo);
        } else {
            return PytestParser.parse(allureCode, matchInfo);
        }
    }
}
