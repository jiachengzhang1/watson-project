import model.Question;
import utils.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class QuestionHandler {

    public List<Question> createQuestions(Configuration config) {
        List<Question> questions = new LinkedList<>();
        File file;
        if (!config.production) {
            String path = config.isTest() ? config.getTestQuestionFile() : config.getQuestionFile();
            ClassLoader classLoader = getClass().getClassLoader();
            file = new File(Objects.requireNonNull(classLoader.getResource(path)).getFile());
        } else {
            file = new File(config.getQuestionFile());
        }


        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            int i = 0;
            String category = "", question = "", answers = "";
            while (line != null) {
                if (i == 0) {
                    category = line;
                }
                if (i == 1) {
                    question = line;
                }
                if (i == 2) {
                    answers = line;
                }
                if (i == 3) {
                    questions.add(new Question(category, question, answers, config));
                    i = -1;
                }
                i ++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Total number of questions: " + questions.size());
        return questions;
    }
}
