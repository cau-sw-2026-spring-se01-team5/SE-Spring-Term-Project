package auth;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import ui.UiTheme;

/* 로그인 화면의 실제 UI 구현체 */
/* 로그인 입력창 생성
    버튼의 이벤트 연결
    메세지 출력
    입력값 제공   */

public class LoginPanel extends JPanel implements LoginView {

    // id 입력 칸
    private final JTextField loginIdField = new JTextField(20);
    // pw 입력 칸
    private final JPasswordField passwordField = new JPasswordField(20);
    // 로그인 버튼
    private final JButton loginButton = new JButton("로그인");
    // 로그인 버튼 클릭 시 실행될 코드 이벤트 핸들러
    // ActionListener를 보통 사용하지만 Runnable을 사용하면 swing에 덜 의존하게 됨
    private Runnable loginHandler;
    private Consumer<Integer> loginSuccessHandler;

    public LoginPanel() {
        setLayout(new GridBagLayout()); // 패널 레이아웃을 GridBagLayout으로 지정
        setBackground(UiTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // 여백 지정
        GridBagConstraints gbc = new GridBagConstraints(); // GridBagLayout 배치 규칙 지정 위한 객체
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백 지정 -> 마진인듯..?
        gbc.fill = GridBagConstraints.HORIZONTAL; // 가로 방향 배치 기준
        JLabel titleLabel = new JLabel("Issue Tracking System"); // 상단 제목
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // 가운데 정렬

        UiTheme.styleTextField(loginIdField);
        UiTheme.stylePasswordField(passwordField);
        UiTheme.stylePrimaryButton(loginButton);

        // 현재 컴포넌트 지정 위치.
        // 0행 0열
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 열 2칸 사용
        gbc.gridwidth = 2;
        add(titleLabel, gbc); // 패널에 제목 추가

        // 이후 컴포넌트들은 열 1칸만 사용하도록
        gbc.gridwidth = 1;
        // 1행 0열
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel idLabel = new JLabel("ID");
        idLabel.setForeground(Color.BLACK);
        add(idLabel, gbc); // ID 라벨 추가

        // 1행 1열
        gbc.gridx = 1;
        add(loginIdField, gbc); // id 입력창 추가

        // 2행 0열
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel pwLabel = new JLabel("Password");
        pwLabel.setForeground(Color.BLACK);
        add(pwLabel, gbc); // pw 라벨 추가

        // 2행 1열
        gbc.gridx = 1;
        add(passwordField, gbc); // pw 필드 추가

        // 3행 0열
        gbc.gridy = 3;
        gbc.gridx = 0;
        // 2칸 차지
        gbc.gridwidth = 2;
        add(loginButton, gbc); // 로그인 버튼 추가
        // 버튼에 이벤트 핸들러 추가
        loginButton.addActionListener(e -> {
            if (loginHandler != null) {
                loginHandler.run();
            }
        });
    }

    public void setLoginSuccessHandler(Consumer<Integer> loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    // 입력된 id 가져오기
    @Override
    public String getLoginId() {
        return loginIdField.getText().trim();
    }
    // 입력된 pw 가져오기
    @Override
    public String getPassword() {
        return new String(
                passwordField.getPassword()
        );
    }

    // 로그인 버튼 클릭 시 실행할 코드 등록
    // 구체적인 실행 코드는 LoginController에 있음
    @Override
    public void onLogin(Runnable handler) {
        this.loginHandler = handler;
    }
    // 로그인 관련해서 노출시킬 메세지 띄우기
    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    // 비밀번호 입력창 초기화
    // 로그인 실패시 동작
    @Override
    public void clearPassword() {
        passwordField.setText("");
    }

    // 로그인 성공 시 호출할 함수
    @Override
    public void moveToMainPage(Integer userId) {
        if (loginSuccessHandler != null) {
            loginSuccessHandler.accept(userId);
            return;
        }
        JOptionPane.showMessageDialog(this, "로그인 되었습니다. User : " + userId);
    }
}
