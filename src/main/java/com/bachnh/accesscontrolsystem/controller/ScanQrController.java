package com.bachnh.accesscontrolsystem.controller;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
@Component
@Lazy
public class ScanQrController implements Initializable {
    @FXML
    private AnchorPane scanQrContainer;
    private Webcam webcam = null;
    @FXML
    private ImageView cameraView;
    @FXML
    private ImageView profileImage;
    @FXML
    private Label resultLabel;
    private volatile boolean running = true;
    private volatile String lastQRResult = "";
    private static final int SCAN_INTERVAL = 100;
    private Rectangle focusRect;
    private long lastScanTime = 0;
    private static final long COOLDOWN_PERIOD = 3000;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeWebcam();
        initializeUI();
        startWebcamStream();
        Platform.runLater(() -> {
            Stage stage = (Stage) scanQrContainer.getScene().getWindow();
            stage.setOnCloseRequest(event -> stopCamera());
        });
    }
    private void initializeWebcam() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            Dimension[] resolutions = webcam.getViewSizes();
            webcam.setViewSize(resolutions[resolutions.length - 1]);
            webcam.open();
        } else {
            System.out.println("Không tìm thấy webcam!");
            Platform.exit();
        }
    }

    private void initializeUI() {
//        cameraView = new ImageView();
        cameraView.setFitWidth(640);
        cameraView.setFitHeight(480);
        cameraView.setPreserveRatio(true);
        cameraView.setScaleX(cameraView.getScaleX() * -1);

        focusRect = new Rectangle();
        focusRect.setStroke(Color.rgb(70, 135, 197));
        focusRect.setStrokeWidth(4);
//        focusRect.getStrokeDashArray().setAll(10.0, 10.0);
        focusRect.setFill(Color.TRANSPARENT);
        focusRect.setArcHeight(10);
        focusRect.setArcWidth(10);
        focusRect.setVisible(false);
//        resultLabel = new Label("Quét mã QR...");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");
        scanQrContainer.getChildren().add(focusRect);
    }


    private void startWebcamStream() {
        Thread webcamThread = new Thread(this::processWebcamStream);
        webcamThread.setDaemon(true);
        webcamThread.start();
    }

    private void processWebcamStream() {
        MultiFormatReader reader = setupQRReader();
        long qrNotFoundStartTime = -1;

        while (running) {
            try {
                if (webcam != null && webcam.isOpen()) {
                    BufferedImage bufferedImage = webcam.getImage();
                    if (bufferedImage != null) {
                        updateImageView(bufferedImage);
                        processQRCode(reader, bufferedImage, qrNotFoundStartTime);
                    }
                }
                Thread.sleep(SCAN_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MultiFormatReader setupQRReader() {
        MultiFormatReader reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
        reader.setHints(hints);
        return reader;
    }

    private void updateImageView(BufferedImage bufferedImage) {
        WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
        Platform.runLater(() -> cameraView.setImage(fxImage));
    }

    private void processQRCode(MultiFormatReader reader, BufferedImage bufferedImage, long qrNotFoundStartTime) {
        try {
            Result result = decodeQRCode(reader, bufferedImage);
            if (result != null && !result.getText().equals(lastQRResult)) {
                updateQRResult(result, bufferedImage);
            }
        } catch (NotFoundException e) {
            handleQRNotFound(qrNotFoundStartTime);
        }
    }

    private Result decodeQRCode(MultiFormatReader reader, BufferedImage bufferedImage) throws NotFoundException {
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        return reader.decode(bitmap);
    }

    private void updateQRResult(Result result, BufferedImage bufferedImage) {
        long currentTime = System.currentTimeMillis();
        // Kiểm tra xem đã đủ thời gian chờ chưa
        if (currentTime - lastScanTime < COOLDOWN_PERIOD) {
            return; // Bỏ qua nếu chưa đủ thời gian chờ
        }

        ResultPoint[] points = result.getResultPoints();
        if (points != null && points.length >= 3) {
            updateFocusRect(points, bufferedImage);
            lastQRResult = result.getText();
            lastScanTime = currentTime; // Cập nhật thời gian quét thành công
            handleNewQRCode(result.getText());
        }
    }


    private void updateFocusRect(ResultPoint[] points, BufferedImage bufferedImage) {
        double[] dimensions = calculateQRDimensions(points);
        // Giảm padding để khung không quá rộng
        double padding = Math.min(dimensions[2], dimensions[3]) * 0.4;

        Platform.runLater(() -> {
            focusRect.setX(dimensions[0] - padding);
            focusRect.setY(dimensions[1] - padding);
            // Điều chỉnh hệ số scale để khung vừa vặn hơn
            focusRect.setWidth(dimensions[2] + padding * 2);
            focusRect.setHeight(dimensions[3] + padding * 2);
            focusRect.setVisible(true);
            resultLabel.setText("Kết quả: " + lastQRResult);
        });
    }

    private double[] calculateQRDimensions(ResultPoint[] points) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (ResultPoint point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }

        // Tính toán tỷ lệ scale chính xác hơn
        double scaleX = cameraView.getFitWidth() / webcam.getViewSize().getWidth();
        double scaleY = cameraView.getFitHeight() / webcam.getViewSize().getHeight();

        // Áp dụng tỷ lệ scale
        double rectX = minX * scaleX;
        double rectY = minY * scaleY;
        double rectWidth = (maxX - minX) * scaleX;
        double rectHeight = (maxY - minY) * scaleY;

        // Xử lý trường hợp ảnh bị lật
        if (cameraView.getScaleX() < 0) {
            rectX = cameraView.getFitWidth() - rectX - rectWidth;
        }
        if (cameraView.getScaleY() < 0) {
            rectY = cameraView.getFitHeight() - rectY - rectHeight;
        }

        return new double[]{rectX, rectY, rectWidth, rectHeight};
    }

    private void handleQRNotFound(long qrNotFoundStartTime) {
        if (System.currentTimeMillis() - qrNotFoundStartTime > 3000) {
            Platform.runLater(() -> {
                focusRect.setVisible(false);
                resultLabel.setText("Kết quả: ");
            });
            lastQRResult = "";
        }
    }

    private void handleNewQRCode(String qrContent) {
        Platform.runLater(() -> {
            resultLabel.setText("Kết quả: " + qrContent);
            // Có thể thêm thông báo về thời gian chờ
            new Timeline(new KeyFrame(Duration.millis(COOLDOWN_PERIOD), e -> {
                resultLabel.setText("Sẵn sàng quét mã QR tiếp theo...");
            })).play();
        });
        System.out.println(qrContent);

    }

    public void stopCamera() {
        running = false;
        if (webcam != null) {
            webcam.close();
        }
    }
}
