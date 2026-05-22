package controller.statistics.v1;

import enums.issue.v1.IssueStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.IssueFilter;
import repository.IssueRepository;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StatisticsImpl implements Statistics {

    @NonNull
    private final IssueRepository issueRepository;

    @Override
    public CountByStatusOutput countByStatus(CountByStatusInput input) {
        try {
            IssueStatus targetStatus = input.status();
            if (targetStatus == null) {
                return new CountByStatusOutput(false, "상태 값이 필요합니다.", 0);
            }

            long count = issueRepository.search(new IssueFilter(
                    input.projectId(),
                    null,
                    null,
                    null,
                    targetStatus,
                    null,
                    null
            )).size();

            return new CountByStatusOutput(true, "상태별 이슈 수 조회 성공", count);
        } catch (Exception e) {
            return new CountByStatusOutput(false, "상태별 이슈 수 조회 실패: " + e.getMessage(), 0);
        }
    }

    @Override
    public GetDailyIssueCountsOutput getDailyIssueCounts(GetDailyIssueCountsInput input) {
        try {
            Map<String, Long> grouped = issueRepository.search(new IssueFilter(
                    input.projectId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            )).stream()
                    .collect(Collectors.groupingBy(
                            issue -> issue.getReportedDate().toLocalDate().toString(),
                            LinkedHashMap::new,
                            Collectors.counting()
                    ));

            List<DailyIssueCountOutput> counts = grouped.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                    .map(entry -> new DailyIssueCountOutput(entry.getKey(), entry.getValue()))
                    .toList();

            return new GetDailyIssueCountsOutput(true, "일별 이슈 수 조회 성공", counts);
        } catch (Exception e) {
            return new GetDailyIssueCountsOutput(false, "일별 이슈 수 조회 실패: " + e.getMessage(), List.of());
        }
    }
}
