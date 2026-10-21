package io;

import exception.FileStorageException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {
    public static boolean findID(String id, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.split("\\|")[0].equals(id)) {
                    return true;
                }
            }

            return false;
        } catch (IOException e) {
            throw new FileStorageException(Operation.READ.getOperation(), filePath);
        }
    }

    public static void write(String input, String filePath, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            writer.write(input);
            writer.newLine();
        } catch (IOException e) {
            throw new FileStorageException(Operation.WRITE.getOperation(), filePath);
        }
    }

    public static void writeAll(ArrayList<String> lines, String filePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))){
            for (String line : lines){
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e){
            throw new FileStorageException(Operation.WRITE.getOperation(), filePath);
        }
    }

    public static boolean update(String lineToUpdate, String newLine, String filePath) {
        final ArrayList<String> lines = retrieve(filePath);

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(lineToUpdate)) {
                lines.set(i, newLine);
                writeAll(lines, filePath);
                
                return true;
            }
        }
        
        return false;
    }

    public static ArrayList<String> retrieve(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            final ArrayList<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        } catch (IOException e) {
            throw new FileStorageException(Operation.RETRIEVE.getOperation(), filePath);
        }
    }

    public static String retrieveLine(String toFind, int index, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                
                if (line.split("\\|")[index].equals(toFind)) {
                    return line;
                }
            }

            return null;
        } catch (IOException e) {
            throw new FileStorageException(Operation.RETRIEVE.getOperation(), filePath);
        }
    }

    public static boolean delete(String lineToDelete, String filePath) {
        final ArrayList<String> lines = retrieve(filePath);

        if(lines.remove(lineToDelete)){
            writeAll(lines, filePath);
            return true;
        }

        return false;
    }
}