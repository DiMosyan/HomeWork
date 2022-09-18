package com.MyChat.clientChat;

import com.MyChat.clientChat.dialogs.Dialogs;
import javafx.scene.control.Alert;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileEditor {

    private final String dirPath = "Lesson7/history";
    private String name;

    public FileEditor(String name) {
        this.name = name;
    }

    public List<String> getHistoryFromFile(String nameOfEntrant) {
        List<String> result = new ArrayList<>();
        Path filePath = Paths.get(String.format("%s/%s/%s_to_%s.txt", dirPath, this.name, this.name, nameOfEntrant));
        int numOfFirstLine = getNumOfFirstLine(filePath);
        if(!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e){
                fileErrorInitDialog(e, "File creating error!");
            }
            return result;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            int currentLine = 0;
            String line;
            while (currentLine != numOfFirstLine) {
                br.readLine();
                currentLine++;
            }
            line = br.readLine();
            while(line != null) {
                result.add(line);
                line = br.readLine();
            }
        }catch (IOException e) {
            fileErrorInitDialog(e, "File reading error!");
        }
        return result;
    }

    public void addLineToFile(String nameOfEntrant, String line) {
        Path filePath = Paths.get(String.format("%s/%s/%s_to_%s.txt", this.dirPath, this.name, this.name, nameOfEntrant));
        try {
            Files.writeString(filePath, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            fileErrorInitDialog(e, "File writing error!");
        }
    }

    public void renameFile(String oldNameOfEntries, String newNameOfEntries) {
        Path oldFilePath = Paths.get(String.format("%s/%s/%s_to_%s.txt", this.dirPath, this.name, this.name, oldNameOfEntries));
        Path newFilePath = Paths.get(String.format("%s/%s/%s_to_%s.txt", this.dirPath, this.name, this.name, newNameOfEntries));
        try {
            Files.move(oldFilePath, newFilePath);
        } catch (IOException e) {
            fileErrorInitDialog(e, "File renaming error!");
        }
    }

    public void renameAllFiles(String newName) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(renameUserDir(this.name, newName))) {
            for (Path file : stream) {
                String fileName = file.toString();
                fileName = fileName.replace(this.name, newName);
                Files.move(file, Paths.get(fileName));
            }
            this.name = newName;
        } catch (IOException e) {
            fileErrorInitDialog(e, "Files renaming error!");
        }
    }

    public void createUserDir() {
        Path userDirPath = Paths.get(String.format("%s/%s", this.dirPath, this.name));
        try {
            if(!Files.exists(userDirPath)) Files.createDirectory(userDirPath);
        } catch (IOException e) {
            fileErrorInitDialog(e, "Directory  creating error!");
        }
    }

    private Path renameUserDir(String oldName, String newName) throws IOException {
        Path oldUserDirPath = Paths.get(String.format("%s/%s", this.dirPath, oldName));
        Path newUserDirPath = Paths.get(String.format("%s/%s", this.dirPath, newName));
        Files.move(oldUserDirPath, newUserDirPath);
        return newUserDirPath;
    }

    private int getNumOfFirstLine(Path filePath) {
        int i = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            while(br.readLine() != null) i++;
            if(i > 100) return i - 100;
        } catch (IOException e) {
            System.err.println("File reading error!");
            e.printStackTrace();
            Dialogs.ErrorType.FILE_ERROR.show(Alert.AlertType.ERROR, "File reading error!");
        }
        return 0;
    }

    private void fileErrorInitDialog(IOException e, String message) {
        System.err.println(message);
        e.printStackTrace();
        Dialogs.ErrorType.FILE_ERROR.show(Alert.AlertType.ERROR, message);
    }
}
