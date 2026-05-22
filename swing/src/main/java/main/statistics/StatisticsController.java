package main.statistics;

import enums.issue.v1.IssueStatus;
import main.header.HeaderView;
import main.support.getCurrentProj;
import session.UserSession;
import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;
import statistics.v1.Statistics;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsController {

    private final HeaderView view;
    private final Statistics statisticsService;
    private final getCurrentProj getCurrentProj;

    public StatisticsController(
            HeaderView view,
            Statistics statisticsService,
            UserSession session
    ) {
        this.view = view;
        this.statisticsService = statisticsService;
        this.getCurrentProj = new getCurrentProj(session);
    }

    public void openStatistics() {
        Integer projectId = getCurrentProj.requireProjectId(view::showMessage);
        if (projectId == null) {
            return;
        }

        Map<String, Long> statusCounts = new LinkedHashMap<>();

        for (IssueStatus status : IssueStatus.values()) {
            var countOutput = statisticsService.countByStatus(new CountByStatusInput(projectId, status));
            if (!countOutput.success()) {
                view.showMessage(countOutput.message());
                return;
            }
            statusCounts.put(status.name(), countOutput.count());
        }

        var dailyOutput = statisticsService.getDailyIssueCounts(new GetDailyIssueCountsInput(projectId));
        if (!dailyOutput.success()) {
            view.showMessage(dailyOutput.message());
            return;
        }

        view.showStatistics(statusCounts, dailyOutput.counts());
    }
}
