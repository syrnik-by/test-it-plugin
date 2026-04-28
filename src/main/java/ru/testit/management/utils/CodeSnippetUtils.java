package ru.testit.management.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import ru.testit.kotlin.client.models.StepModel;
import ru.testit.management.enums.FrameworkOption;
import ru.testit.management.snippet.*;
import ru.testit.management.windows.settings.TmsSettingsState;
import ru.testit.management.windows.tools.TmsNodeModel;

import java.util.function.Function;

public class CodeSnippetUtils {

    public static String getNewSnippet(Object userObject) {
        String framework = TmsSettingsState.getInstance().getFramework();
        if (FrameworkOption.BEHAVE.toString().equals(framework)) {
            return GherkinSnippet.getNewSnippetCucumberOrBehaveOrSpecFlow(userObject);
        } else if (FrameworkOption.NOSE.toString().equals(framework)) {
            return PytestOrNoseSnippet.getNewSnippetPytestOrNose(userObject);
        } else if (FrameworkOption.PYTEST.toString().equals(framework)) {
            return PytestOrNoseSnippet.getNewSnippetPytestOrNose(userObject);
        } else if (FrameworkOption.ROBOTFRAMEWORK.toString().equals(framework)) {
            return RobotFrameworkSnippet.getNewSnippetRobotFramework(userObject);
        } else if (FrameworkOption.JUNIT.toString().equals(framework)) {
            return JunitSnippet.getNewSnippetJunit(userObject);
        } else if (FrameworkOption.MSTEST.toString().equals(framework)) {
            return MSTestOrNUnitSnippet.getNewSnippetMSTestOrNUnit(userObject);
        } else if (FrameworkOption.NUNIT.toString().equals(framework)) {
            return MSTestOrNUnitSnippet.getNewSnippetMSTestOrNUnit(userObject);
        } else if (FrameworkOption.XUNIT.toString().equals(framework)) {
            return XUnitSnippet.getNewSnippetXUnit(userObject);
        } else if (FrameworkOption.SPECFLOW.toString().equals(framework)) {
            return GherkinSnippet.getNewSnippetCucumberOrBehaveOrSpecFlow(userObject);
        } else if (FrameworkOption.CODECEPTJS.toString().equals(framework)) {
            return CodeceptJSSnippet.getNewSnippetCodeceptJS(userObject);
        } else if (FrameworkOption.CUCUMBER.toString().equals(framework)) {
            return GherkinSnippet.getNewSnippetCucumberOrBehaveOrSpecFlow(userObject);
        } else if (FrameworkOption.JEST.toString().equals(framework)) {
            return PlaywrightOrJestSnippet.getNewSnippetPlaywrightOrJest(userObject);
        } else if (FrameworkOption.MOCHA.toString().equals(framework)) {
            return MochaSnippet.getNewSnippetMocha(userObject);
        } else if (FrameworkOption.PLAYWRIGHT.toString().equals(framework)) {
            return PlaywrightOrJestSnippet.getNewSnippetPlaywrightOrJest(userObject);
        } else if (FrameworkOption.TESTCAFE.toString().equals(framework)) {
            return TestCafeSnippet.getNewSnippetTestCafe(userObject);
        } else {
            return JunitSnippet.getNewSnippetJunit(userObject);
        }
    }

    public static Function<Long, String> getComparator() {
        String framework = TmsSettingsState.getInstance().getFramework();
        if (FrameworkOption.BEHAVE.toString().equals(framework)) {
            return GherkinSnippet.comparator;
        } else if (FrameworkOption.NOSE.toString().equals(framework)) {
            return PytestOrNoseSnippet.comparator;
        } else if (FrameworkOption.PYTEST.toString().equals(framework)) {
            return PytestOrNoseSnippet.comparator;
        } else if (FrameworkOption.ROBOTFRAMEWORK.toString().equals(framework)) {
            return RobotFrameworkSnippet.comparator;
        } else if (FrameworkOption.JUNIT.toString().equals(framework)) {
            return JunitSnippet.comparator;
        } else if (FrameworkOption.MSTEST.toString().equals(framework)) {
            return MSTestOrNUnitSnippet.comparator;
        } else if (FrameworkOption.NUNIT.toString().equals(framework)) {
            return MSTestOrNUnitSnippet.comparator;
        } else if (FrameworkOption.XUNIT.toString().equals(framework)) {
            return XUnitSnippet.comparator;
        } else if (FrameworkOption.SPECFLOW.toString().equals(framework)) {
            return GherkinSnippet.comparator;
        } else if (FrameworkOption.CODECEPTJS.toString().equals(framework)) {
            return CodeceptJSSnippet.comparator;
        } else if (FrameworkOption.CUCUMBER.toString().equals(framework)) {
            return GherkinSnippet.comparator;
        } else if (FrameworkOption.JEST.toString().equals(framework)) {
            return PlaywrightOrJestSnippet.comparator;
        } else if (FrameworkOption.MOCHA.toString().equals(framework)) {
            return MochaSnippet.comparator;
        } else if (FrameworkOption.PLAYWRIGHT.toString().equals(framework)) {
            return PlaywrightOrJestSnippet.comparator;
        } else if (FrameworkOption.TESTCAFE.toString().equals(framework)) {
            return TestCafeSnippet.comparator;
        } else {
            return JunitSnippet.comparator;
        }
    }

    public static String getTestName(TmsNodeModel model) {
        String testName = model.getName() != null ? model.getName() : "";
        while (!testName.isBlank() && Character.isDigit(testName.charAt(0))) {
            testName = testName.substring(1);
        }
        return testName;
    }

    public static String tryUpdateLineWithSteps(String line, TmsNodeModel model) {
        if (line.contains("preconditions")) {
            String prefix = line.substring(0, line.indexOf("preconditions"));
            return getNewLineWithStepsInserted(prefix, model.getPreconditions());
        } else if (line.contains("testSteps")) {
            String prefix = line.substring(0, line.indexOf("testSteps"));
            return getNewLineWithStepsInserted(prefix, model.getSteps());
        } else if (line.contains("postconditions")) {
            String prefix = line.substring(0, line.indexOf("postconditions"));
            return getNewLineWithStepsInserted(prefix, model.getPostconditions());
        } else {
            return line;
        }
    }

    private static String getNewLineWithStepsInserted(String prefix, Iterable<StepModel> steps) {
        StringBuilder builder = new StringBuilder();
        if (steps != null) {
            for (StepModel step : steps) {
                String stepName = toHumanReadableStepName(prefix, step);
                if (!stepName.isBlank()) {
                    builder.append(stepName).append(System.lineSeparator());
                }
            }
        }
        String result = builder.toString();
        if (result.endsWith(System.lineSeparator())) {
            result = result.substring(0, result.length() - System.lineSeparator().length());
        }
        return result;
    }

    private static String toHumanReadableStepName(String prefix, StepModel step) {
        String stepName = (step.getWorkItem() == null)
            ? (step.getAction() != null ? step.getAction() : "")
            : (step.getWorkItem().getName() != null ? step.getWorkItem().getName() : "");

        stepName = Jsoup.clean(stepName, Safelist.none())
            .lines().reduce((a, b) -> a + " " + b).orElse("");

        return prefix + stepName;
    }
}
