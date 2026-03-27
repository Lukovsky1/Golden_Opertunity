package com.GoldenOpportunity;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * AddRoomPage represents the UI used by a clerk to:
 * - Enter details of a new room
 * - Submit the room to the system
 * - Clear or reset the form
 */
public class AddRoomPage extends JFrame {

    // Input fields for room data
    private JTextField roomNumberField;
    private JTextField rateField;
    private JSpinner bedsSpinner;
    private JCheckBox smokingCheckBox;
    private JComboBox<String> qualityComboBox;
    private JComboBox<String> floorComboBox;
    private JComboBox<String> bedTypeComboBox;

    /**
     * Constructor: initializes the window and layout
     */
    public AddRoomPage() {
        setTitle("Add New Room");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the header section with the page title
     */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Add New Room");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));

        header.add(titleLabel);
        return header;
    }

    /**
     * Builds the main form where the clerk enters room details
     */
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(new Color(245, 245, 245));
        outerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize input components
        roomNumberField = new JTextField(20);
        bedsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        smokingCheckBox = new JCheckBox("Smoking Allowed");
        smokingCheckBox.setBackground(Color.WHITE);

        qualityComboBox = new JComboBox<>(new String[] {
                "Standard", "Deluxe", "Suite"
        });

        floorComboBox = new JComboBox<>(new String[] {
        		"Floor 1", "Floor 2", "Floor 3"
        });

        bedTypeComboBox = new JComboBox<>(new String[] {
                "Single", "Double", "Queen", "King"
        });

        rateField = new JTextField(20);

        int row = 0;

        // Add labeled fields to the form
        addFormRow(formPanel, gbc, row++, "Room Number:", roomNumberField);
        addFormRow(formPanel, gbc, row++, "Number of Beds:", bedsSpinner);
        addFormRow(formPanel, gbc, row++, "Quality Level:", qualityComboBox);
        addFormRow(formPanel, gbc, row++, "Floor:", floorComboBox);
        addFormRow(formPanel, gbc, row++, "Bed Type:", bedTypeComboBox);
        addFormRow(formPanel, gbc, row++, "Rate per Night:", rateField);

        // Smoking checkbox (full row)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(smokingCheckBox, gbc);

        row++;

        // Buttons panel (Save / Clear / Back)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Save Room");
        JButton clearButton = new JButton("Clear");
        JButton backButton = new JButton("Back");

        // Temporary actions (UI only for now)
        saveButton.addActionListener(e -> saveRoom());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Go back to Clerk Menu")
        );

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        outerPanel.add(formPanel, BorderLayout.NORTH);
        return outerPanel;
    }

    /**
     * Helper method to add a label + input component in one row
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    /**
     * Handles the "Save Room" action
     * (Currently UI-only; later this will connect to backend logic)
     */
    private void saveRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String rate = rateField.getText().trim();

        // Basic validation
        if (roomNumber.isEmpty() || rate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in the required fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulate successful room creation
        JOptionPane.showMessageDialog(this,
                "New room added successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clears all input fields and resets the form
     */
    private void clearForm() {
        roomNumberField.setText("");
        rateField.setText("");
        bedsSpinner.setValue(1);
        smokingCheckBox.setSelected(false);
        qualityComboBox.setSelectedIndex(0);
        floorComboBox.setSelectedIndex(0);
        bedTypeComboBox.setSelectedIndex(0);
    }

    /**
     * Entry point to launch this page independently
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddRoomPage page = new AddRoomPage();
            page.setVisible(true);
        });
    }
}