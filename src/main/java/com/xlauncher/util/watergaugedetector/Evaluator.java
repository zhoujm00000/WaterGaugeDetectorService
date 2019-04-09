package com.xlauncher.util.watergaugedetector;
import org.apache.log4j.Logger;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.xlauncher.util.watergaugedetector.Classifier.object_image;

/**
 * 整合水尺对象检测模型和水尺刻度预测模型，输入图片，直接输出水位
 * @author liangjia
 * @date 2018-10-09
 */
public class Evaluator {

    private volatile static Evaluator evaluator;
    private volatile static boolean modelLoaded;
    private static Logger logger = Logger.getLogger(Evaluator.class);
    private static Detector detector = null;
    private static Classifier classifier = null;
    private static MultiDetector multiDetector = null;
    private static Predictor predictor = null;

    private Evaluator(String detectorModelPath, String classifierModelPath,String multiDetectorModelPath, String predictorModelPath){
        detector = new Detector(detectorModelPath);
        classifier = new Classifier(classifierModelPath);
        multiDetector = new MultiDetector(multiDetectorModelPath);
        predictor = new Predictor(predictorModelPath);
        modelLoaded = false;
    }

    /**
     * 单例模式加锁操作，并保证模型只加载一次，否则返回null
     * @param detectorModelPath String
     * @param predictorModelPath String
     * @return evaluator
     */
    public static Evaluator getInstance (String detectorModelPath, String classifierModelPath,String multiDetectorModelPath, String predictorModelPath){
        if (null == evaluator) synchronized (Evaluator.class) {
            if (null == evaluator) {
                File detectorModelFile = new File(detectorModelPath);
                File classifierModelFile = new File(classifierModelPath);
                File multidetectorModelFile = new File(multiDetectorModelPath);
                File predictorModelFile = new File(predictorModelPath);

                if (!multidetectorModelFile.exists()){
                    logger.error("模型文件不存在！");
                    return null;
                }

                File[] multidetectorModelFiles = multidetectorModelFile.listFiles();
                assert multidetectorModelFiles != null;
                int a = 0;
                if (multidetectorModelFiles.length == a || !detectorModelFile.exists() ||
                        !classifierModelFile.exists() || !predictorModelFile.exists()){
                    logger.error("模型文件不存在！");
                    return null;
                }

                evaluator = new Evaluator(detectorModelPath, classifierModelPath, multiDetectorModelPath, predictorModelPath);
                //保证模型只加载一次
                if (!evaluator.modelLoaded) {
                    boolean loadSucc = evaluator.loadModel();
                    if (!loadSucc) {
                        logger.error("加载模型阶段出现异常情况！");
                        evaluator = null;
                    }
                }
            }
        }
        return evaluator;
    }

    /**
     * 加载模型，并判断是否加载成功
     * @return boolean
     */
    private static boolean loadModel(){
        if (null == detector || null == predictor || classifier == null || multiDetector == null) {
            logger.error("加载模型阶段一出现异常情况！");
            return false;
        }

        if (detector.loadModel())
        {
            if (classifier.loadModel()) {
                if (multiDetector.loadModel()){
                    if (predictor.loadModel()){
                        modelLoaded = true;
                        return true;
                    }
                    multiDetector.closeModel();
                }
                classifier.closeModel();
            }
            detector.closeModel();
        }
        logger.error("加载模型阶段二出现异常情况！");
        return false;
    }
    //模型加载完毕关闭，释放内存
    public static void closeModel() {
        if (null != detector){
            detector.closeModel();
        }
        if (null != classifier){
            classifier.closeModel();
        }
        if(null != multiDetector) {
            multiDetector.closeModel();
        }
        if(null != predictor) {
            predictor.closeModel();
        }
    }

    /**
     * 根据水尺刻度预测结果和水尺总长，计算水位
     * @param overallLength 水尺刻度预测结果
     * @param  prediction 水尺总长
     * @return int
     */
    private static int inference(double overallLength, double prediction){
        double aboveLength = Math.floor(prediction/3)*5+Math.floor(Math.pow(3,prediction%3-1));
        return (int)(overallLength-aboveLength);
    }

    /**
     * 读取传入的图片byte数组
     * @param img byte[]
     * @return BufferedImage
     * @throws IOException
     */
    static BufferedImage readImage(byte[] img) throws IOException {
        InputStream is = new ByteArrayInputStream(img);

        if (null == is) {
            logger.info("InputStream is null!");
            return null;
        }

        BufferedImage bf = ImageIO.read(is);

        if (null == bf) {
            logger.info("BufferedImage is null!");
            return null;
        }

        if (bf.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            throw new IOException(
                    String.format(
                            "Expected 3-byte BGR encoding in BufferedImage, found %d .",
                            bf.getType()));
        }
        return bf;
    }

    /**
     * 将读取的图片数据转换为模型需要的Tensor<UInt8>格式的数据
     * @param bf BufferedImage
     * @return Tensor<UInt8>
     * @throws IOException
     */
    static Tensor<UInt8> inputImageData(BufferedImage bf) throws IOException {
        byte[] data = ((DataBufferByte) bf.getData().getDataBuffer()).getData();
        bgr2rgb(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int height = bf.getHeight();
        int width = bf.getWidth();
        long[] shape = new long[]{height, width, 3};
        return Tensor.create(UInt8.class, shape, buffer);
    }

    /**
     * 将传入的BGR格式的图片数据转为RGB格式的
     * @param data byte[]
     */
    private static void bgr2rgb(byte[] data) {
        for (int i = 0; i < data.length; i += 3) {
            byte tmp = data[i];
            data[i] = data[i + 2];
            data[i + 2] = tmp;
        }
    }

    //获取图片的高，并转换为Tensor<Integer>格式的数据
    static Tensor<Integer> imageHeight(BufferedImage bf) throws IOException {
        int height = bf.getHeight();
        return Tensor.create(height, Integer.class);
    }

    //获取图片的宽，并转换为Tensor<Integer>格式的数据
    static Tensor<Integer> imageWidth(BufferedImage bf) throws IOException {
        int width = bf.getWidth();
        return Tensor.create(width, Integer.class);
    }

    /**
     * 整合水尺对象检测模型和水尺刻度预测模型
     * @param img byte[]
     * @param thresh float
     * @param maxHeight int
     * @return int
     * @throws IOException
     */
    public static int evaluate(byte[] img, float thresh, int maxHeight)
            throws IOException {
        BufferedImage image = readImage(img);
        Tensor<UInt8> inputImageData = inputImageData(image);
        Tensor<Integer> imageHeight = imageHeight(image);
        Tensor<Integer> imageWidth = imageWidth(image);

        float[][] boxes = detector.detect(inputImageData, imageHeight, imageWidth, thresh);
        //-1表示图片数据异常
        if (null == boxes) {
            logger.info("Boxes is null!");
//           System.out.println("Boxes is null!");
            return -1;
        }

        int[] object_class = classifier.classify(inputImageData, boxes);
        if (null == object_class) {
            logger.info("Object_class is null!");
//            System.out.println("Object_class is null!");
            return -2;
        }
//        System.out.print("分类结果：");
//        System.out.print(object_class[0]);

        if (object_class[0] == 1) {
            float[][] accurateBox = multiDetector.multiDetect(inputImageData, imageHeight, imageWidth, thresh);
            if (null == accurateBox) {
                logger.info("accurateBox is null!");
//                System.out.println("Object_class is null!");
                return -3;
            }

            int[][] prediction_output = predictor.predict(inputImageData, accurateBox);
            if (null == prediction_output) {
                logger.info("Prediction_output is null!");
                return -4;
            }

            double prediction = prediction_output[0][0];
//            System.out.print("横线数：");
//            System.out.print(prediction);
            int predictResult = inference((double) maxHeight, prediction);
            if (predictResult<0 && maxHeight > 0){
                return 0;
            }
            return predictResult;
        }

        logger.info("The water gauge is bad!");
        return -2;
    }
}