import java.awt.GridLayout;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginForm extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginForm() {
        setTitle("Вход");
        setSize(360, 160);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 8, 8));

        JButton loginButton = new JButton("Войти");
        add(new JLabel("Логин"));
        add(usernameField);
        add(new JLabel("Пароль"));
        add(passwordField);
        add(new JPanel());
        add(loginButton);

        loginButton.addActionListener(event -> login());
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String encodedAuth = Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
        if (!TaskManagerForm.canOpen(encodedAuth)) {
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
            return;
        }
        new TaskManagerForm(encodedAuth).setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
