import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * Пункт 2
 */
public class P2 {
    public static void main(String[] args) {
        try {
            var tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");                               //Инициализация tesseract
            var image = ImageIO.read(new File("letters.png"));
            var resultImage = Utils.cloneImage(image);                               //Создание копии изображения, на ней я буду отрисовывать рамки
            Utils.removeColors(image);                                               //Превращение изображения в чёрно-белую для более точной работы tesseract
            var list = tesseract.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_SYMBOL); //Определение положения букв с помощью tesseract
            for (var rect : list) {
                Utils.drawBorder(resultImage, rect, Color.RED);                     //Отрисовка рамок
            }
            Utils.saveImage(resultImage, "letters_with_borders.png");          //Сохранение изображения с рамками
        }
        catch (TesseractException e) {
            System.err.println("Failed to init tesseract: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        }
    }
}
/*
Результат работы: letters_with_borders.png
 */
