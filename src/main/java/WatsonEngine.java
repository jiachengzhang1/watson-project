import model.Result;
import model.WikiPage;
import utils.Configuration;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatsonEngine {

    private final WikiPage wikiPage;
    private final IndexHandler indexHandler;

    private final Configuration config;

    public WatsonEngine(Configuration config) {
        this.config = config;
        wikiPage = new WikiPage();
        indexHandler = new IndexHandler(config);
    }

    public void index() {
        File[] files = getFiles();
        if (files == null) {
            return;
        }
        int total = files.length;
        int i = 1;
        System.out.println("Start indexing ...");
        indexHandler.create();
        for (File file : files) {
            System.out.println("Indexing file " + i + "/" + total);
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    parse(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            i ++;
        }
        System.out.println("Indexing complete.");
        indexHandler.closeAll();
    }

    public List<Result> search(String question) {
        return indexHandler.run(question);
    }

    private void parse(String line) {
        Pattern titlePattern = Pattern.compile("\\[\\[(.+?)]]");
        Pattern headerPattern = Pattern.compile("==(.+?)==");

        Matcher titleMatcher = titlePattern.matcher(line);
        Matcher headerMatcher = headerPattern.matcher(line);

        if (titleMatcher.find() && line.startsWith("[[") && line.endsWith("]]")) {
            indexHandler.add(wikiPage);
            wikiPage.reset();
            String title = titleMatcher.group(1);
            wikiPage.setTitle(title);
        } else if (headerMatcher.find() && line.startsWith("==") && line.endsWith("==")) {
            String header = headerMatcher.group(1);
            wikiPage.updateLast(header);
        } else {
            wikiPage.updateLast(line);
        }
    }

    private File[] getFiles() {
        File directory;
        String path = config.isTest() ? config.getTestWikiDirectory() : config.getWikiDirectory();
        ClassLoader classLoader = getClass().getClassLoader();
        directory = new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());

        if (directory.exists() && directory.isDirectory()) {
            return directory.listFiles((dir, name) -> name.startsWith("enwiki") && name.endsWith(".txt"));
        }
        return null;
    }
}
