import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Сюда я выносил все методы, использующиеся в нескольких пунктах задачи
 */
public final class Utils {
    /**
     * Метод превращает цветное изображение в чёрно-белое, нужен для более точной работы tesseract
     */
    public static void removeColors(BufferedImage image) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(i, j) != Color.WHITE.getRGB()) {
                    image.setRGB(i, j, Color.BLACK.getRGB());
                }
            }
        }
    }

    /**
     * Метод отрисовывает прямоугольную рамку
     */
    public static void drawBorder(BufferedImage image, Rectangle rectangle, Color color) {
        var graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(rectangle.x - 5, rectangle.y - 5, rectangle.width + 10, 3);
        graphics.fillRect(rectangle.x - 5, rectangle.y - 5, 3, rectangle.height + 10);
        graphics.fillRect(rectangle.x - 5, rectangle.y - 5 + rectangle.height + 10 - 3, rectangle.width + 10, 3);
        graphics.fillRect(rectangle.x - 5 + rectangle.width + 10 - 3, rectangle.y - 5, 3, rectangle.height + 10);
    }

    /**
     * Метод создаёт копию изображения
     */
    public static BufferedImage cloneImage(BufferedImage image) {
        var result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        var g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    /**
     * Метод определяет цвет буквы или слова, ища самый "далёкий" от белого цвет
     */
    public static Color detectColor(BufferedImage image) {
        var result = Color.WHITE;
        var maxDistance = 0d;                        //Максимальное Евклидово расстояние в пространстве RGB
        var offsetX = image.getWidth() >= 40 ? 10 : 0;                         //Сужение области поиска цвета, потребность в это возникла при
        var offsetY = image.getHeight() >= 40 ? 10 : 0;                        //определении цвета слов, находящихся практически вплотную
        for (int i = offsetX; i < image.getWidth() - offsetX; i++) {
            for (int j = offsetY; j < image.getHeight() - offsetY; j++) {
                var color = new Color(image.getRGB(i, j));
                var distance = countDistance(color, Color.WHITE);           //Подсчёт расстояния
                if (distance > maxDistance) {
                    result = color;
                    maxDistance = distance;
                }
            }
        }
        return result;
    }

    /**
     * Отрисовывает рамку и фон
     */
    public static void drawBackgroundAndBorder(BufferedImage image, Rectangle rectangle) {
        var subImage = cloneImage(image.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height));    //Создание копии части изображения, под которым нужно отрисовать фон
        var color = detectColor(subImage);                      //Определение цвета слова или буквы
        var graphics = image.getGraphics();
        graphics.setColor(new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()));      //Установка цвета фона
        graphics.fillRect(rectangle.x - 13, rectangle.y - 13, rectangle.width + 26, rectangle.height + 26);     //Отрисовка фона
        removeBackground(subImage, color.getRGB());                                                                     //Замена первоначального белого фона слова или буквы на прозрачный
        graphics.drawImage(subImage, rectangle.x, rectangle.y, null);               //Отрисовка фона
        drawBorder(image, rectangle, Color.BLACK);                                          //Отрисовка рамки
    }

    /**
     * Метод сохраняет изображение в файл
     */
    public static void saveImage(BufferedImage image, String name) {
        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException e) {
            System.err.println("Failed to write image: " + e.getMessage());
        }
    }

    /**
     * Поворачивает изображение на 90°
     */
    public static BufferedImage rotateImage(BufferedImage image) {
        var result = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                result.setRGB(image.getHeight() - j - 1, i, image.getRGB(i, j));
            }
        }
        return result;
    }
    public static List<Rectangle> detectWords(BufferedImage image) {
        return detectWords(image, 0);
    }

    /**
     * Метод определяет границы слов по их цвету
     * @param borderSpacing зазор между возвращаемой границей и словом
     */
    public static List<Rectangle> detectWords(BufferedImage image, int borderSpacing) {
        var result = new ArrayList<Rectangle>();
        for (int j = 0; j < image.getHeight(); j += 10) {           //Итерация по матрице с шагом в 10 пикселей
            for (int i = 0; i < image.getWidth(); i += 10) {        //
                if (image.getRGB(i, j) != Color.WHITE.getRGB() && !insideRectangles(result, i, j)) {   //Обнаружение не белых пикселей и проверка, чтобы эти пиксели не принадлежали уже найденному слову
                    var color = detectColor(image.getSubimage(i - 2, j - 2, 5, 5));  //Определение цвета слова. Область берётся для того, чтобы исключить ошибки, ведь цвет каёмки буквы не совпадает с истинным цветом буквы
                    var minX = i;
                    var maxX = i;       //Граничные координаты прямоугольной области слова
                    var minY = j;
                    var maxY = j;
                    var attempts = 0;   //Белый пиксель - не повод прекращать искать слово, ведь это может быть конец буквы или даже пустоты в самой букве.
                                        //Эта переменная служит для хранения количеста пустых пикселей при итерации по строке.
                    while (maxX + attempts < image.getWidth() && attempts < 50) {   //Поиск цветных пикселей от старта вправо, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                        var colorFound = image.getRGB(maxX + attempts, j) == color.getRGB();    //Переменная хранит, был ли найден подходящий пиксель за итерацию цикла
                        var currentMinY = j; //Нельзя использовать сразу minY и maxY, т.к. мы итерируемся по вертикали множество раз
                        var currentMaxY = j;
                        var verticalAttempts = 0; //Служит для хранения количества пустых пикселей при итерации по вертикали.
                        while (currentMaxY + verticalAttempts < image.getHeight() && verticalAttempts < 50) { //Поиск цветных пикселей от старта вниз, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                            if (image.getRGB(maxX + attempts, currentMaxY + verticalAttempts) == color.getRGB()) {  //Проверка текущего пикселя
                                currentMaxY += verticalAttempts; //Тут verticalAttempts может быть не равно нулю, если через промежуток из пустых неподходящих пикселей был найден пиксель нужного цвета
                                currentMaxY++;
                                verticalAttempts = 0;
                                colorFound = true;
                            } else verticalAttempts++;
                        }
                        verticalAttempts = 0;
                        while (currentMinY - verticalAttempts > 0 && verticalAttempts < 50) { //Поиск цветных пикселей от старта вверх, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                            if (image.getRGB(maxX + attempts, currentMinY - verticalAttempts) == color.getRGB()) {
                                currentMinY -= verticalAttempts;
                                currentMinY--;
                                verticalAttempts = 0;
                                colorFound = true;
                            } else verticalAttempts++;
                        }
                        if (colorFound) {
                            maxY = Math.max(maxY, currentMaxY); //Установка максимального и минимального значений границ слова
                            minY = Math.min(minY, currentMinY);
                            maxX += attempts; //Тут attempts может быть не равно нулю, если через промежуток из пустых неподходящих пикселей был найден пиксель нужного цвета
                            maxX++;
                            attempts = 0;
                        } else attempts++;
                    }
                    attempts = 0;
                    while (minX - attempts > 0 && attempts < 50) { //Поиск цветных пикселей от старта влево, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                        var colorFound = image.getRGB(minX - attempts, j) == color.getRGB();
                        var currentMinY = j;
                        var currentMaxY = j;
                        var verticalAttempts = 0;
                        while (currentMaxY + verticalAttempts < image.getHeight() && verticalAttempts < 50) { //Поиск цветных пикселей от старта вниз, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                            if (image.getRGB(minX - attempts, currentMaxY + verticalAttempts) == color.getRGB()) {
                                currentMaxY += verticalAttempts;
                                currentMaxY++;
                                verticalAttempts = 0;
                                colorFound = true;
                            } else verticalAttempts++;
                        }
                        verticalAttempts = 0;
                        while (currentMinY - verticalAttempts > 0 && verticalAttempts < 50) { //Поиск цветных пикселей от старта вверх, пока не достигнута граница изображения или последние 50 пикселей неподходящего цвета
                            if (image.getRGB(minX - attempts, currentMinY - verticalAttempts) == color.getRGB()) {
                                currentMinY -= verticalAttempts;
                                currentMinY--;
                                verticalAttempts = 0;
                                colorFound = true;
                            } else verticalAttempts++;
                        }
                        if (colorFound) {
                            maxY = Math.max(maxY, currentMaxY);
                            minY = Math.min(minY, currentMinY);
                            minX -= attempts;
                            minX--;
                            attempts = 0;
                        } else attempts++;
                    }
                    minY -= borderSpacing;  //Добавление зазоров между словом и возращаемой границей
                    minX -= borderSpacing;
                    maxY += borderSpacing;
                    maxX += borderSpacing;
                    if (minX < 0) minX = 0; //Проверка на соответствме границ изображения
                    if (minY < 0) minY = 0;
                    if (maxX > image.getWidth()) maxX = image.getWidth();
                    if (maxY > image.getHeight()) maxY = image.getHeight();
                    if (minX + borderSpacing * 2 == maxX || minY + borderSpacing * 2 == maxY) continue; //Это на тот случай, если был найден единичный пиксель вместо слова
                    var width = maxX - minX;
                    var height = maxY - minY;
                    if (!insideRectangles(result, minX + width / 2, minY + height / 2)) //Проверка, не было ли слово уже найдено
                        result.add(new Rectangle(minX, minY, width, height));   //Добавление границ слова во возвращаемое значение
                }
            }
        }
        return result;
    }

    /**
     * Метод заменяет белый фон на прозрачный и не белые пиксели на цвет color (например каёмку буквы на цвет буквы)
     */
    private static void removeBackground(BufferedImage image, int color) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, image.getRGB(i, j) == Color.WHITE.getRGB() ? 0x00000000 : color);
            }
        }
    }

    /**
     * Проверяет, находится ли точка внутри одного из прямоугольников
     */
    private static boolean insideRectangles(List<Rectangle> rects, int x, int y) {
        for (var rect : rects) {
            if (rect.contains(x, y)) return true;
        }
        return false;
    }

    /**
     * Считает Евклидово расстояние между двумя цветами
     */
    private static double countDistance(Color c1, Color c2) {
        return Math.sqrt(Math.pow(c1.getRed() - c2.getRed(), 2) + Math.pow(c1.getGreen() - c2.getGreen(), 2) + Math.pow(c1.getBlue() - c2.getBlue(), 2));
    }
}
