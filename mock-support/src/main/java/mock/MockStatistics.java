package mock;

import enums.issue.v1.IssueStatus;
import mock.model.MockIssueData;
import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.countByStatus.v1.CountByStatusOutput;
import statistics.dto.getDailyIssueCounts.v1.DailyIssueCountOutput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsOutput;
import statistics.v1.Statistics;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MockStatistics implements Statistics {

    private final MockDatabase database;

    public MockStatistics(MockDatabase database) {
        this.database = database;
    }

    @Override
    public CountByStatusOutput countByStatus(CountByStatusInput input) {
        IssueStatus targetStatus = input.status();
        if (targetStatus == null) {
            return new CountByStatusOutput(false, "상태 값이 필요합니다.", 0);
        }

        long count = database.issues().values().stream()
                .filter(issue -> Objects.equals(issue.projectId(), input.projectId()))
                .filter(issue -> issue.status() == targetStatus)
                .count();

        return new CountByStatusOutput(true, "상태별 이슈 수 조회 성공", count);
    }

    @Override
    public GetDailyIssueCountsOutput getDailyIssueCounts(GetDailyIssueCountsInput input) {
        Map<String, Long> grouped = database.issues().values().stream()
                .filter(issue -> Objects.equals(issue.projectId(), input.projectId()))
                .collect(Collectors.groupingBy(
                        issue -> issue.reportedDate().toLocalDate().toString(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        List<DailyIssueCountOutput> counts = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> new DailyIssueCountOutput(entry.getKey(), entry.getValue()))
                .toList();

        return new GetDailyIssueCountsOutput(true, "일별 이슈 수 조회 성공", counts);
    }
}
