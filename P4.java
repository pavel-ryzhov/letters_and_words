import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Word;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Пункт 4
 */
public class P4 {
    public static void main(String[] args) {
        try {
            var tesseract = new Tesseract();
            tesseract.setVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");  //Определение допустимых символов для большей точности
            tesseract.setDatapath("tessdata");                               //Инициализация tesseract
            tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_WORD);      //Настройка tesseract, чтобы он ориентировался на одиночные слова
            var image = ImageIO.read(new File("words.png"));
            var rectangles = Utils.detectWords(image, 5);                //Определение положения слов по их цвету
            Utils.removeColors(image);                                               //Превращение изображения в чёрно-белую для более точной работы tesseract
            var images = rectangles.stream().map(rectangle -> {
                var subImage = image.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height); //Вырезание изображения, содержащего одно слово
                return rectangle.width > rectangle.height ? subImage : Utils.rotateImage(subImage);            //Поворот изображения, если оно вертикальное
            }).toList();
            System.out.println("Всего слов найдено: " + images.size());             //Уже тут известно общее количество слов
            var words = tesseract.getWords(images, ITessAPI.TessPageIteratorLevel.RIL_WORD).stream().map(Word::getText).toList();  //Получение текста из изображений с помощью tesseract
            countWords(words);                                                      //Подсчёт слов
        } catch (IOException e) {
            System.err.println("Failed to read image file: " + e.getMessage());
        }

    }

    /**
     * Подсчёт и вывод количества слов
     */
    private static void countWords(List<String> words) {
        var result = new HashMap<String, Integer>();
        for (var word : words) {
            if (result.containsKey(word)) {
                result.put(word, result.get(word) + 1);
            } else {
                result.put(word, 1);
            }
        }
        result.remove("\f");            //Удаление разделительных символов, добавленных tesseract
        for (var entry : result.entrySet()) {
            System.out.printf("%s: %s\n", entry.getKey(), entry.getValue());
        }
    }
}
/*
Результат работы:

Всего слов найдено: 3047
ATTENTION: 1
DUSTY: 1
DISMISS: 1
CABIN: 1
NEAR: 1
APART: 2
NEAT: 2
CLENCH: 2
PINCH: 1
DISTANT: 2
YEAR: 2
FARM: 3
PONT: 1
MARBLE: 1
WHOEVER: 1
WHERE: 1
SEA: 2
PONY: 1
SEE: 2
VICTORY: 1
...
LABOUR: 1
JACK: 1
RUSH: 1
GROAN: 2
 */
