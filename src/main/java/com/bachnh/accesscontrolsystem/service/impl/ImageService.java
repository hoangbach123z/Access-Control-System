package com.bachnh.accesscontrolsystem.service.impl;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageService {

    // Save image in a local directory
    public String saveImageToStorage(String uploadDirectory, String imageName, BufferedImage image) throws IOException {
        // Tạo đường dẫn tới thư mục upload
        Path uploadPath = Path.of(uploadDirectory);
        Path filePath = uploadPath.resolve(imageName);

        // Kiểm tra và tạo thư mục nếu chưa tồn tại
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Lấy phần mở rộng của file để xác định định dạng ảnh
        String fileExtension = getFileExtension(imageName);
        if (fileExtension == null || fileExtension.isEmpty()) {
            throw new IOException("File extension is not provided or invalid.");
        }
        // Ghi ảnh vào file với định dạng tương ứng
         ImageIO.write(image, "png", filePath.toFile());
        return filePath.toString();
    }

    // Hàm hỗ trợ lấy phần mở rộng của file
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }

    // To view an image
    public byte[] getImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return imageBytes;
        } else {
            return null; // Handle missing images
        }
    }

    // Delete an image
    public String deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed"; // Handle missing images
        }
    }
}