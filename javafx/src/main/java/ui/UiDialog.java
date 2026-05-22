package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/*
 * JavaFX 화면에서 공통으로 사용하는 다이얼로그 스타일 클래스이다.
 *
 * 기본 Alert 대신 같은 여백, 테두리, 버튼 스타일을 적용하여 UI 느낌을 통일한다.
 */
public final class UiDialog {

    private UiDialog() {
    }

    public static void showInfo(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().add(okButtonType());
        dialog.getDialogPane().setContent(messageContent(title, content, "#111827"));
        styleDialog(dialog);
        dialog.showAndWait();
    }

    public static void showWarning(String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("알림");
        dialog.getDialogPane().getButtonTypes().add(okButtonType());
        dialog.getDialogPane().setContent(messageContent("알림", content, "#b91c1c"));
        styleDialog(dialog);
        dialog.showAndWait();
    }

    public static void styleDialog(Dialog<?> dialog) {
        DialogPane pane = dialog.getDialogPane();
        pane.setPadding(new Insets(18));
        pane.setStyle(
                "-fx-background-color: #f4f6f8;" +
                        "-fx-border-color: #d0d7de;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        pane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) pane.lookupButton(buttonType);
            if (button != null) {
                styleDialogButton(button, buttonType.getButtonData().isDefaultButton());
            }
        });
    }

    public static ButtonType okButtonType() {
        return new ButtonType("확인", ButtonBar.ButtonData.OK_DONE);
    }

    public static ButtonType cancelButtonType() {
        return new ButtonType("취소", ButtonBar.ButtonData.CANCEL_CLOSE);
    }

    private static VBox messageContent(String title, String content, String titleColor) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + titleColor + ";"
        );

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(460, Math.min(360, Math.max(140, content.length() * 2)));
        textArea.setStyle(
                "-fx-control-inner-background: white;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: #111827;"
        );

        VBox contentBox = new VBox(12, titleLabel, textArea);
        contentBox.setPadding(new Insets(4));
        return contentBox;
    }

    private static void styleDialogButton(Button button, boolean primary) {
        if (primary) {
            button.setStyle(
                    "-fx-background-color: #2563eb;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 8 18 8 18;"
            );
        } else {
            button.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-text-fill: #111827;" +
                            "-fx-border-color: #d1d5db;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 8 18 8 18;"
            );
        }
    }
}
