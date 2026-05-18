package projectselect;

import project.dto.getProjectList.v1.ProjectInfoOutput;

import java.util.List;

public interface ProjectSelectView {

    // 프로젝트 목록을 화면에 표시
    void setProjects(List<ProjectInfoOutput> projects);

    // 선택한 프로젝트 id 반환
    Integer getSelectedProjectId();

    // 프로젝트 생성 시 새 프로젝트 이름 값 받아오기
    String getNewProjectTitleInput();

    // 프로젝트 수정 시 이름 받아오기
    String getUpdateProjectTitleInput();

    // 이벤트 등록 객체
    void onLoadProjects(Runnable handler);

    void onCreateProject(Runnable handler);

    void onUpdateProject(Runnable handler);

    void onDeleteProject(Runnable handler);

    void onEnterProject(Runnable handler);

    void onLogout(Runnable handler);

    // 관리자 권한 여부에 따라 UI 제어
    void applyAdminPermission(boolean admin);

    // 메세지 팝업
    void showMessage(String message);
}
