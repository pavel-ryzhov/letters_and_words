import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * Пункт 5
 */
public class P5 {
    public static void main(String[] args) {
        try {
            var image = ImageIO.read(new File("words.png"));
            var list = Utils.detectWords(image);                            //Определение положения слов по их цвету
            for (var rect : list) {
                Utils.drawBorder(image, rect, Color.RED);                   //Отрисовка рамок
            }
            Utils.saveImage(image, "words_with_borders.png");
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        }
    }
}
/*
Результат работы: words_with_borders.png
 */
