package com.xlauncher.util.watergaugedetector;
import org.apache.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Detector：加载水尺对象检测模型
 * @author liangjia
 * @date 2018-10-09
 */
class Detector {
    private static SavedModelBundle detectorModel;
    private static Logger logger = Logger.getLogger(Detector.class);
    private  String detectorModelPath;

    public Detector(String detectorModelPath){
        this.detectorModelPath = detectorModelPath;
    }

    /**
     * 加载对象检测模型
     * @return boolean
     */
    public boolean loadModel(){
        try {
            detectorModel = SavedModelBundle.load(detectorModelPath, "serve");
        }
        catch (Exception e)
        {
            logger.error("加载水尺检测模型失败！");
            return false;
        }

        if (null == detectorModel){
            logger.error("加载水尺检测模型失败，模型为空！");
            return false;
        }
        return true;
    }
    //加载完模型关闭，释放内存
    public void closeModel(){
        if (null != detectorModel) {
            detectorModel.close();
        }
    }

    /**
     *根据加载的对象检测模型，输入一张图片得到图片中对象的位置及分数。
     * @param inputImageData Tensor<UInt8>
     * @param imageHeight Tensor<Integer>
     * @param imageWidth Tensor<Integer>
     * @param thresh float
     * @return float[][]
     * @throws IOException
     */
    static float[][] detect(Tensor<UInt8> inputImageData, Tensor<Integer> imageHeight, Tensor<Integer> imageWidth,
                            float thresh) throws IOException{

        List<Tensor<?>> detectorOutputs;
        try {
            detectorOutputs = detectorModel
                    .session()
                    .runner()
                    .feed("input_01", inputImageData)
                    .feed("input_02", imageHeight)
                    .feed("input_03", imageWidth)
                    .fetch("boxes")
                    .fetch("scores")
                    .run();
        }
        catch (Exception e){
            logger.error(e);
            return null;
        }

        if (detectorOutputs.size() == 0){
            logger.info("No model output was obtained!");
            return null;
        }

        float[][] boxes;
        Tensor<Float> boxesT = detectorOutputs.get(0).expect(Float.class);
        Tensor<Float> scoresT = detectorOutputs.get(1).expect(Float.class);
        int numberObjects = (int) boxesT.shape()[0];

        if (numberObjects == 0){
            logger.info("There is no object detected！");
            return  null;
        }else if (numberObjects > 1) {
            logger.info("There are more than one objects detected！");
            return  null;
        }else if (((int) boxesT.shape()[1]) == 0){
            logger.info("There is one object detected, but boxes is null！");
            return  null;
        }

        float[] scores = scoresT.copyTo(new float[numberObjects]);
        if(scores[0]<thresh){
            logger.info("The probability of detecting the object is below the threshold！");
            return null;
        }
        boxes = boxesT.copyTo(new float[numberObjects][4]);
        return boxes;
    }
}

