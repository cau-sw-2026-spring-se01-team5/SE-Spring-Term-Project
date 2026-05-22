package main.header;

import enums.user.v1.UserRole;
import statistics.dto.getDailyIssueCounts.v1.DailyIssueCountOutput;

import java.util.Map;

// 메인 화면의 상단 헤더 부분
public interface HeaderView {
    // 현재 로그인한 사용자 정보 표시 메서드
    void setUserInfo(Integer userId, String loginId, UserRole role);
    // 프로젝트 리스트 화면으로 돌아가기 위한 메서드
    void onBackToProjectList(Runnable handler);
    // 통계 팝업 열기 위한 메서드
    void onOpenStatistics(Runnable handler);
    // 로그아웃 하기 위한 메서드
    void onLogout(Runnable handler);
    // 메세지 띄우기 위한 메서드
    void showMessage(String message);
    // 통계 팝업 출력
    void showStatistics(Map<String, Long> statusCounts, java.util.List<DailyIssueCountOutput> dailyCounts);
}
