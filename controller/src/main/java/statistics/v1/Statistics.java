package statistics.v1;

import statistics.dto.countByStatus.v1.CountByStatusInput;
import statistics.dto.countByStatus.v1.CountByStatusOutput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsInput;
import statistics.dto.getDailyIssueCounts.v1.GetDailyIssueCountsOutput;

public interface Statistics {
    CountByStatusOutput countByStatus(CountByStatusInput input);
    GetDailyIssueCountsOutput getDailyIssueCounts(GetDailyIssueCountsInput input);
}
