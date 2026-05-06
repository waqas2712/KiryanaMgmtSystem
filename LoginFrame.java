import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    JTextField     usernameField;
    JPasswordField passwordField;
    JButton        loginButton;
    AuthService    authService;

    public LoginFrame() {

        authService = new AuthService();

        setTitle("Smart Kiryana Shop - Login");
        setSize(380, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centres the window on screen
        setLayout(new BorderLayout());

       
        JLabel titleLabel = new JLabel("Kiryana Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        
        JLabel emptyLabel = new JLabel("");
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(emptyLabel);
        formPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText().trim();
                // getPassword() returns char[] - we convert to String
                String password = new String(passwordField.getPassword());

                // Ask AuthService to check username+password in the DB
                User loggedInUser = authService.login(username, password);

                if (loggedInUser != null) {
                    // SUCCESS: open Dashboard, close this window
                    DashboardFrame dashboard = new DashboardFrame(loggedInUser);
                    dashboard.setVisible(true);
                    dispose(); // closes LoginFrame

                } else {
                    // FAILURE: show error popup
                    JOptionPane.showMessageDialog(
                        null,
                        "Incorrect username or password. Try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    passwordField.setText(""); // clear password field
                }
            }
        });
    }
}