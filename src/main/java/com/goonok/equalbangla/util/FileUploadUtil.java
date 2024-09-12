package com.goonok.equalbangla.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Iterator;

@Slf4j
public class FileUploadUtil {

    private static final long MAX_FILE_SIZE_PDF = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_FILE_SIZE_IMAGE = 1024 * 1024; // 1 MB

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Folder created successfully");
        }

        String fileType = multipartFile.getContentType();
        long fileSize = multipartFile.getSize();

        if (fileType != null) {
            if (fileType.equals("application/pdf")) {
                if (fileSize > MAX_FILE_SIZE_PDF) {
                    throw new IOException("PDF file size exceeds the 5 MB limit.");
                }
                // Proceed with saving the PDF
                saveFileToDirectory(uploadPath, fileName, multipartFile);
            } else if (fileType.equals("image/jpeg") || fileType.equals("image/png") || fileType.equals("image/jpg")) {
                if (fileSize > MAX_FILE_SIZE_IMAGE) {
                    throw new IOException("Image file size exceeds the 1 MB limit.");
                }
                // Compress and save the image if it exceeds 1MB
                compressAndSaveImage(uploadPath, fileName, multipartFile);
            } else {
                throw new IOException("Unsupported file type: " + fileType);
            }
        } else {
            throw new IOException("Unknown file type.");
        }
    }

    private static void saveFileToDirectory(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully");
        } catch (IOException ioe) {
            log.error("File upload failed");
            throw new IOException("Could not save file: " + fileName, ioe);
        }
    }

    private static void compressAndSaveImage(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            File outputFile = new File(uploadPath.resolve(fileName).toString());

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("No writers found for JPEG format");
            }

            ImageWriter writer = writers.next();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.75f); // Change compression quality if needed
                }

                writer.write(null, new IIOImage(image, null, null), param);
                log.info("Image compressed and saved successfully");
            } finally {
                writer.dispose();
            }
        }
    }
}