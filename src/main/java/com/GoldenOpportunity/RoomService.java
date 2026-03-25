package com.GoldenOpportunity;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

public class RoomService {
    private List<Room> roomList = new ArrayList<>();
    //Holds room number and binds it to its room object
    static Map<Integer, Room> roomMap = new HashMap<>();

    RoomService(String filename) throws FileNotFoundException {
        File file = new File(filename);
        Scanner fileScanner = new Scanner(file);

        // Skip header
        if (fileScanner.hasNextLine()) {
            fileScanner.nextLine();

        }

        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] parts = parseCSVLine(line);

            int floorNum = Integer.parseInt(parts[0]);
            int roomNo = Integer.parseInt(parts[1]);
            String roomType = parts[2];
            String qLevel = parts[3];
            int beds = Integer.parseInt(parts[4]);
            boolean smoking = Boolean.parseBoolean(parts[6]);

            // No rate in CSV → default to 0.0
            double rate = 0.0;

            Room r = new Room(floorNum, roomNo, beds, smoking, qLevel, roomType, rate);
            roomList.add(r);
            roomMap.put(roomNo, r);
        }

        fileScanner.close();
    }

    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());

        return tokens.toArray(new String[0]);
    }

    List<Room> searchRoom(LocalDate start, LocalDate end, String roomType, double price){
        List<Room> validRooms = new ArrayList<>();
        for(Room r: roomList){
            if(roomType.equalsIgnoreCase(r.getRoomType()) && r.getRate() <= price){
                validRooms.add(r);
            }
        }
        return validRooms;

    }

    void createRoom(int floorNum, int rmNo, int b, boolean sm, String qlty, String rmType, double r){
        Room a = new Room(floorNum, rmNo, b, sm, qlty, rmType, r);
        roomList.add(a);
        //write to file
    }

    void modifyRoom(int rmNo, int b, boolean sm, String qlty, String rmType, double r) {
        for (Room room : roomList) {
            if (room.getRoomNo() == rmNo) {
                room.setBeds(b);
                room.setSmoking(sm);
                room.setQLevel(qlty);
                room.setRoomType(rmType);
                room.setRate(r);

                //Write to file
                return;
            }
        }
    }
    public void findRoom(int rmNo) {
        Room room = roomMap.get(rmNo);
    }

    List<Room> getRoomList() {
        return  roomList;
    }

}