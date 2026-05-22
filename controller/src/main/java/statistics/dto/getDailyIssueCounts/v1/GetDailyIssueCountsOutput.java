package statistics.dto.getDailyIssueCounts.v1;

import java.util.List;

public record GetDailyIssueCountsOutput(
        boolean success,
        String message,
        List<DailyIssueCountOutput> counts
) {
}
