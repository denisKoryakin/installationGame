import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        String[][] dirGame = {{"C://Games/"}, {"src", "res", "savegames", "temp"}};
        String[][] dirSrc = {{"C://Games/src/"}, {"main", "test"}};
        String[][] dirRes = {{"C://Games/res/"}, {"drawables", "vectors", "icons"}};
        String[][] filesMain = {{"C://Games/src/main/"}, {"Main.java", "Utils.java"}};
        String[][] filesTemp = {{"C://Games/temp/"}, {"temp.txt"}};

        StringBuilder log = new StringBuilder();
        Date date = new Date();

// создаем файлы и директории
        List<File> gameDir = directoriesMaker(dirGame, log, date);
        List<File> srcDir = directoriesMaker(dirSrc, log, date);
        List<File> resDir = directoriesMaker(dirRes, log, date);
        List<File> mainFiles = filesMaker(filesMain, log, date);
        List<File> tempFiles = filesMaker(filesTemp, log, date);

//        Запись логов в файл
        try (FileWriter fileWriter = new FileWriter("C://Games/temp/temp.txt", true)) {
            fileWriter.write(String.valueOf(log));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

//      Создание объектов сохранения игры
        List<GameProgress> gameProgresses = new ArrayList<GameProgress>();
        GameProgress save1 = new GameProgress(85, 12, 11, 254.5);
        GameProgress save2 = new GameProgress(92, 5, 4, 110.4);
        GameProgress save3 = new GameProgress(80, 17, 15, 313.3);
        gameProgresses.add(save1);
        gameProgresses.add(save2);
        gameProgresses.add(save3);
        List<String> paths = new ArrayList<>();

//        Запись сохранений
        for (int i = 0; i < gameProgresses.size(); i++) {
            saveGame("C://Games/savegames/save" + (i + 1) + ".txt", gameProgresses.get(i));
            paths.add("C://Games/savegames/save" + (i + 1) + ".txt");
        }

        String zip = "C://Games/savegames/saves.zip";

//        Архивирование сохранений
        zipFiles(zip, paths);

        //    Удаление неархивированных файлов
        for (File item : gameDir.get(2).listFiles()) {
            if (!item.equals(new FileInputStream(zip))) {
                item.delete();
            }
        }
    }

    // метод создания директорий -------------------------------------------------------------------------------------------
    public static List<File> directoriesMaker(String[][] directories, StringBuilder log, Date date) {
        List<File> dir = new ArrayList<>();
        for (int i = 0; i < directories[1].length; i++) {
            File directory = new File(directories[0][0] + directories[1][i]);
            dir.add(directory);
            if (directory.mkdir()) {
                log
                        .append("Директория ")
                        .append(directories[0][0])
                        .append(directories[1][i])
                        .append(" создана ")
                        .append(date.toString())
                        .append("\n");
            } else {
                log
                        .append("Директория ")
                        .append(directories[0][0])
                        .append(directories[1][i])
                        .append(" не создана ")
                        .append(date.toString())
                        .append("\n");
            }
        }
        return dir;
    }

    // метод создания файлов -----------------------------------------------------------------------------------------------
    public static List<File> filesMaker(String[][] files, StringBuilder log, Date date) {
        List<File> f = new ArrayList<>();
        for (int i = 0; i < files[1].length; i++) {
            File file = new File(files[0][0], files[1][i]);
            try {
                f.add(file);
                if (file.createNewFile())
                    log
                            .append("Файл ")
                            .append(files[0][0])
                            .append(files[1][i])
                            .append(" создан ")
                            .append(date.toString())
                            .append("\n");
            } catch (IOException ex) {
                log
                        .append("Файл ")
                        .append(files[0][0])
                        .append(files[1][i])
                        .append(" не создан ")
                        .append(date.toString())
                        .append("\n");
            }
        }
        return f;
    }

    // метод создания сохранения игры --------------------------------------------------------------------------------------
    public static void saveGame(String path, GameProgress save) {
// откроем выходной поток для записи в файл
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
// запись экземпляра класса в файл
            oos.writeObject(save);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // метод архивирования файлов сохранений -------------------------------------------------------------------------------
    public static void zipFiles(String pathZip, List<String> paths) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathZip))) {
            for (int i = 0; i < paths.size(); i++) {
                try (FileInputStream fis = new FileInputStream(paths.get(i))) {
                    ZipEntry entry = new ZipEntry("save" + (i + 1) + ".txt");
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

