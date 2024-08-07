import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Пункт 1
 */
public class P1 {
    public static void main(String[] args) {
        try {
            var tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");                               //Инициализация tesseract
            var image = ImageIO.read(new File("letters.png"));
            Utils.removeColors(image);                                               //Превращение изображения в чёрно-белую для более точной работы tesseract
            var text = tesseract.doOCR(image).replace("\n", "");     //Чтение текста с изображения с помощью tesseract
            countLetters(text);                                                      //Подсчёт букв
        }
        catch (TesseractException e) {
            System.err.println("Failed to init tesseract: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        }
    }

    /**
     * Подсчёт и вывод количества букв в тексте
     */
    private static void countLetters(String text) {
        System.out.println("Всего букв найдено: " + text.length());
        var result = new HashMap<Character, Integer>();
        for (var c : text.toCharArray()) {
            if (result.containsKey(c)) {
                result.put(c, result.get(c) + 1);
            } else {
                result.put(c, 1);
            }
        }
        for (var entry : result.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
        }
    }
}
/*
Результат работы:

Всего букв найдено: 13456
A: 651
B: 565
C: 935
D: 498
E: 61
F: 183
G: 557
H: 740
I: 797
J: 322
K: 562
L: 188
M: 727
N: 762
O: 371
P: 86
Q: 188
R: 657
S: 630
T: 169
U: 763
V: 888
W: 526
X: 29
Y: 857
Z: 744
 */
