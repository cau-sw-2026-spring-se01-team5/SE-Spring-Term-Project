package mock.model;

public class MockProjectData {

    private final Integer projectId;
    private String title;

    public MockProjectData(Integer projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    public Integer projectId() {
        return projectId;
    }

    public String title() {
        return title;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}