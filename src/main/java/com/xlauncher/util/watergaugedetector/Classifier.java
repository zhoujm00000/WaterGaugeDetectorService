package com.xlauncher.util.watergaugedetector;
import org.apache.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;
import java.io.*;
import java.util.List;

/**
 * Detector：加载水尺分类预测模型
 * @author liangjia
 * @date 2018-10-09
 */
class Classifier {
    private static SavedModelBundle classifyModel;
    private  String classifierModelPath;
    private static Logger logger = Logger.getLogger(Classifier.class);
    public static Tensor<Integer> object_image;

    public Classifier(String classifierModelPath){
        this.classifierModelPath = classifierModelPath;
    }

    /**
     * 加载水尺分类预测模型
     * @return boolean
     */
    public boolean loadModel(){
        try{
            classifyModel = SavedModelBundle.load(classifierModelPath, "serve");
        }
        catch (Exception e)
        {
            logger.error("加载水尺分类模型失败！");
            return false;
        }

        if (null == classifyModel){
            logger.error("加载水尺分类模型失败，模型为空！");
            return false;
        }
        return true;
    }

    //加载完模型关闭，释放内存
    public void closeModel(){
        if (null != classifyModel) {
            classifyModel.close();
        }
    }

    /**
     *根据保存的水尺分类预测模型，输入图片及图片中水尺boxes，得到水尺对象并预测水尺是good或者bad
     * @param inputImageData Tensor<UInt8>
     * @param boxes float[][]
     * @return predition
     * @throws IOException
     */
    static int[] classify(Tensor<UInt8> inputImageData, float[][] boxes) throws IOException {
        List<Tensor<?>> classifyOutputs;
        try {
            Tensor<Float> input_boxes = Tensor.create(boxes, Float.class);
            int[][] label = {{0},{1}};
            Tensor<Integer> input_label = Tensor.create(label, Integer.class);

            classifyOutputs =
                    classifyModel
                            .session()
                            .runner()
                            .feed("origin_image", inputImageData)
                            .feed("object_boxes", input_boxes)
                            .feed("label", input_label)
                            .fetch("output")
                            //.fetch("object_image_output")
                            .run();
        }catch (Exception e){
            logger.error(e);
            return null;
        }

        if (classifyOutputs.size() == 0){
            logger.info("No model output was obtained!");
            return null;
        }

        Tensor<Integer> output = classifyOutputs.get(0).expect(Integer.class);
        //object_image = classifyOutputs.get(1).expect(Integer.class);

        if(((int) output.shape()[0]) == 0){
            logger.info("The prediction result of the classifier model is null");
            return null;
        }
        //System.out.println(output);
        try{
            int[] predition = output.copyTo(new int[1]);
            return predition;
        }catch (Exception e){
            logger.error(e);
            return null;
        }
    }

}

