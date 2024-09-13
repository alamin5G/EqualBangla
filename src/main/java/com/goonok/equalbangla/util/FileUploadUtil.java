package com.goonok.equalbangla.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
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
                // Compress and save the PDF
                compressAndSavePdf(uploadPath, fileName, multipartFile);
            } else if (fileType.startsWith("image/")) {
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

    // This method checks for file name conflicts and resolves them
    private static String resolveFileNameConflict(Path uploadPath, String fileName) {
        String newFileName = fileName;
        Path filePath = uploadPath.resolve(newFileName);

        String baseName = getBaseName(fileName); // file name without extension
        String extension = getFileExtension(fileName); // file extension

        int counter = 1;
        while (Files.exists(filePath)) {
            newFileName = baseName + "_" + counter + extension;
            filePath = uploadPath.resolve(newFileName);
            counter++;
        }

        return newFileName;
    }

    // Helper method to extract the base name (without extension) of a file
    private static String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    // Helper method to extract the extension of a file
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(dotIndex);
    }

    // Compress and save an image to the given directory
    private static void compressAndSaveImage(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException {
        String contentType = multipartFile.getContentType();
        String formatName;

        // Determine format from content type
        if (contentType.equals("image/png")) {
            formatName = "png";
        } else if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) {
            formatName = "jpeg"; // Use 'jpeg' as ImageIO supports this format for compression
        } else {
            throw new IOException("Unsupported image format: " + contentType);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            String newFileName = resolveFileNameConflict(uploadPath, fileName);
            File outputFile = new File(uploadPath.resolve(newFileName).toString());

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
            if (!writers.hasNext()) {
                throw new IllegalStateException("No writers found for format: " + formatName);
            }

            ImageWriter writer = writers.next();
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed() && formatName.equals("jpeg")) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.75f); // Adjust compression quality for JPEG
                }

                writer.write(null, new IIOImage(image, null, null), param);
                log.info("Image compressed and saved successfully as: " + newFileName);
            } finally {
                writer.dispose();
            }
        }
    }

    // Compress and save a PDF to the given directory
    private static void compressAndSavePdf(Path uploadPath, String fileName, MultipartFile multipartFile) throws IOException {
        String newFileName = resolveFileNameConflict(uploadPath, fileName);
        Path compressedPdfPath = uploadPath.resolve(newFileName);

        // Use the Loader class to load the PDF in version 3.0.3 of PDFBox
        try (RandomAccessReadBuffer buffer = new RandomAccessReadBuffer(multipartFile.getInputStream());
             PDDocument document = Loader.loadPDF(buffer)) {
            // Add a dummy page to trigger compression
            document.addPage(new PDPage());

            // Save the compressed PDF
            document.save(compressedPdfPath.toFile());
            log.info("PDF compressed and saved successfully as: " + newFileName);
        } catch (IOException ioe) {
            log.error("PDF compression and save failed", ioe);
            throw new IOException("Could not save compressed PDF file", ioe);
        }
    }
}
