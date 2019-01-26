package com.george.softwareenginnering;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.*;
import javax.imageio.*;
import java.io.*;
import java.io.Reader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRcodeverifier {
    public static String QR_CODE_IMAGE_PATH = "C:\\AUTOMATEDATTENDENCESYSTEM\\sanpshot0.png";

    static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }


    private static String readQRCodeImage(String filePath)
            throws WriterException, IOException {
        //QRCodeReader qrCodeReader = new QRCodeReader();
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath)))));

        String decodedText;
        // get QR reader
        final QRCodeReader reader = new QRCodeReader();
        // try to decode QR code
        try {
            // get Result from decoder
            final Result result = reader.decode(binaryBitmap);
            // get text from Result
            decodedText = result.getText();
        } catch (Exception e) {
            // set text to blank, no QR code found
            decodedText = "";
        }
        // return text
        return decodedText;
    }

    public static void main(String[] args) throws WriterException, IOException {

        check(QR_CODE_IMAGE_PATH);


    }
    static String check(String QR_CODE_IMAGE_PATH1) {
        String res = "false";
        try {
            res = readQRCodeImage(QR_CODE_IMAGE_PATH1);

        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }
        /*if (readQRCodeImage(QR_CODE_IMAGE_PATH).equals("My name is Asa"))
            System.out.println("You are marked present");
        else
            System.out.println("Retry");*/
        return(res);

    }
}
