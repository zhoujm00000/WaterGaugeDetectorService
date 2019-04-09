package com.xlauncher.util;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.springframework.util.Base64Utils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/11/2 0002
 * @Desc :实现缩略图
 **/
public class ImageUtil {
    private static int width;
    private static int height;
    private static BufferedImage img;
    private static int w = 150;
    private static int h = 150;
    private static BASE64Decoder decoder = new BASE64Decoder();
    private static BASE64Encoder encoder = new BASE64Encoder();
    private static Logger logger = Logger.getLogger(ImageUtil.class);

    /**
     * 缩略图
     *
     * @param bytes
     * @return
     */
    public static byte[] thumbnail(byte[] bytes){
        logger.info("___实现缩略图bytes." + bytes.length);
        if (bytes.length !=0) {
            InputStream is = new ByteArrayInputStream(bytes);
            BufferedImage bf;
            try {
                bf = ImageIO.read(is);
                Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(bf).size(w, h);
                BufferedImage bufferedImage = builder.asBufferedImage();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", bos);
                byte[] byteArray = bos.toByteArray();
                logger.info("___缩略图length." + Base64Utils.encodeToString(byteArray).length());
                return byteArray;
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("___Err." + e);
            }
        }
        return null;
    }

    /**
     * 获取source
     *
     * @param bytes bytes
     * @return byte
     */
    public static byte[] image(byte[] bytes) {
        logger.info(" ___实现缩略图!" + bytes.length);
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(" ___Err!" + e);
        }
        return resize();
    }

    /**
     * 缩略图
     *
     * @return byte
     */
    private static byte[] resize() {
        try {
            BufferedImage image = new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
            image.getGraphics().drawImage(img,0,0,w,h,null);
            image.getGraphics().dispose();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageIO.write(image,"jpg",bs);
            return bs.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(" ___Err!" + e);
        }
        return null;
    }

    /**
     * 以宽度为基准，等比例放缩图片
     *
     * @param w w
     * @return byte
     */
    private byte[] resizeByWidth(int w) {
        h = (height*w/width);
        return resize();
    }

    /**
     * 以高度为基准，等比例缩放图片
     *
     * @param h h
     * @return byte
     */
    private byte[] resizeByHeight(int h) {
        w = (width*h/height);
        return resize();
    }


    static String getImageBinary(String path) {
        File file = new File(path);
        try {
            BufferedImage bi ;
            bi = ImageIO.read(file);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi,"jpg",os);
            byte[] bytes = os.toByteArray();
            return encoder.encodeBuffer(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void toImage(String base64, String path) {
        try {
            byte[] bytes = decoder.decodeBuffer(base64);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            BufferedImage bi = ImageIO.read(is);
            File file = new File(path);
            ImageIO.write(bi,"jpg",file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图片对象获取对应InputStream
     *
     * @param image
     * @param readImageFormat
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(BufferedImage image, String readImageFormat)
            throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, readImageFormat, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        os.close();
        return is;
    }
}
