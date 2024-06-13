
import org.elsquatrecaps.portada.portadaimagetools.FixBackTransparencyTool;

/**
 *
 * @author josep
 */
public class Test {
    
    public static void main(String[] args){
        FixBackTransparencyTool prg = new FixBackTransparencyTool();
        prg.setImagePath("data/inclinada.jpg");
        prg.fixTransparency();
        prg.saveImage("data/inclinadaF1.jpg");
        FixBackTransparencyTool.fixTransparencyFromFile("data/imatgeTransparencia.jpg", "data/imatgeTransparenciaF1.jpg");
    }
    
}
