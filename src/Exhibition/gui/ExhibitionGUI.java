package Exhibition.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;

import Exhibition.model.Participant;
import Exhibition.db.DatabaseManager;
import Exhibition.util.InputValidator;

public class ExhibitionGUI extends JFrame {
    private DatabaseManager dbManager;
    private JTextField regIdField, nameField, facultyField, projectField, contactField, emailField;
    private JLabel imageLabel;
    private JButton browseButton, registerButton, searchButton, updateButton, deleteButton, clearButton, exitButton;
    private String imagePath;

    private static final String IMAGE_FOLDER = "images";
    
    // color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BORDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_COLOR = new Color(51, 51, 51);

    public ExhibitionGUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbManager = new DatabaseManager();
        initializeComponents();
        setupUI();

        new File(IMAGE_FOLDER).mkdirs();
    }

    private void initializeComponents() {
        regIdField = createModernTextField();
        nameField = createModernTextField();
        facultyField = createModernTextField();
        projectField = createModernTextField();
        contactField = createModernTextField();
        emailField = createModernTextField();

        imageLabel = new JLabel("No image selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 200));
        imageLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setForeground(new Color(150, 150, 150));
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        browseButton = createStyledButton("Browse Image", PRIMARY_COLOR);
        registerButton = createStyledButton("Register", SUCCESS_COLOR);
        searchButton = createStyledButton("Search", SECONDARY_COLOR);
        updateButton = createStyledButton("Update", WARNING_COLOR);
        deleteButton = createStyledButton("Delete", DANGER_COLOR);
        clearButton = createStyledButton("Clear", new Color(149, 165, 166));
        exitButton = createStyledButton("Exit", new Color(127, 140, 141));
    }

    private void setupUI() {
        setTitle("Victoria University Exhibition Registration System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Setting application icon
        setIconImage(createAppIcon());

        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(248, 249, 250);
                Color color2 = new Color(233, 236, 239);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Form panel with modern styling
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                "Participant Details",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        //form fields
        formPanel.add(createModernLabel("Registration ID:"));
        formPanel.add(regIdField);
        formPanel.add(createModernLabel("Student Name:"));
        formPanel.add(nameField);
        formPanel.add(createModernLabel("Faculty:"));
        formPanel.add(facultyField);
        formPanel.add(createModernLabel("Project Title:"));
        formPanel.add(projectField);
        formPanel.add(createModernLabel("Contact Number:"));
        formPanel.add(contactField);
        formPanel.add(createModernLabel("Email Address:"));
        formPanel.add(emailField);

        // Image panel 
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setOpaque(false);
        imagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                "Project Image",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JPanel browsePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        browsePanel.setOpaque(false);
        browsePanel.add(browseButton);
        imagePanel.add(browsePanel, BorderLayout.SOUTH);

        // Content split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, imagePanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(3);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOpaque(false);

        // Modern button panel
        JPanel buttonPanel = createModernButtonPanel();

        // Assembling main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(splitPane, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Adding action listeners
        browseButton.addActionListener(e -> browseImage());
        registerButton.addActionListener(e -> registerParticipant());
        searchButton.addActionListener(e -> searchParticipant());
        updateButton.addActionListener(e -> updateParticipant());
        deleteButton.addActionListener(e -> deleteParticipant());
        clearButton.addActionListener(e -> clearForm());
        exitButton.addActionListener(e -> System.exit(0));

        add(mainContainer);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Exhibition Registration System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel subtitleLabel = new JLabel("Victoria University", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createModernButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));

        buttonPanel.add(registerButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(PRIMARY_COLOR);
        field.setSelectionColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 100));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private Image createAppIcon() {
        //creating an icon
        int size = 32;
        BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRoundRect(4, 4, size-8, size-8, 8, 8);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "E";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return icon;
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();

            try {
                Path destination = new File(IMAGE_FOLDER + File.separator + fileName).toPath();
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                imagePath = destination.toString();

                ImageIcon icon = new ImageIcon(imagePath);
                Image image = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                imageLabel.setText("");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error copying image: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void registerParticipant() {
        String regId = regIdField.getText().trim();
        String name = nameField.getText().trim();
        String faculty = facultyField.getText().trim();
        String project = projectField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();

        if (InputValidator.hasEmptyFields(regId, name, faculty, project, contact, email)) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!InputValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participant p = new Participant(regId, name, faculty, project, contact, email, imagePath);
        if (dbManager.addParticipant(p)) {
            JOptionPane.showMessageDialog(this, "Participant registered successfully.");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchParticipant() {
        String regId = regIdField.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to search.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participant p = dbManager.searchParticipant(regId);
        if (p != null) {
            nameField.setText(p.getName());
            facultyField.setText(p.getFaculty());
            projectField.setText(p.getProjectTitle());
            contactField.setText(p.getContact());
            emailField.setText(p.getEmail());
            imagePath = p.getImagePath();

            if (imagePath != null && !imagePath.isEmpty()) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                    imageLabel.setText("");
                } else {
                    imageLabel.setIcon(null);
                    imageLabel.setText("Image not found");
                }
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("No image selected");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No participant found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateParticipant() {
        String regId = regIdField.getText().trim();
        String name = nameField.getText().trim();
        String faculty = facultyField.getText().trim();
        String project = projectField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();

        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Participant p = new Participant(regId, name, faculty, project, contact, email, imagePath);
        if (dbManager.updateParticipant(p)) {
            JOptionPane.showMessageDialog(this, "Participant updated successfully.");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteParticipant() {
        String regId = regIdField.getText().trim();
        if (regId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Registration ID to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this participant?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteParticipant(regId)) {
                JOptionPane.showMessageDialog(this, "Participant deleted.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        regIdField.setText("");
        nameField.setText("");
        facultyField.setText("");
        projectField.setText("");
        contactField.setText("");
        emailField.setText("");
        imageLabel.setIcon(null);
        imageLabel.setText("No image selected");
        imagePath = "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExhibitionGUI().setVisible(true));
    }
}
