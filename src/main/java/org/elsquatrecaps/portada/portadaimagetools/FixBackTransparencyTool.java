package org.elsquatrecaps.portada.portadaimagetools;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author josep
 */
public class FixBackTransparencyTool {
    private static FixBackTransparencyTool instance = null;
    private String imagePath;
    private Mat image;
    static {
        nu.pattern.OpenCV.loadLocally();
    }
    
    public void readImage(String imagePath){
        this.setImagePath(imagePath);
    }
    
    public void saveImage(){
        this.saveImage(this.imagePath);
    }
    
    public void saveImage(String imagePath){
        Imgcodecs.imwrite(imagePath, image);
    }
    
    public void fixTransparency(){
        // Aplicar un umbral para obtener una imagen binaria
        Mat binaryImage = new Mat();
        Imgproc.threshold(this.image, binaryImage, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Mat dilatedImage = new Mat();
        Imgproc.dilate(binaryImage, dilatedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 1)));

        // Encontrar contornos en la imagen binaria
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        Scalar meanScalar = Core.mean(this.image);
        double meanIntensity = meanScalar.val[0] ;
        double thresholdIntensity = (meanIntensity + Core.minMaxLoc(this.image).minVal)*0.5;
        

        // Dibujar los contornos en una imagen de salida
        Mat contourImage = new Mat(this.image.size(), CvType.CV_8U, new Scalar(255));
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Mat countourRegion = this.image.submat(rect);
            double min = Core.minMaxLoc(countourRegion).minVal;
            if(min<thresholdIntensity){
                Imgproc.drawContours(contourImage, contours, i, new Scalar(0), -1); // Rellenar contorno
            }
        }
        
        //Core.bitwise_not(contourImage, contourImage);        
        Mat modifiedImage = new Mat();
        Core.bitwise_or(this.image, contourImage, modifiedImage);
        
        // Recorrer cada píxel de la imagen
        for (int y = 0; y < modifiedImage.rows(); y++) {
            for (int x = 0; x < this.image.cols(); x++) {
                // Obtener el valor del píxel actual
                double pixelValue = modifiedImage.get(y, x)[0];
                
                // Verificar si el valor del píxel coincide con el tono de gris que deseas cambiar
                if (pixelValue == 255) {
                    // Actualizar el valor del píxel con el nuevo tono de gris
                    modifiedImage.put(y, x, new double[] { meanIntensity*1.1});
                }
            }
        }
        this.image = modifiedImage;        
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
    }

    /**
     * @return the image
     */
    public Mat getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Mat image) {
        if(image.channels()==3){
            Imgproc.cvtColor(image, this.image, Imgproc.COLOR_BGR2GRAY);
        }else{
            this.image = image.clone();
        }
    }
    
    public static FixBackTransparencyTool getInstance(){
        if(instance==null){
            instance = new FixBackTransparencyTool();
        }
        return instance;
    }
    
    public static Mat fixTransparencyFromMatImage(Mat opencvImage){
        FixBackTransparencyTool locInstance = getInstance();
        locInstance.setImage(opencvImage);
        locInstance.fixTransparency();
        return locInstance.getImage();        
    }
    
    public static void fixTransparencyFromFile(String inputPath){
        fixTransparencyFromFile(inputPath, inputPath);
    }
    
    public static void fixTransparencyFromFile(String inputPath, String outputPath){
        FixBackTransparencyTool locInstance = getInstance();
        locInstance.setImagePath(inputPath);
        locInstance.fixTransparency();
        locInstance.saveImage(outputPath);
    }
}
