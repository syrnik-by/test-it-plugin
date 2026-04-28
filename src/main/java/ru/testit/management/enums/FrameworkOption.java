package ru.testit.management.enums;

public enum FrameworkOption {
    JUNIT {
        @Override
        public String toString() { return "junit"; }
    },
    BEHAVE {
        @Override
        public String toString() { return "behave"; }
    },
    NOSE {
        @Override
        public String toString() { return "nose"; }
    },
    PYTEST {
        @Override
        public String toString() { return "pytest"; }
    },
    ROBOTFRAMEWORK {
        @Override
        public String toString() { return "robotframework"; }
    },
    MSTEST {
        @Override
        public String toString() { return "mstest"; }
    },
    NUNIT {
        @Override
        public String toString() { return "nunit"; }
    },
    XUNIT {
        @Override
        public String toString() { return "xunit"; }
    },
    SPECFLOW {
        @Override
        public String toString() { return "specflow"; }
    },
    CODECEPTJS {
        @Override
        public String toString() { return "codeceptjs"; }
    },
    CUCUMBER {
        @Override
        public String toString() { return "cucumber"; }
    },
    JEST {
        @Override
        public String toString() { return "jest"; }
    },
    MOCHA {
        @Override
        public String toString() { return "mocha"; }
    },
    PLAYWRIGHT {
        @Override
        public String toString() { return "playwright"; }
    },
    TESTCAFE {
        @Override
        public String toString() { return "testcafe"; }
    }
}
