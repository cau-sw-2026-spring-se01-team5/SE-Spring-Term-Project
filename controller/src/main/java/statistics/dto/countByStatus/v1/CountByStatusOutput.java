package statistics.dto.countByStatus.v1;

public record CountByStatusOutput(
        boolean success,
        String message,
        long count
) {
}
