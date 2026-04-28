package ru.testit.management.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public final class TmsIcons {
    public static final Icon CheckList = IconLoader.getIcon("/icons/check_list/checkList.svg", TmsIcons.class);
    public static final Icon CheckListAutomated = IconLoader.getIcon("/icons/check_list/checkListAutomated.svg", TmsIcons.class);
    public static final Icon SharedStep = IconLoader.getIcon("/icons/shared_step/sharedStep.svg", TmsIcons.class);
    public static final Icon SharedStepAutomated = IconLoader.getIcon("/icons/shared_step/sharedStepAutomated.svg", TmsIcons.class);
    public static final Icon TestCase = IconLoader.getIcon("/icons/test_case/testCase.svg", TmsIcons.class);
    public static final Icon TestCaseAutomated = IconLoader.getIcon("/icons/test_case/testCaseAutomated.svg", TmsIcons.class);

    private TmsIcons() {}
}
