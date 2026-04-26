package com.GoldenOpportunity;

import com.GoldenOpportunity.Roles.RolePermissions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Vector;

public class ModifyRoomsPage extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final UIState uiState;

    private JTable roomsTable;
    private EditRoomPanel editRoomPanel;
    private DefaultTableModel model;

    public ModifyRoomsPage(CardLayout cardLayout, JPanel mainPanel,UIState uiState) throws IOException {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.uiState = uiState;

        setLayout(new BorderLayout());
        setBackground(new Color(243, 246, 249));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag && !RolePermissions.requireRole(this, uiState, "Managing rooms", "HOME", cardLayout, mainPanel, com.GoldenOpportunity.Login.enums.Role.CLERK)) {
            super.setVisible(false);
            return;
        }
        super.setVisible(aFlag);
    }

    // =========================
    // HEADER
    // =========================
    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        header.setBackground(Color.WHITE);

        Image logo = ImageIO.read(new File("src/main/java/com/GoldenOpportunity/Images/logo.png"));

        int originalWidth = logo.getWidth(null);
        int originalHeight = logo.getHeight(null);

        int newHeight = 70;
        int newWidth = (originalWidth * newHeight) / originalHeight;

        Image scaledLogo = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        nav.setBackground(Color.WHITE);
        String profileIcon = "👤";

        JButton homeButton = new JButton("Home");
        homeButton.setFocusPainted(false);
        homeButton.setBackground(Color.WHITE);
        homeButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        homeButton.setPreferredSize(new Dimension(90, 35));
        nav.add(homeButton);

        JButton profileButton = new JButton(profileIcon);
        profileButton.setFocusPainted(false);
        profileButton.setBackground(Color.WHITE);
        profileButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        profileButton.setPreferredSize(new Dimension(90, 35));
        nav.add(profileButton);

        profileButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"PROFILE");
        });
        homeButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"CLERK_HOME");
        });

        header.add(logoLabel, BorderLayout.WEST);
        header.add(nav, BorderLayout.EAST);
        return header;
    }

    // =========================
    // MAIN CONTENT
    // =========================
    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(new Color(243, 246, 249));
        content.setBorder(new EmptyBorder(12, 15, 18, 15));

        content.add(createSidebar(), BorderLayout.WEST);
        content.add(createCenterArea(), BorderLayout.CENTER);

        return content;
    }

    // =========================
    // SIDEBAR
    // =========================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new LineBorder(new Color(190, 205, 218), 1));
        sidebar.setPreferredSize(new Dimension(185, 0));

        JButton addRoomsButton = createSidebarButton("Add Rooms");
        JButton modifyRoomsButton = createSidebarButton("Modify Rooms");

        addRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"ADD_ROOMS");
        });
        modifyRoomsButton.addActionListener(e -> {
            cardLayout.show(mainPanel,"MODIFY_ROOMS");
        });

        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(addRoomsButton);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(modifyRoomsButton);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(135, 40));
        button.setMaximumSize(new Dimension(135, 40));
        button.setMinimumSize(new Dimension(135, 40));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(new LineBorder(new Color(150, 150, 150), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        return button;
    }

    // =========================
    // CENTER AREA
    // =========================
    private JPanel createCenterArea() {
        JPanel centerArea = new JPanel(new GridBagLayout());
        centerArea.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        // LEFT (table) → wider
        gbc.gridx = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 20); // ← RIGHT GAP
        centerArea.add(createRoomsPanel(), gbc);

        // RIGHT (form) → smaller
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0); // no gap needed here
        editRoomPanel = new EditRoomPanel("Modify Room");
        centerArea.add(editRoomPanel, gbc);

        return centerArea;
    }

    // =========================
    // LEFT PANEL - TABLE
    // =========================
    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(190, 205, 218), 1),
                new EmptyBorder(14, 16, 16, 16)
        ));

        JLabel title = new JLabel("Rooms", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(52, 66, 80));
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        String[] columns = {"Floor Number","Room Number","Room Type","Quality","Number of Beds","Type of Beds","Smoking Status","Rate/per Night"};

        model = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for(Room room : uiState.roomService.getRoomList()){
            String desc = "";
            for(Map.Entry<String,Integer> entry : room.getBedTypes().entrySet()){
                desc = desc.concat(entry.getValue() + " ");
                desc = desc.concat(entry.getKey() + ", ");
            }
            String finalDesc = desc.substring(0,desc.length()-2);
            model.addRow(new Object[]{room.getFloorNum(),room.getRoomNo(),room.getRoomType(),room.getQLevel(),room.getBeds(),finalDesc,room.isSmoking(),room.getRate()});
        }

        roomsTable = new JTable(model);
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomsTable.setRowHeight(38);
        roomsTable.setShowGrid(true);
        roomsTable.setGridColor(new Color(210, 218, 226));
        roomsTable.setSelectionBackground(new Color(220, 230, 240));
        roomsTable.setSelectionForeground(Color.BLACK);
        roomsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roomsTable.getTableHeader().setReorderingAllowed(false);
        roomsTable.getTableHeader().setResizingAllowed(false);
        roomsTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        roomsTable.getTableHeader().setBackground(Color.WHITE);
        roomsTable.getTableHeader().setBorder(new LineBorder(new Color(210, 218, 226), 1));

        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBorder(new LineBorder(new Color(210, 218, 226), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JButton selectButton = new JButton("Select");
        selectButton.setFocusPainted(false);
        selectButton.setBackground(Color.BLACK);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        selectButton.setPreferredSize(new Dimension(82, 38));
        selectButton.setBorder(BorderFactory.createEmptyBorder());
        selectButton.setOpaque(true);
        selectButton.setContentAreaFilled(true);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = roomsTable.getSelectedRow();
                if(row != -1){
                    int modelRow = roomsTable.convertRowIndexToModel(row);
                    Vector<Object> data = model.getDataVector().get(modelRow);
                    editRoomPanel.updateInfo(data);
                    editRoomPanel.revalidate();
                    editRoomPanel.repaint();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 14));
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // =========================
    // TEST MAIN
    // =========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Modify Rooms");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1220, 800);
            frame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            ModifyRoomsPage modifyRoomsPage = null;
            UIState uiState1 = new UIState();
            uiState1.reservationService = new ReservationService(Path.of("src/main/resources/testReservationData1.csv"));
            try {
                uiState1.roomService = new RoomService("src/main/resources/testRoomData1.csv");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                modifyRoomsPage = new ModifyRoomsPage(cardLayout, mainPanel,uiState1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mainPanel.add(modifyRoomsPage, "MODIFY_ROOMS");

            frame.setContentPane(mainPanel);
            cardLayout.show(mainPanel, "MODIFY_ROOMS");

            frame.setVisible(true);
        });
    }
}
