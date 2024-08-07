import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Пункт 6
 */
public class P6 {
    public static void main(String[] args) {
        try {
            var image = ImageIO.read(new File("words.png"));
            var words = Utils.detectWords(image);                           //Определение положения слов по их цвету
            for (var rect : words) {
                Utils.drawBackgroundAndBorder(image, rect);                 //Отрисовка рамок и фона
            }
            ImageIO.write(image, "png", new File("words_with_borders_and_background.png"));
        } catch (IOException e) {
            System.err.println("Failed to read or write image file: " + e.getMessage());
        }
    }
}
/*
Результат работы: words_with_borders_and_background.png
 */