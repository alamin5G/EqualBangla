package com.goonok.equalbangla.service;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;

@Service
public class DatabaseBackupAndRestoreService {

    @Autowired
    private DataSource dataSource;

    @Value("${db.username}")
    String dbUser;
    @Value("${db.password}")
    String dbPassword;
    @Value("${db.name}")
    String dbName;
    @Value("${db.host}")
    String dbHost;

    @Value("${mysql.path}")
    String mysqlPath;  // Full path to mysql.exe

    private static final String SCHEMA_NAME = "equal_bangladesh"; // Set the schema name

    public void backupDatabaseToResponseUsingJdbc(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=equal_bangladesh_backup.sql");

        try (PrintWriter writer = response.getWriter();
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Get database metadata and list all tables for the specific schema
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, SCHEMA_NAME, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                // Check if the table exists in the given schema and backup the data for that table
                if (doesTableExist(connection, SCHEMA_NAME, tableName)) {
                    writer.println("-- Dumping data for table: " + tableName);
                    ResultSet rows = statement.executeQuery("SELECT * FROM " + SCHEMA_NAME + "." + tableName);

                    // Write INSERT statements for each row
                    while (rows.next()) {
                        StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " VALUES(");
                        for (int i = 1; i <= rows.getMetaData().getColumnCount(); i++) {
                            insertQuery.append("'").append(rows.getString(i)).append("'");
                            if (i < rows.getMetaData().getColumnCount()) {
                                insertQuery.append(",");
                            }
                        }
                        insertQuery.append(");");
                        writer.println(insertQuery.toString());
                    }
                    writer.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to backup the database", e);
        }
    }

    // Method to check if a table exists in the specific schema
    private boolean doesTableExist(Connection connection, String schemaName, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM information_schema.tables WHERE table_schema = ? AND table_name = ?")) {
            preparedStatement.setString(1, schemaName);  // Specify the schema
            preparedStatement.setString(2, tableName);  // Specify the table
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();  // Return true if the table exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    //backup using mysql dump
    public void backupDatabase(HttpServletResponse response) throws IOException {
        // Customize with your database credentials
        //String dbName = "equal_bangladesh";
        //tring dbUser = "root";
        //String dbPassword = "252646"; //for local
        //TODO - CHANGE THE PASSWORD WHILE YOU PROD THE PROJECT

        // Set response headers for file download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=database_backup" + LocalDateTime.now().toString() + ".sql");

        // Build the mysqldump command
        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "--databases", dbName,
                "--user=" + dbUser,
                "--password=" + dbPassword
        );

        pb.redirectErrorStream(true);  // Combine stderr and stdout
        Process process = pb.start();

        // Stream the process output directly to HTTP response
        try (InputStream in = process.getInputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, length);
            }
        }

        // Ensure the HTTP response is fully flushed
        response.flushBuffer();

        // Wait for the process to complete
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Backup failed with exit code: " + exitCode);
            } else {
                System.out.println("Backup streamed successfully.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Backup process was interrupted", e);
        }
    }


    //one-click backup the database
    // One-click backup the database with improved error handling
    public void backupDatabaseOnClick(HttpServletResponse response) throws IOException {
        // Define backup file name and path (saved temporarily)
        String fileName = "db_backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
        //String command = "mysqldump -u " + dbUser + " -p" + dbPassword + " " + dbName; //worked perfect for local host

        String command = "mysqldump -h " + dbHost + " -u " + dbUser + " -p" + dbPassword + " " + dbName;


        try {
            // Execute the mysqldump command
            Process process = Runtime.getRuntime().exec(command);

            // Wait for the process to complete
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream(); // Capture error stream if needed

            // Set response headers for file download
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Write the backup SQL file to the output stream for download
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Writing the mysqldump output to the response output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();

            // Check for errors during the execution of the command
            StringBuilder errorOutput = new StringBuilder();
            while ((bytesRead = errorStream.read(buffer)) != -1) {
                errorOutput.append(new String(buffer, 0, bytesRead));
            }

            if (!errorOutput.toString().isEmpty()) {
                // Log or handle the error as needed
                System.err.println("Errors during backup: " + errorOutput.toString());
            }
        } catch (IOException e) {
            // Log or handle any exceptions that occur
            System.err.println("Error during database backup: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during database backup.");
        }
    }


    //one-click backup the database -- working fine
   /* public void backupDatabaseOnClick(HttpServletResponse response) throws IOException {
        // Define backup file name and path (saved temporarily)
        String fileName = "db_backup"+LocalDateTime.now().toString() + ".sql";
        String command = "mysqldump -u "+ dbUser + " -p" + dbPassword + " " + dbName;

        // Create the backup file by executing the mysqldump command
        Process process = Runtime.getRuntime().exec(command);

        // Wait for the process to complete
        InputStream inputStream = process.getInputStream();

        // Set response header for file download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Write the backup SQL file to the output stream for download
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }*/

    /*Restore Service method*/

    public void restoreDatabase(MultipartFile backupFile) throws IOException, InterruptedException {
        // Save the uploaded file to a temporary location
        File tempFile = File.createTempFile("db_backup", ".sql");
        backupFile.transferTo(tempFile);

        // Ensure the MySQL path is correct for Windows and use ProcessBuilder


        // Build the command (adjust if needed)
        String[] command = {mysqlPath, "-u", dbUser, "-p" + dbPassword, dbName};

        // Use ProcessBuilder for executing commands
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);  // Redirect stderr to stdout for easier debugging
        Process process = processBuilder.start();

        // Provide the SQL file content to the process's standard input
        try (OutputStream os = process.getOutputStream();
             FileInputStream fis = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }

        // Wait for the process to finish and check the exit code
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Database restore completed successfully.");
        } else {
            throw new RuntimeException("Error during database restore. Exit code: " + exitCode);
        }
    }

}