package com.xlauncher.util.watergaugedetector;
import java.io.*;

public class Test {

    public Test(){
    }

    public static void main(String[] args) throws IOException {
        String detectorModelPath = args[0];
        String classifierModelPath = args[1];
        String predictorModelPath = args[2];
        String imgPath = args[3];
        String multiDetectorModelPath = args[4];

        float thresh = 0.5f;
        int maxHeight = 200;

        Evaluator evaluator = Evaluator.getInstance(detectorModelPath, classifierModelPath, multiDetectorModelPath, predictorModelPath);
        if (null == evaluator) {
            System.err.println("Load Evaluator model failed, exit.");
        }
        System.out.println("Evaluator create success.");

        File file  = new File(imgPath);
        if(!file.isDirectory()){
            System.out.println("文件夹地址不存在");
        }else{
            String[] filelist = file.list();
            for(int i = 0; i < filelist.length; i++){
                File readfile = new File(imgPath+"/"+filelist[i]);

                byte[] imageData = makeImageTensor(readfile);
                if ( null == imageData){
                    System.out.println("imageData is null exit.");
                }

                System.out.print(readfile.getName());
                int eval = evaluator.evaluate(imageData, thresh, maxHeight);
                System.out.print("刻度预测结果：");
                System.out.println(eval);
            }
        }
        evaluator.closeModel();
    }

    private static byte[] makeImageTensor(File filename) throws IOException {
        //File filename1 = new File(filename);
        if(!filename.exists()){
            System.out.printf("文件不存在！");
        }
        InputStream is = new FileInputStream(filename);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 12];
        int n ;
        while ((n = is.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();

//        BufferedImage img = ImageIO.read(filename1);
//        if (img.getType() != BufferedImage.TYPE_3BYTE_BGR) {
//            throw new IOException(
//                    String.format(
//                            "Expected 3-byte BGR encoding in BufferedImage, found %d (file: %s). This code could be made more robust",
//                            img.getType(), filename));
//        }
//        return ((DataBufferByte) img.getData().getDataBuffer()).getData();
    }

}
