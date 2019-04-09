package com.xlauncher.util.watergaugedetector;

import org.apache.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Detector：水尺对象精确定位模型
 * @author liangjia
 * @date 2018-12-24
 */

public class MultiDetector {

    private static Logger logger = Logger.getLogger(MultiDetector.class);
    private  String multiDetectorModelPath;
    private  static SavedModelBundle savedModels[];
    private static int modelNumber;

    public MultiDetector(String multiDetectorModelPath){
        this.multiDetectorModelPath = multiDetectorModelPath;
    }

    /**
     * 加载多个对象检测模型
     * @return boolean
     */
    public boolean loadModel(){
        boolean loadModel = false;
        File dir = new File(multiDetectorModelPath);
        File[] multiDetectorModelPaths = dir.listFiles();

        if (multiDetectorModelPaths == null){
            logger.info("模型文件夹为空！");
            return false;
        }
        boolean loadModeltrue[] = new boolean[multiDetectorModelPaths.length];
        modelNumber = multiDetectorModelPaths.length;
        savedModels = new SavedModelBundle[multiDetectorModelPaths.length];
        for (int i=0; i<multiDetectorModelPaths.length; i++){
            try {
                savedModels[i] = SavedModelBundle.load(multiDetectorModelPaths[i].getPath(), "serve");
            }
            catch (Exception e)
            {
                logger.error("加载其中一个对象检测模型失败！");
                loadModeltrue[i] = false;
            }
            if (null == savedModels[i]){
                logger.error("加载其中一个对象检测模型失败，模型为空！");
                loadModeltrue[i] = false;
            }
            loadModeltrue[i] = true;
        }
        for (boolean b : loadModeltrue){
            if(!b){
                logger.error("加载水尺精确定位模型失败！");
                loadModel = false;
            }
            loadModel = true;
        }
        return loadModel;
    }
    //加载完模型关闭，释放内存
    public void closeModel(){
        for (int i=0; i<modelNumber; i++){
            if (null != savedModels[i]) {
                savedModels[i].close();
            }
        }
    }

    /**
     *计算几个模型预测的boxes平均值
     * @param boxes float[][]
     * @return float[][]
     */
    static float[][] computeMean(float[][] boxes){
        float[][] mean = new float[1][boxes[0].length];
        for (int i=0; i<boxes[0].length; i++){
            int numBoexes = 0;
            for (int j=0; j<boxes.length; j++){
                if(boxes[j] != null){
                    mean[0][i] += boxes[j][i];
                    numBoexes += 1;
                }
            }
            mean[0][i] = mean[0][i]/(numBoexes);
        }
        return mean;
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
    static float[][] multiDetect(Tensor<UInt8> inputImageData, Tensor<Integer> imageHeight, Tensor<Integer> imageWidth,
                            float thresh) throws IOException{
        float[][] allBoxes = new float[modelNumber][4];
        for (int i=0;i<modelNumber; i++){
            List<Tensor<?>> detectorOutputs;
            try {
                detectorOutputs = savedModels[i]
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
                System.out.println("No model output was obtained!");
                logger.info("No model output was obtained!");
                allBoxes[i] = null;
            }

            float[][] boxes;
            Tensor<Float> boxesT = detectorOutputs.get(0).expect(Float.class);
            Tensor<Float> scoresT = detectorOutputs.get(1).expect(Float.class);
            int numberObjects = (int) boxesT.shape()[0];

            if (numberObjects == 0){
                System.out.println("There is no object detected in one of the detected models！");
                logger.info("There is no object detected in one of the detected models！");
                allBoxes[i] = null;
            }else if (numberObjects > 1) {
                System.out.println("There are more than one objects detected in one of the detected models！");
                logger.info("There are more than one objects detected in one of the detected models！");
                allBoxes[i] = null;
            }else if (((int) boxesT.shape()[1]) == 0){
                System.out.println("There is one object detected in one of the detected models, but boxes is null！");
                logger.info("There is one object detected in one of the detected models, but boxes is null！");
                allBoxes[i] = null;
            }

            float[] scores = scoresT.copyTo(new float[numberObjects]);
            if(scores[0]<thresh){
                System.out.println("The probability of detecting the object is below the threshold in one of the detected models！");
                logger.info("The probability of detecting the object is below the threshold in one of the detected models！");
                allBoxes[i] = null;
            }

            boxes = boxesT.copyTo(new float[numberObjects][4]);
            if (allBoxes[i] != null){
                allBoxes[i] = boxes[0];
            }
        }
        return computeMean(allBoxes);
    }
}
