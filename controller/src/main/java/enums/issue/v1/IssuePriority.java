package enums.issue.v1;

public enum IssuePriority {
    BLOCKER, // 가장 치명적인 문제 (시스템 중단)
    CRITICAL, // 심각하지만 시스템 중단까지는 아님
    MAJOR, // 중요한 기능 문제
    MINOR, // 사소한 기능 문제
    TRIVIAL // 사소한 문제
}