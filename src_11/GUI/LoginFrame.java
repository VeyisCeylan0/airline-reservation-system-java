package GUI;

import ServicesAndManagers.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField(14);
    private final JPasswordField txtPassword = new JPasswordField(14);

    private final JButton btnLogin = new JButton("Login");
    private final JButton btnRegister = new JButton("Register (Customer)");

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        form.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassword, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnRegister);
        buttons.add(btnLogin);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());

        setVisible(true);
    }

    private void doLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username ve password boş olamaz.");
            return;
        }

        boolean ok = UserManager.login(u, p);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!");
            return;
        }

        dispose();
        new MainFrame();
    }

    private void doRegister() {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();

        Object[] msg = {
                "Username:", u,
                "Password:", p
        };

        int res = JOptionPane.showConfirmDialog(this, msg, "Register Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        String username = u.getText().trim();
        String pass = new String(p.getPassword());

        if (username.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Boş bırakma.");
            return;
        }


        boolean ok = UserManager.registerCustomer(username, pass);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Bu username zaten var!");
            return;
        }

        JOptionPane.showMessageDialog(this, "Kayıt başarılı. Şimdi login ol.");
    }
}
