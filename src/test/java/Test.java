
import org.elsquatrecaps.portada.portadaimagetools.FixBackTransparencyTool;

/**
 *
 * @author josep
 */
public class Test {
    
    public static void main(String[] args){
        FixBackTransparencyTool prg = new FixBackTransparencyTool();
        prg.setImagePath("data/imatgeTransparencia.jpg");
        prg.fixTransparency();
        prg.saveImage("data/iamtgeTransparenciaF1.jpg");
        FixBackTransparencyTool.fixTransparencyFromFile("data/imatgeNormal.jpg", "data/imatgeNormalF1.jpg");
    }
    
}
