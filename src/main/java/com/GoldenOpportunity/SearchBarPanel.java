package com.GoldenOpportunity;

import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.Period;
import java.util.*;
import java.util.List;

public class SearchBarPanel extends JPanel{
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private JComboBox<String> floorBox;
    private JComboBox<String> roomNumberBox;
    private JComboBox<String> roomTypeBox;
    private JComboBox<String> qualityBox;
    private JSpinner numBeds;
    private JComboBox<String> rateBox;
    private JCheckBox smokingBox;
    private List<JComboBox<String>> bedTypeList;
    private JButton searchBtn;

    private UIState uiState;

    public SearchBarPanel(UIState uiState){
        this.uiState = uiState;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ================= ROW 1: LABELS =================
        gbc.gridy = 0;

        gbc.gridx = 0;
        add(new JLabel("Check-In Date"), gbc);

        gbc.gridx = 1;
        add(new JLabel("Check-out Date"), gbc);

        gbc.gridx = 2;
        add(new JLabel("Floor Number"), gbc);

        gbc.gridx = 3;
        add(new JLabel("Room Number"), gbc);

        gbc.gridx = 4;
        add(new JLabel("Room Type"), gbc);

        gbc.gridx = 5;
        add(new JLabel("Quality"), gbc);

        // ================= ROW 2: INPUTS =================
        gbc.gridy = 1;

        gbc.gridx = 0;
        startDatePicker = new DatePicker();
        add(startDatePicker, gbc);

        gbc.gridx = 1;
        endDatePicker = new DatePicker();
        add(endDatePicker, gbc);

        gbc.gridx = 2;
        floorBox = new JComboBox<>(new String[]{"Any","1","2","3"});
        add(floorBox, gbc);

        floorBox.addActionListener(e -> updateRoomNumberBox());

        gbc.gridx = 3;
        roomNumberBox = new JComboBox<>(new String[]{"Any"});
        for(Room room : uiState.roomService.getAllRooms()){
            roomNumberBox.addItem(String.valueOf(room.getRoomNo()));
        }
        add(roomNumberBox, gbc);

        gbc.gridx = 4;
        roomTypeBox = new JComboBox<>(new String[]{
                "Any","Single","Double","Suite","Standard","Deluxe"
        });
        add(roomTypeBox, gbc);

        gbc.gridx = 5;
        qualityBox = new JComboBox<>(new String[]{
                "Any","Standard","Business","Comfort","Executive"
        });
        add(qualityBox, gbc);

        // ================= ROW 3: LABELS =================
        gbc.gridy = 2;

        gbc.gridx = 0;
        add(new JLabel("Number of Beds"), gbc);

        gbc.gridx = 1;
        add(new JLabel("Rate"), gbc);

        gbc.gridx = 2;
        add(new JLabel("Smoking"), gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        add(new JLabel("Bed Types"), gbc);
        gbc.gridwidth = 1;

        // ================= ROW 4: INPUTS =================
        gbc.gridy = 3;

        gbc.gridx = 0;
        numBeds = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        add(numBeds, gbc);

        gbc.gridx = 1;
        rateBox = new JComboBox<>(new String[]{
                "Any","Below $100","Below $150","Below $200","Below $250"
        });
        add(rateBox, gbc);

        gbc.gridx = 2;
        smokingBox = new JCheckBox("Smoking");
        smokingBox.setBackground(Color.WHITE);
        add(smokingBox, gbc);

        // ===== FIXED 4 BED TYPE COMBO BOXES =====
        JPanel bedTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bedTypePanel.setBackground(Color.WHITE);

        bedTypeList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            JComboBox<String> bedBox = new JComboBox<>(new String[]{
                    "Any","King", "Queen", "Twin", "Full"
            });
            bedBox.setPreferredSize(new Dimension(90, 28));
            bedTypeList.add(bedBox);
            bedTypePanel.add(bedTypeList.get(i));
        }

        gbc.gridx = 3;
        gbc.gridwidth = 3;
        add(bedTypePanel, gbc);
        gbc.gridwidth = 1;

        // ================= SEARCH BUTTON =================
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.gridheight = 3;

        searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(50, 100, 230));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(true);
        searchBtn.setPreferredSize(new Dimension(100, 35));

        add(searchBtn, gbc);

        gbc.gridheight = 1;
    }

    public Criteria search(){
        Criteria criteria = new Criteria();

        if (startDatePicker.getDate() == null || endDatePicker.getDate() == null ||
                Period.between(startDatePicker.getDate(),endDatePicker.getDate()).getDays() < 1) {
            JOptionPane.showMessageDialog(null, "Please select valid dates");
            return null;
        }
        else{
            criteria.setDateRange(new DateRange(startDatePicker.getDate(),endDatePicker.getDate()));
        }

        if(!Objects.equals(floorBox.getSelectedItem(), "Any")){
            criteria.setFloorNum(Integer.parseInt(Objects.requireNonNull(floorBox.getSelectedItem()).toString()));
        }

        if(!Objects.equals(roomNumberBox.getSelectedItem(), "Any")){
            criteria.setRoomNum(Integer.parseInt(Objects.requireNonNull(roomNumberBox.getSelectedItem()).toString()));
        }

        if(!Objects.equals(roomTypeBox.getSelectedItem(), "Any")){
            criteria.setRoomType(Objects.requireNonNull(roomTypeBox.getSelectedItem()).toString());
        }

        if(!Objects.equals(qualityBox.getSelectedItem(), "Any")){
            criteria.setQuality(Objects.requireNonNull(qualityBox.getSelectedItem()).toString());
        }

        if((int) numBeds.getValue() > 1){
            criteria.setNumBeds((int) numBeds.getValue());
        }

        if(!Objects.equals(rateBox.getSelectedItem(), "Any")){
            criteria.setRate(Double.parseDouble(Objects.requireNonNull(rateBox.getSelectedItem()).toString().replaceAll("\\D","")));
        }

        criteria.setSmoking(smokingBox.isSelected());

        Map<String,Integer> bedTypeMap = new HashMap<>();

        for(JComboBox<String> bedType : bedTypeList){
            if(!Objects.equals(bedType.getSelectedItem(), "Any")){
                bedTypeMap.putIfAbsent(Objects.requireNonNull(bedType.getSelectedItem()).toString(),0);
            }
        }

        criteria.setBeds(bedTypeMap);

        return criteria;
    }

    public void clearSearchPanel(){
        startDatePicker.setDate(null);
        endDatePicker.setDate(null);
        floorBox.setSelectedIndex(0);
        roomNumberBox.setSelectedIndex(0);
        roomTypeBox.setSelectedIndex(0);
        qualityBox.setSelectedIndex(0);
        numBeds.setValue(0);
        rateBox.setSelectedIndex(0);
        smokingBox.setSelected(false);

        for(JComboBox<String> bedType : bedTypeList){
            bedType.setSelectedIndex(0);
        }

        revalidate();
        repaint();
    }

    public void addSearchListener(ActionListener listener) {
        searchBtn.addActionListener(listener);
    }

    public DatePicker getStartDatePicker(){
        return startDatePicker;
    }

    public DatePicker getEndDatePicker(){
        return endDatePicker;
    }

    public void saveToUIState() {
        uiState.searchStartDate = startDatePicker.getDate();
        uiState.searchEndDate = endDatePicker.getDate();
        uiState.searchFloor = (String) floorBox.getSelectedItem();
        uiState.searchRoomNumber = (String) roomNumberBox.getSelectedItem();
        uiState.searchRoomType = (String) roomTypeBox.getSelectedItem();
        uiState.searchQuality = (String) qualityBox.getSelectedItem();
        uiState.searchNumBeds = (int) numBeds.getValue();
        uiState.searchRate = (String) rateBox.getSelectedItem();
        uiState.searchSmoking = smokingBox.isSelected();

        uiState.searchBedTypes.clear();
        for (JComboBox<String> bedType : bedTypeList) {
            uiState.searchBedTypes.add((String) bedType.getSelectedItem());
        }
    }

    public void loadFromUIState() {
        startDatePicker.setDate(uiState.searchStartDate);
        endDatePicker.setDate(uiState.searchEndDate);
        floorBox.setSelectedItem(uiState.searchFloor);
        roomNumberBox.setSelectedItem(uiState.searchRoomNumber);
        roomTypeBox.setSelectedItem(uiState.searchRoomType);
        qualityBox.setSelectedItem(uiState.searchQuality);
        numBeds.setValue(uiState.searchNumBeds);
        rateBox.setSelectedItem(uiState.searchRate);
        smokingBox.setSelected(uiState.searchSmoking);

        for (int i = 0; i < bedTypeList.size() && i < uiState.searchBedTypes.size(); i++) {
            bedTypeList.get(i).setSelectedItem(uiState.searchBedTypes.get(i));
        }
    }

    private void updateRoomNumberBox(){
        String floorNumber = Objects.requireNonNull(floorBox.getSelectedItem()).toString();

        if(!floorNumber.equals("Any")){
            roomNumberBox.removeAllItems();
            roomNumberBox.addItem("Any");

            for(Room room : uiState.roomService.getAllRooms()){
                if(room.getFloorNum() == Integer.parseInt(floorNumber)){
                    roomNumberBox.addItem(String.valueOf(room.getRoomNo()));
                }
            }
        }

        roomNumberBox.setSelectedIndex(0);
        roomNumberBox.revalidate();
        roomNumberBox.repaint();
    }
}
