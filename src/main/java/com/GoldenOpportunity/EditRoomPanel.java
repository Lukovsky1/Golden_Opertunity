package com.GoldenOpportunity;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This page allows a clerk to:
 * - Add a new room
 * - Validate inputs
 * - Save room into CSV file
 */
public class EditRoomPanel extends JPanel {

    // ===== UI COMPONENTS =====
    private JTextField roomNumberField;
    private JTextField rateField;
    private JSpinner bedsSpinner;
    private JCheckBox smokingCheckBox;
    private JComboBox<String> qualityComboBox;
    private JComboBox<String> floorComboBox;
    private JComboBox<String> roomTypeComboBox;

    // Panel that dynamically shows bed types
    private JPanel bedTypesPanel;
    private List<JComboBox<String>> bedTypeComboBoxes;

    private String titlePanel;

    // Path to CSV file
    private static final String ROOM_CSV_FILE = "src/main/resources/testRoomData1.csv";

    /**
     * Constructor → initializes UI
     */
    public EditRoomPanel(String title) {
        setLayout(new BorderLayout());

        this.titlePanel = title;

        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    /**
     * Header (title)
     */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel(titlePanel);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        header.add(title);
        return header;
    }

    /**
     * Main form UI
     */
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== INPUT FIELDS =====
        roomNumberField = new JTextField(20);
        rateField = new JTextField(20);

        bedsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        smokingCheckBox = new JCheckBox("Smoking Allowed");

        qualityComboBox = new JComboBox<>(new String[]{
                "Economic", "Business", "Comfort", "Executive"
        });

        floorComboBox = new JComboBox<>(new String[]{
                "1", "2", "3"
        });

        roomTypeComboBox = new JComboBox<>(new String[]{
                "Single", "Double", "Suite", "Standard", "Deluxe"
        });

        // ===== BED TYPES (dynamic) =====
        bedTypeComboBoxes = new ArrayList<>();
        bedTypesPanel = new JPanel();
        bedTypesPanel.setLayout(new BoxLayout(bedTypesPanel, BoxLayout.Y_AXIS));

        updateBedTypeFields(); // initial creation

        // When number of beds changes → update UI
        bedsSpinner.addChangeListener(e -> updateBedTypeFields());

        // Auto smoking for floor 2 & 3
        floorComboBox.addActionListener(e -> updateSmokingByFloor());
        updateSmokingByFloor();

        int row = 0;

        addRow(form, gbc, row++, "Room Number:", roomNumberField);
        addRow(form, gbc, row++, "Room Type:", roomTypeComboBox);
        addRow(form, gbc, row++, "Quality:", qualityComboBox);
        addRow(form, gbc, row++, "Floor:", floorComboBox);
        addRow(form, gbc, row++, "Number of Beds:", bedsSpinner);
        addRow(form, gbc, row++, "Type of Beds:", bedTypesPanel);
        addRow(form, gbc, row++, "Rate:", rateField);

        // Smoking checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(smokingCheckBox, gbc);

        row++;

        // ===== BUTTONS =====
        JPanel buttons = new JPanel();

        JButton save = new JButton("Save");
        JButton clear = new JButton("Clear");

        save.addActionListener(e -> saveRoom());
        clear.addActionListener(e -> clearForm());

        buttons.add(save);
        buttons.add(clear);

        gbc.gridy = row;
        form.add(buttons, gbc);

        return form;
    }

    /**
     * Helper → adds label + field
     */
    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

    /**
     * Creates one dropdown per bed
     */
    private void updateBedTypeFields() {
        bedTypesPanel.removeAll();
        bedTypeComboBoxes.clear();

        int beds = (int) bedsSpinner.getValue();

        for (int i = 1; i <= beds; i++) {
            JComboBox<String> box = new JComboBox<>(new String[]{
                    "Twin", "Full", "Queen", "King"
            });

            bedTypeComboBoxes.add(box);
            bedTypesPanel.add(new JLabel("Bed " + i));
            bedTypesPanel.add(box);
        }

        bedTypesPanel.revalidate();
        bedTypesPanel.repaint();
    }

    /**
     * Automatically set smoking based on floor
     */
    private void updateSmokingByFloor() {
        int floor = getFloor();

        if (floor == 2 || floor == 3) {
            smokingCheckBox.setSelected(true);
            smokingCheckBox.setEnabled(false);
        } else {
            smokingCheckBox.setEnabled(true);
            smokingCheckBox.setSelected(false);
        }
    }

    /**
     * Get selected floor (int)
     */
    private int getFloor() {
        return Integer.parseInt((String) floorComboBox.getSelectedItem());
    }

    /**
     * MAIN LOGIC → SAVE ROOM
     */
    private void saveRoom() {

        // ===== READ INPUT =====
        String roomText = roomNumberField.getText().trim();
        String rateText = rateField.getText().trim();

        // ===== BASIC CHECK =====
        if (roomText.isEmpty() || rateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields");
            return;
        }

        int roomNumber;
        double rate;

        try {
            roomNumber = Integer.parseInt(roomText);
            rate = Double.parseDouble(rateText);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
            return;
        }

        // ===== NEGATIVE CHECK =====
        if (roomNumber < 0 || rate < 0) {
            JOptionPane.showMessageDialog(this, "Values cannot be negative");
            return;
        }

        int floor = getFloor();

        // ===== FLOOR RULE =====
        if (String.valueOf(roomNumber).charAt(0) != String.valueOf(floor).charAt(0)) {
            JOptionPane.showMessageDialog(this,
                    "Room must start with floor number");
            return;
        }

        // ===== DUPLICATE CHECK =====
        if (roomExists(roomNumber)) {
            JOptionPane.showMessageDialog(this,
                    "Room already exists");
            return;
        }

        // ===== COLLECT DATA =====
        String roomType = (String) roomTypeComboBox.getSelectedItem();
        String quality = (String) qualityComboBox.getSelectedItem();
        int beds = (int) bedsSpinner.getValue();
        boolean smoking = smokingCheckBox.isSelected();

        List<String> bedsList = new ArrayList<>();
        for (JComboBox<String> box : bedTypeComboBoxes) {
            bedsList.add((String) box.getSelectedItem());
        }

        String bedsText = "\"" + String.join(", ", bedsList) + "\"";

        // ===== SAVE TO CSV =====
        boolean saved = writeToCSV(
                floor, roomNumber, roomType, quality,
                beds, bedsText, smoking, rate
        );

        if (saved) {
            JOptionPane.showMessageDialog(this, "Room added!");
            clearForm();
        }
    }

    /**
     * Check if room already exists in CSV
     */
    private boolean roomExists(int roomNumber) {
        try (BufferedReader br = new BufferedReader(new FileReader(ROOM_CSV_FILE))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {

                String[] parts = parseCSV(line);

                int existing = Integer.parseInt(parts[1].trim());

                if (existing == roomNumber) return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Write new room to CSV
     */
    private boolean writeToCSV(int floor, int room, String type, String quality,
                              int beds, String bedTypes, boolean smoking, double rate) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOM_CSV_FILE, true))) {

            bw.newLine();

            bw.write(floor + "," +
                    room + "," +
                    type + "," +
                    quality + "," +
                    beds + "," +
                    bedTypes + "," +
                    (smoking ? "TRUE" : "FALSE") + "," +
                    rate);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handles CSV parsing with quotes
     */
    private String[] parseCSV(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (char c : line.toCharArray()) {

            if (c == '"') insideQuotes = !insideQuotes;

            else if (c == ',' && !insideQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else current.append(c);
        }

        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }

    /**
     * Reset form
     */
    private void clearForm() {
        roomNumberField.setText("");
        rateField.setText("");
        bedsSpinner.setValue(1);
        updateBedTypeFields();
    }

    public void updateInfo(Vector<Object> data){
        //String[] columns = {"Floor Number","Room Number","Room Type","Quality","Number of Beds","Type of Beds","Smoking Status","Rate/per Night"};
        floorComboBox.setSelectedItem(data.elementAt(0));
        roomNumberField.setText(data.elementAt(1).toString());
        roomTypeComboBox.setSelectedItem(data.elementAt(2));
        qualityComboBox.setSelectedItem(data.elementAt(3));
        bedsSpinner.setValue(data.elementAt(4));
        smokingCheckBox.setSelected((Boolean) data.elementAt(6));
        rateField.setText(String.valueOf(data.elementAt(7)));
        updateBedTypeFields();
        List<String> beds = processBedTypes(data.elementAt(5));
        for(int i = 0; i < bedTypeComboBoxes.size(); i++){
            bedTypeComboBoxes.get(i).setSelectedItem(beds.get(i));
        }
    }

    private List<String> processBedTypes(Object data){
        String full = data.toString();
        String[] types = full.replaceAll(",","").split(" ");
        List<String> result = new ArrayList<>();

        for(int i = 0; i < types.length; i++){
            int num = Integer.parseInt(types[i]);

            for(int j = 0; j < num; j++){
                result.add(types[i+1]);
            }

            i++;
        }

        return result;
    }
}