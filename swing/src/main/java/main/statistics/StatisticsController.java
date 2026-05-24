package main.statistics;

import enums.issue.v1.IssueStatus;
import main.header.HeaderView;
import main.issue.IssueView;
import statistics.dto.getDailyIssueCounts.v1.DailyIssueCountOutput;

import issue.dto.getIssueList.v1.IssueSummaryOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {

    private final HeaderView view;
    private final IssueView issueView;

    public StatisticsController(
            HeaderView view,
            IssueView issueView
    ) {
        this.view = view;
        this.issueView = issueView;
    }

    public void openStatistics() {
        List<IssueSummaryOutput> issues = issueView.getVisibleIssues();
        if (issues == null) {
            issues = List.of();
        }

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (IssueStatus status : IssueStatus.values()) {
            long count = issues.stream()
                    .filter(issue -> issue.status() == status)
                    .count();
            statusCounts.put(status.name(), count);
        }

        Map<LocalDate, Long> dailyGrouped = issues.stream()
                .filter(issue -> issue.reportedDate() != null)
                .collect(Collectors.groupingBy(
                        issue -> issue.reportedDate().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        List<DailyIssueCountOutput> dailyCounts = dailyGrouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> new DailyIssueCountOutput(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));

        view.showStatistics(statusCounts, dailyCounts);
    }
}
