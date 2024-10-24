package com.example.coursework.FileInterpreter;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingHandler {
    private BufferedWriter logWriter;

    public LoggingHandler() {
        try {
            // Створюємо назву файлу в форматі рік-місяць-день-версія
            String fileName = createLogFileName();
            logWriter = new BufferedWriter(new FileWriter(fileName, true)); // Відкриваємо файл на дописування

            // Записуємо старт програми
            logMessage("Program started ❄️", "INFO");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createLogFileName() {
        // Отримуємо поточну дату
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "/Users/ihnore_ihor/IntelliJIDEAProjects/CourseWork/src/main/java/com/example/coursework/logs/" + today.format(formatter) + ".log"; // Назва файлу
    }

    public void logMessage(String message, String messageType) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = String.format("[%s] [%s] %s", timestamp, messageType, message);

            // Встановлюємо колір залежно від типу повідомлення
            String coloredLogEntry;
            switch (messageType) {
                case "ERROR":
                    coloredLogEntry = "\u001B[31m" + logEntry + "\u001B[0m"; // Червоний
                    break;
                case "WARNING":
                    coloredLogEntry = "\u001B[33m" + logEntry + "\u001B[0m"; // Жовтий
                    break;
                case "INFO":
                default:
                    coloredLogEntry = logEntry; // Стандартний колір
                    break;
            }

            // Записуємо у файл
            logWriter.write(logEntry);
            logWriter.newLine();
            logWriter.flush();

            // Виводимо кольорове повідомлення у консоль
            System.out.println(coloredLogEntry);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void logError(String errorMessage) {
        logMessage(errorMessage, "ERROR");
    }

    public void logWarning(String warningMessage) {
        logMessage(warningMessage, "WARNING");
    }

    public void logInfo(String infoMessage) {
        logMessage(infoMessage, "INFO");
    }

    public void close() {
        try {
            logMessage("Program finished", "INFO");
            if (logWriter != null) {
                logWriter.close(); // Закриваємо writer при завершенні програми
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


