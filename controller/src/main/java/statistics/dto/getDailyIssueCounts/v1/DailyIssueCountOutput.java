package statistics.dto.getDailyIssueCounts.v1;

public record DailyIssueCountOutput(
        String date,
        long count
) {
}
