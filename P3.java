import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Пункт 3
 */
public class P3 {
    public static void main(String[] args) {
        try {
            var tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");                               //Инициализация tesseract
            var image = ImageIO.read(new File("letters.png"));
            var resultImage = Utils.cloneImage(image);                                 //Создание копии изображения, на ней я буду отрисовывать рамки и фон
            Utils.removeColors(image);                                                 //Превращение изображения в чёрно-белую для более точной работы tesseract
            var list = tesseract.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_SYMBOL); //Определение положения букв с помощью tesseract
            for (var rect : list) {
                Utils.drawBackgroundAndBorder(resultImage, rect);                      //Отрисовка рамок и фона для каждой буквы
            }
            Utils.saveImage(resultImage, "letters_with_borders_and_background.png"); //Сохранение готового изображения
        }
        catch (TesseractException e) {
            System.err.println("Failed to init tesseract: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        }
    }
}
/*
Результат работы: letters_with_borders_and_background.png
 */