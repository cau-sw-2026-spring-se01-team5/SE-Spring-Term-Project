package auth;

public interface LoginView {

    // 화면에 사용자가 입력한 로그인 id 가져오기
    String getLoginId();
    // 화면에 사용자가 입력한 pw 가져오기
    String getPassword();
    // 로그인 버튼 눌렸을 때 실행할 함수 등록
    void onLogin(Runnable handler);
    // 로그인 관련 띄워줄 메세지 출력
    void showMessage(String message);
    // 비밀번호 입력칸 초기화
    void clearPassword();
    // 로그인 성공시 호출할 함수
    void moveToMainPage(Integer userId);
}