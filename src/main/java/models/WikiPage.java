package models;

import java.util.LinkedList;
import java.util.List;

public class WikiPage {

    private String title;
    private List<String> content;

    public WikiPage() {
        reset();
    }

    public void reset() {
        title = "";
        content = new LinkedList<>();
        content.add("");
    }

    public void updateLast(String line) {
        String last = content.get(content.size()-1) + trim(line);
        content.set(content.size()-1, last);
    }

    public void setTitle(String line) {
        title = line;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getContent() {
        return content;
    }

    public String getContentString() {
        return String.join(" ", content);
    }

    public String getFirstParagraph() {
        return content.get(0);
    }

    private String trim(String str) {
        if (str.startsWith("CATEGORIES: ")) {
            str = str.replaceFirst("CATEGORIES: ", "");
        }
        return str.replaceAll("\\[tpl](.*)\\[/tpl]", " ")
                .replaceAll("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)", " ")
                .replaceAll("\\[ref]", " ")
                .replaceAll("[^a-zA-Z0-9\\s+]", " ");
    }
}
