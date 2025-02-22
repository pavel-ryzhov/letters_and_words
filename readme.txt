Для компиляции и запуска программы я использовал Oracle OpenJDK 20.
Для определения текста я использовал стороннюю библитотеку "Tess4J Tesseract For Java", вот ссылка на Maven репозиторий: https://mvnrepository.com/artifact/net.sourceforge.tess4j/tess4j
Для работы tesseract также нужна папка tessdata

Для запуска каждого пункта задачи я создал файлы P1.java, P2.java и т.д.; методы, используемые в нескольких пунктах задачи находятся в файле Utils.java


Также я собрал letters_and_words.jar (tesseract туда входит, но папка tessdata всё равно нужна), для запуска пунктов задачи:
java -cp letters_and_words.jar P1
java -cp letters_and_words.jar P2
...


В коде я написал многочисленные комментарии, но хотелось бы еще сказать пару слов по пунктам задачи.
1. Для извлечения букв из изображения я использую tesseract. Но тут я столкнулся с проблемой: tesseract не определял буквы бледных цветов, почти белые.
Поэтому возникла необходимость превратить изображение в чёрно-белое, и только потом определять текст. Чёрно-белое изображение также используется в пунктах 2-4.
Результаты получились такие:
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


2. Для определения границ букв я использовал tesseract. Результат исполнения: letters_with_borders.png


3. Для определения границ букв я снова использовал tesseract. Результат исполнения: letters_with_borders_and_background.png


4. Моя любимая часть задачи. Сначала я думал, что можно просто поменять ITessAPI.TessPageIteratorLevel.RIL_SYMBOL на ITessAPI.TessPageIteratorLevel.RIL_WORD и tesseract успешно обнаружит все слова.
Но результат был неудовлетворительный для меня: хоть большинство горизонтальных слов было определено, было очень много мусора из-за вертикальных слов и очень малых зазоров между словами,
также не определялись слова, где буквы находились почти вплотную или даже сливались. Я долго работал с настройками tesseract и добавил список возможных символов, но существенного результата это не принесло.
Тогда я решил взять ответственность за поиск слов на себя и передавать в tesseract для распознавания уже вырезанные из общего изображения слова. Т.е. мой алгоритм сначала определяет границы всех слов,
по этим границам вырезаются слова, вертикальные переворачиваются, потом передаются для распознавания в tesseract. Tesseract без проблем определяет слова, когда на изображении только одно слово.
Суть алгоритма следующая: с шагом в 10 пикселей по обоим осям я ищу не белые пиксели, при нахождении определяю цвет слова, далее определяю прямоугольную область с шагом в 1 пиксель,
где встречается этот цвет. Качество результата значительно повысилось, теперь слова распознаются почти идеально. Всего слов найдено: 3047, запустив код, можно узнать, какие именно это слова.

5. В данном пункте не важно, какие слова окаймлять, достаточно знать их границы, а значит нет необходимости использовать tesseract. Я снова по цвету определяю границы слов и потом дорисовываю рамки.
Должен отметить, что без tesseract программа работает гораздо быстрее. Результат исполнения: words_with_borders.png

6. В этом пункте всё аналогично пункту 5, я снова не использую сторонних библиотек.
Результат исполнения: words_with_borders_and_background.png



P.S. Я понимаю, что это не относится к сути задачи, но вот ссылки на некоторые мои проекты, которыми я горжусь. Возможно, они сыграют мне на руку во время распределения в группы:
1. https://github.com/pavel-ryzhov/expenses_app (Это менеджер расходов для Android, который залит и активен в Google Play: https://play.google.com/store/apps/details?id=com.expenses.mngr)
2. https://github.com/pavel-ryzhov/sea_battle (Чтобы не рисовать на бумаге поле боя, я решил перенести это в онлайн. Другу достаточно поставить приложение и подключиться к локальной сети, чтобы сыграть)