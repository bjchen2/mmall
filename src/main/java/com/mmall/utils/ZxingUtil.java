package com.mmall.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * 用于生成二维码
 * Created By Cx On 2018/9/12 21:32
 */
@Slf4j
public class ZxingUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    /**
     * 将内容 contents 生成长宽均为 width 的图片，并保存到 imgPath 下
     * 返回生成的 File 对象
     */
    public static File getQRCodeImge(String contents, int width, String imgPath) {
        return getQRCodeImge(contents, width, width, imgPath);
    }

    /**
     * 将内容 contents 生成长宽均为 width 的图片，并保存到 imgPath 下
     * 返回生成的 File 对象
     */
    public static File getQRCodeImge(String contents, int width, int height, String imgPath) {
        try {
            Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);

            File imageFile = new File(imgPath);
            writeToFile(bitMatrix, "png", imageFile);

            return imageFile;

        } catch (Exception e) {
            log.error("create QR code error!", e);
            return null;
        }
    }
}
