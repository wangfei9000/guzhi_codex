package com.admin.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;

    @GetMapping("/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(
            @RequestParam String path,
            @RequestParam(defaultValue = "300") int maxWidth,
            @RequestParam(defaultValue = "300") int maxHeight) throws IOException {

        Path filePath = Paths.get(uploadDir, path).normalize();
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        // Read original image
        BufferedImage original = ImageIO.read(filePath.toFile());
        if (original == null) {
            return ResponseEntity.notFound().build();
        }

        // Skip resize if image is already small enough
        if (original.getWidth() <= maxWidth && original.getHeight() <= maxHeight) {
            return toResponse(original);
        }

        // Calculate new dimensions preserving aspect ratio
        double ratio = Math.min(
                (double) maxWidth / original.getWidth(),
                (double) maxHeight / original.getHeight()
        );
        int newWidth = (int) (original.getWidth() * ratio);
        int newHeight = (int) (original.getHeight() * ratio);

        // Resize
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return toResponse(resized);
    }

    private ResponseEntity<byte[]> toResponse(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.6f);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(baos.toByteArray());
    }
}
