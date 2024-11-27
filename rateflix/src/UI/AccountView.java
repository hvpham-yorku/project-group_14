package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

public class AccountView {
    private JTextField descriptionField;
    private JButton backButton, changePicButton, setPic;
    private JFrame frame;

    public AccountView() {
        initialize();
    }

    void initialize() {
        frame = new JFrame("Account View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        frame.add(createUserDetails(), BorderLayout.WEST);
        frame.add(createDescription(), BorderLayout.EAST);

        frame.setVisible(true);
    }

    private JPanel createDescription() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        descriptionField = new JTextField();
        JLabel desc = new JLabel("Your Description:");
        descriptionField.setPreferredSize(new Dimension(400,200));
        gbc.gridy = 0;
        panel.add(desc, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(descriptionField,gbc);
        return panel;
    }

    private JPanel createUserDetails() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel userDetailsLabel = new JLabel("User Details Section");
        panel.add(userDetailsLabel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        new AccountView();
    }
}
