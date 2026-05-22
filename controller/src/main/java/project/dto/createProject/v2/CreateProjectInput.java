package project.dto.createProject.v2;

public record CreateProjectInput(
        Integer requesterUserId,
        String title
) {

}