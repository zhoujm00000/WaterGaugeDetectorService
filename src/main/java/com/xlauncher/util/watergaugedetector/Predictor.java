package com.xlauncher.util.watergaugedetector;
import org.apache.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;

import java.io.*;
import java.util.List;

/**
 * Detector：加载水尺刻度预测模型
 * @author liangjia
 * @date 2018-10-09
 */
class Predictor {
    private static SavedModelBundle predictModel;
    private  String predictorModelPath;
    private static Logger logger = Logger.getLogger(Predictor.class);

    public Predictor(String predictorModelPath){
        this.predictorModelPath = predictorModelPath;
    }

    /**
     * 加载水尺刻度预测模型
     * @return boolean
     */
    public boolean loadModel(){
        try{
            predictModel = SavedModelBundle.load(predictorModelPath, "serve");
        }
        catch (Exception e)
        {
            logger.error("加载刻度预测模型失败！");
            return false;
        }

        if (null == predictModel){
            logger.error("加载刻度预测模型失败，模型为空！");
            return false;
        }
        return true;
    }

    //加载完模型关闭，释放内存
    public void closeModel(){
        if (null != predictModel) {
            predictModel.close();
        }
    }

    /**
     *根据保存的水尺刻度预测模型，输入图片及图片中水尺boxes，得到水尺水面上刻度
     * @param inputImageData Tensor<Integer>
     * @return predition
     * @throws IOException
     */
    static int[][] predict(Tensor<UInt8> inputImageData, float[][] boxes) throws IOException {

        List<Tensor<?>> predictorOutputs;
        try {
            Tensor<Float> input_boxes = Tensor.create(boxes, Float.class);
            int[][] label = {{0}};
            Tensor<Integer> input_label = Tensor.create(label, Integer.class);

            predictorOutputs =
                    predictModel
                            .session()
                            .runner()
                            .feed("origin_image", inputImageData)
                            .feed("object_boxes", input_boxes)
                            .feed("label", input_label)
                            .fetch("output")
                            .run();
        }catch (Exception e){
            logger.error(e);
            return null;
        }

        if (predictorOutputs.size() == 0){
            logger.info("No model output was obtained!");
            return null;
        }

        Tensor<Integer> output = predictorOutputs.get(0).expect(Integer.class);
        if(((int) output.shape()[0]) == 0){
            logger.info("The prediction result of the predictor model is null!");
            return null;
        }else if(((int) output.shape()[1]) == 0){
            logger.info("The prediction result of the predictor model is obtained, but it is null!");
            return null;
        }
        int[][] predition = output.copyTo(new int[1][1]);
//        System.out.print("横线数：");
//        System.out.println(predition[0][0]);
        return predition;
    }

}
