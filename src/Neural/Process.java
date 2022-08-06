package Neural;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Process {
    public static Map<Character, Integer> alphabet = new HashMap<>() {
        {
            put('a', 0);
            put('b', 0);
            put('c', 0);
            put('d', 0);
            put('e', 0);
            put('f', 0);
            put('g', 0);
            put('h', 0);
            put('i', 0);
            put('j', 0);
            put('k', 0);
            put('l', 0);
            put('m', 0);
            put('n', 0);
            put('o', 0);
            put('p', 0);
            put('q', 0);
            put('r', 0);
            put('s', 0);
            put('t', 0);
            put('u', 0);
            put('v', 0);
            put('w', 0);
            put('x', 0);
            put('y', 0);
            put('z', 0);
        }
    };
    public static Map<String, Perceptron> network = new HashMap<>();
    public static List<String> languages = new ArrayList<>();
    static int lNumber = 0;

    public static void flow() throws IOException {
        String learnDir = "lang";
        languages = Process.listLanguages(learnDir);
        Map<String, String> dataMap = new HashMap<>();
        //!!!!!!!!!!!!
        Map<String, List<double[]>> dMap = new HashMap<>();

        for (String l : languages) {
            String lDir = learnDir + "\\" + l;
            getList(lDir, dMap);
        }
        System.out.println(dMap.keySet());
        //!!!!!!!!!!!!

        for (String language : languages) {
            String mergedFile = language + ".txt";
            Process.mergeLanguage(learnDir + "\\" + language, mergedFile);
            String text = Files.readString(Path.of(mergedFile));
            dataMap.put(language, text);
        }

        Map<String, double[]> vectors = new HashMap<>();
        dataMap.forEach((language, data) -> {
            vectors.put(language, createVector(data));
            Perceptron perceptron = new Perceptron(language, 0.5);
            network.put(language, perceptron);
        });
        System.out.println(languages);
        Perceptron p1 = network.get("English");
        Perceptron p2 = network.get("Polish");
        Perceptron p3 = network.get("German");


        for (int i = 0; i < 200000; i++ ) {
            p1.learn(vectors.get("English"), "English");
            p1.learn(vectors.get("Polish"), "Polish");
            p1.learn(vectors.get("German"), "German");

            p2.learn(vectors.get("English"), "English");
            p2.learn(vectors.get("Polish"), "Polish");
            p2.learn(vectors.get("German"), "German");

            p3.learn(vectors.get("English"), "English");
            p3.learn(vectors.get("Polish"), "Polish");
            p3.learn(vectors.get("German"), "German");
        }
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

    }
    public static void test() throws IOException {
        String testData = Files.readString(Path.of("lang.test\\German\\3.txt"));
        for (String language : network.keySet()) {
            Perceptron perceptron = network.get(language);
            System.out.println(perceptron);
            System.out.println(perceptron.calculateNet(createVector(testData)));
            //if (perceptron.calculateNet(createVector(testData)) == 1)
                //System.out.println(language);
        }
    }

    public static List<String> listLanguages(String dir) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir)).skip(1)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    public static void mergeLanguage(String dirPath, String resultFileName) {
        try {
            Path resultFilePath = Paths.get(resultFileName);
            List<Path> pathsToFiles = getPaths(dirPath);
            List<String> resultContent = new ArrayList<>();

            for (Path file : pathsToFiles) {
                List<String> fileLines = Files.readAllLines(file);
                resultContent.addAll(fileLines);
            }

            Files.write(resultFilePath, resultContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getList(String dirPath, Map<String, List<double[]>> map) throws IOException {
        List<double[]> vectors = new ArrayList<>();
        Path mainDirectory = Paths.get(dirPath);
        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file)) {
                    vectors.add(createVector(Files.readString(file)));
                }
                return FileVisitResult.CONTINUE;
            }
        });
        map.put(languages.get(lNumber), vectors);
        lNumber++;
    }

    public static List<Path> getPaths(String dirPath) throws IOException {
        List<Path> pathsToFiles = new ArrayList<>();
        Path mainDirectory = Paths.get(dirPath);

        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (Files.isRegularFile(file))
                    pathsToFiles.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return pathsToFiles;
    }



    public static double[] createVector(String text) {
        alphabet.replaceAll((k, v) -> v = 0);
        char[] strArray = text.toCharArray();
        double charCounter = 0;
        for (Character c : strArray) {
            if (alphabet.containsKey(c)) {
                alphabet.put(c, alphabet.get(c) + 1);
                charCounter++;
            }
        }

        double[] vector = new double[27];
        int counter = 0;
        for (double value : alphabet.values()) {
            vector[counter] = value / charCounter;
            counter++;
        }
        vector[26] = -1.0;
        return vector;
    }
}
