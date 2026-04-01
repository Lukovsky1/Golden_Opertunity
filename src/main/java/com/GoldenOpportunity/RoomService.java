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
            int numBeds = Integer.parseInt(parts[4]);

            // Parse bed types: e.g. "1 Full, 2 Twin"
            Map<String, Integer> bedTypeMap = parseBedTypes(parts[5]);

            boolean smoking = Boolean.parseBoolean(parts[6]);
            double rate = Double.parseDouble(parts[7]);

            Room r = new Room(
                    floorNum,
                    roomNo,
                    numBeds,
                    smoking,
                    qLevel,
                    roomType,
                    rate,
                    bedTypeMap
            );

            roomList.add(r);
            roomMap.put(roomNo, r);
        }

        fileScanner.close();
    }

    private Map<String, Integer> parseBedTypes(String input) {
        Map<String, Integer> map = new HashMap<>();

        // Remove quotes if present
        input = input.replace("\"", "").trim();

        // Split by comma
        String[] parts = input.split(",");

        for (String part : parts) {
            part = part.trim(); // "1 Full"

            String[] tokens = part.split(" ");
            int count = Integer.parseInt(tokens[0]);
            String type = tokens[1];

            map.put(type, count);
        }

        return map;
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

    //TODO: See about refactoring
    List<Room> searchRoom(Criteria criteria){
        List<Room> validRoomList = new ArrayList<>();
        for(Room room: roomList){
            if(criteria.getFloorNum() != 0 && criteria.getFloorNum() != room.getFloorNum() ){
                continue;
            }
            if(criteria.getRoomNum() != 0 && criteria.getRoomNum() != room.getRoomNo()){
                continue;
            }
            if(criteria.getRoomType() != null &&
                    !criteria.getRoomType().isBlank() &&
                    !criteria.getRoomType().equalsIgnoreCase(room.getRoomType())){
                continue;
            }
            if(criteria.getQuality() != null &&
                    !criteria.getQuality().isBlank() &&
                    !criteria.getQuality().equalsIgnoreCase(room.getQLevel())){
                continue;
            }
            if (criteria.isSmoking() && !room.isSmoking()) {
                continue;
            }
            if(criteria.getNumBeds() > 0 && criteria.getNumBeds() != room.getBeds()){
                continue;
            }
            if (!criteria.getBeds().isEmpty()) {
                boolean bedMatch = true;

                for (Map.Entry<String, Integer> entry : criteria.getBeds().entrySet()) {
                    String bedType = entry.getKey();
                    int required = entry.getValue();

                    if (required == 0) continue;

                    int available = room.getBedTypes().getOrDefault(bedType, 0);
                    if (available < required) {
                        bedMatch = false;
                        break;
                    }
                }

                if (!bedMatch) continue;
            }
            if(criteria.getRate() > 0 && criteria.getRate() < room.getRate()){
                continue;
            }
            validRoomList.add(room);
        }
        return validRoomList;
    }

    void createRoom(int floorNum, int rmNo, int numBeds, boolean smoke, String qlty,
                    String rmType, double rate, Map<String, Integer> bedTypesInput){
        Room a = new Room(floorNum, rmNo, numBeds, smoke, qlty, rmType, rate, bedTypesInput);
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