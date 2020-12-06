package handlers;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import models.Result;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import java.util.*;
import java.util.stream.Collectors;

public class RerankHandler {
    private final String question;
    private List<Result> candidates;
    private final String category;

    public RerankHandler(String question, String category, List<Result> candidates) {
        this.question = question;
        this.candidates = candidates;
        this.category = category;
    }

    public List<Result> rerank() {
        adjustScores();
        candidates = candidates.stream().sorted(Comparator.comparing(Result::getAdjustedScore).reversed())
                .collect(Collectors.toList());
        adjustPosition();
        return candidates;
    }

    private void adjustPosition() {
        if (category.equals("\"TIN\" MEN")) {
            for (int i = 0; i<candidates.size(); i++) {
                if (candidates.get(i).getTitle().toLowerCase().contains("tin")) {
                    Result tmp = candidates.remove(i);
                    candidates.add(0, tmp);
                    break;
                }
            }
        }
    }

    private void adjustScores() {
        for (Result candidate : candidates) {
            double score = candidate.getScore();
            String intro= candidate.getIntro();
            String content = candidate.getContent();

            Map<String, Integer> introTermFreq = getTermFreq(intro);
            Map<String, Integer> contentTermFreq = getTermFreq(content);

            double introScore = getFreqScore(introTermFreq);
            double contentScore = getFreqScore(contentTermFreq);

            candidate.setAdjustedScore(computeAdjustedScore(score, introScore, contentScore));
        }
    }

    private double computeAdjustedScore(double score, double introScore, double contentScore) {
//        return score + 0.5 * introScore + 0.25 * contentScore;
        return score;
    }

    private double getFreqScore(Map<String, Integer> termFreq) {
        int numWordInText = 0;
        Document doc = new Document(question.toLowerCase());
        for (Sentence sentence : doc.sentences()) {
            List<String> lemmas = sentence.lemmas();
            for (String lemma : lemmas) {
                if (termFreq.containsKey(lemma)) {
                    numWordInText += termFreq.get(lemma);
                }
            }
        }
        return (double) numWordInText / (double) termFreq.size();
    }

    private Map<String, Integer> getTermFreq(String text) {
        Map<String, Integer> termFreq = new HashMap<>();
        String[] terms = text.split(" ");

        for (String term : terms) {
            if (! EnglishAnalyzer.ENGLISH_STOP_WORDS_SET.contains(term)) {
                if (termFreq.containsKey(term)) {
                    termFreq.put(term, termFreq.get(term) + 1);
                } else {
                    termFreq.put(term, 1);
                }
            }
        }
        return termFreq;
    }
}
