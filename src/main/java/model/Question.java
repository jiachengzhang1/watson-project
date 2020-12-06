package model;

import edu.stanford.nlp.simple.Sentence;
import utils.Configuration;

import java.util.LinkedList;
import java.util.List;

public class Question {
    final private Configuration config;
    final private String category;
    final private String[] answers;
    final private String rawQuestion;
    private String question;

    public Question(String category, String question, String answers, Configuration config) {
        this.config = config;
        this.category = trimCategory(category);
        this.question = config.isLemmatize() ? lemmatize(trimQuestion(question)) : trimQuestion(question);
        this.rawQuestion = this.question;
        this.answers = answers.split("\\|");

        process();
    }

    public boolean isCorrect(String answer) {
        boolean isCorrect = checkCorrect(answer);
        if (config.isShowDetails()) {
            String ans = String.join(" | ", answers);
            String result = isCorrect ? "Correct\n" + answer : "Wrong\n" + "Expect: " + ans + "\nActual: " + answer;
            System.out.println(result + "\n");
        }
        return isCorrect;
    }

    public List<RelevanceMap> getRelevanceScales(List<Result> results) {
        List<RelevanceMap> scales = new LinkedList<>();
        for (int i = 0; i<results.size(); i++) {
            int scale = checkCorrect(results.get(i).getTitle()) ? 1 : 0;
            scales.add(new RelevanceMap(i+1, scale));
        }
        return scales;
    }

    private boolean checkCorrect(String answer) {
        for (String a : answers) {
            if (answer.equals(a)) {
                return true;
            }
        }
        return false;
    }

    private String lemmatize(String text) {
        edu.stanford.nlp.simple.Document doc = new edu.stanford.nlp.simple.Document(text.toLowerCase());
        StringBuilder str = new StringBuilder();
        for (Sentence sentence : doc.sentences()) {
            str.append(" ").append(String.join(" ", sentence.lemmas()));
        }
        return str.toString();
    }

    public String getCategory() {
        return category;
    }

    public String[] getAnswer() {
        return answers;
    }

    public String getQuestion() {
        return question;
    }

    public String getRawQuestion() {
        return rawQuestion;
    }

    private void process() {
        if (config.isQueryBoosting()) {
            boosting();
        }
        if (config.isUseCategory()) {
            mixCategory();
        }
    }

    private void mixCategory() {
        final String BEST = config.isLemmatize() ? lemmatizeWord("best") : "best";
        final String BESTSELLING =  config.isLemmatize() ? lemmatizeWord("bestselling") : "bestselling";
        final String TOP = config.isLemmatize() ? lemmatizeWord("top") : "top";
        final String SINGER = config.isLemmatize() ? lemmatizeWord("singer") : "singer";
        final String SONGWRITER = config.isLemmatize() ? lemmatizeWord("songwriter") : "songwriter";
        final String UNIVERSITY = config.isLemmatize() ? lemmatizeWord("university") : "university";
        final String CALIFORNIA = config.isLemmatize() ? lemmatizeWord("california") : "california";
        final String LOS = config.isLemmatize() ? lemmatizeWord("los") : "los";
        final String ANGELES = config.isLemmatize() ? lemmatizeWord("angeles") :"angeles";
        final String WINNER = config.isLemmatize() ? lemmatizeWord("winner") : "winner";
        final String AWARD = config.isLemmatize() ? lemmatizeWord("award") : "award";

        String hitmaker = String.format("\"%s OR %s OR %s \" AND \" %s OR %s \"", BEST, TOP, BESTSELLING, SINGER, SONGWRITER);
        String ucla = String.format("ucla OR \"%s %s %s %s\"~1", UNIVERSITY, CALIFORNIA, LOS, ANGELES);
        String winner = String.format("%s AND %s OR %s", AWARD, BEST, WINNER);

        String processedCategory = category.toLowerCase()
                .replace("hitmaker", hitmaker)
                .replace("ucla", ucla)
                .replace("winner", winner);

        List<String> nouns = getNouns(processedCategory);
        question = question + " " + String.join(" ", nouns);
    }

    private String lemmatizeWord(String word) {
        return new Sentence(word).lemma(0);
    }

    private void boosting() {
        List<String> nouns = getNouns(question);
        if (config.isLemmatize()) {
            for (int i = 0; i<nouns.size(); i++) {
                nouns.set(i, lemmatizeWord(nouns.get(i)));
            }
        }

        question = question + " " + String.join(" ", nouns);
//        System.out.println(question);
    }

    private List<String> getNouns(String text) {
        List<String> nouns = new LinkedList<>();
        Sentence sentence = new Sentence(text);
        for (int i = 0; i<sentence.length(); i++) {
            if(sentence.posTag(i).contains("NN")) {
                nouns.add(sentence.word(i));
            }
        }
        return nouns;
    }

    private String trimQuestion(String str) {
        return str.replace('-', ' ')
                .replace('!', ' ')
                .replace('\"', ' ')
                .replace(';',' ')
                .replace(':', ' ');
    }

    private String trimCategory(String str) {
        return str.replace("\\([^()]*\\)", "")
                .replace("Alex: ", "");
    }
}
