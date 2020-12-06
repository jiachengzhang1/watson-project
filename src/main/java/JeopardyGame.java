import handlers.QuestionHandler;
import handlers.RerankHandler;
import models.Question;
import models.Result;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;
import utils.Configuration;
import utils.Configure;
import utils.Performance;
import utils.Report;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class JeopardyGame {

    final private List<Question> questions;
    final private WatsonEngine watson;

    final private Configuration config;

    public JeopardyGame(Configuration config) {
        this.config = config;
        questions = new QuestionHandler().createQuestions(config);
        watson = new WatsonEngine(config);
        index();
    }

    private void index() {
        try {
            String indexPath = config.isLemmatize() ? config.getIndexPath() : config.getRawIndexPath();

            if (config.isLemmatize() && config.isPositional()) {
                indexPath = config.getPositionalIndexPath();
            }

            System.out.println(indexPath);

            if (! DirectoryReader.indexExists(FSDirectory.open(Paths.get(indexPath)))) {
                System.out.println("No index on local storage found.");
                if (config.production) {
                    System.exit(1);
                }
                watson.index();
            } else {
                System.out.println("Index found, using local index.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (config.isShowDetails()) {
            System.out.println("---\nResults:");
        }

        int correctNum = 0;
        boolean writeReport = config.isWriteReport();
        List<Double> ranks = new LinkedList<>();
        for (Question question : questions) {
            String query = question.getQuestion();
            List<Result> rawCandidates = watson.search(query);

            List<Result> candidates = rawCandidates;
            if (config.isRerank()) {
                RerankHandler rerankHandler = new RerankHandler(question.getRawQuestion(), question.getCategory(), rawCandidates);
                candidates = rerankHandler.rerank();
            }

            String best = "NO MATCHES FOUND";
            if (candidates.size() > 0) {
                best = candidates.get(0).getTitle();
            }
            if (question.isCorrect(best)) {
                if (writeReport) {
                    Report.write(true, question, new String[] {best}, config);
                }
                correctNum ++;
            } else {

                String[] bests = new String[Math.min(candidates.size(), 10)];
                for (int i = 0; i<bests.length; i++) {
                    bests[i] = candidates.get(i).getTitle();
                }
                if (writeReport) {
                    Report.write(false, question, bests, config);
                }
            }
            ranks.add(Performance.reciprocalRank(candidates, question));
        }
        printPerformance((double) correctNum / (double) questions.size(), ranks);

        if (writeReport) {
            Report.writeConfig(config);
        }
    }

    private void printPerformance(double accuracy, List<Double> ranks) {
        System.out.println("---\nSystem performance:");
        System.out.println("MRR: " +  Performance.meanReciprocalRank(ranks));
        System.out.println("Prediction Accuracy: " + accuracy);
    }

    public static void main(String[] args) {
        Configuration config = Configure.configure(args);
        JeopardyGame jeopardyGame = new JeopardyGame(config);
        System.out.println("\n---\nSystem configurations: \n" + config.toString());
        jeopardyGame.play();
    }
}
