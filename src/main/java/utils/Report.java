package utils;

import model.Question;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Report {

    public static void writeConfig(Configuration config) {
        String path = config.getReportDirectory() + "/" + config.getReportName() + "/";
        File report = new File(path + "configuration.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(report));
            writer.write(config.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(boolean correct, Question question, String[] predictions, Configuration config) {
        String str;
        if (correct) {
            str = "/correct/";
        } else {
            str = "/wrong/";
        }

        String path = config.getReportDirectory() + "/" + config.getReportName() + str;
        File reportDirectory = new File(path);
        if (!Files.exists(Paths.get(path))) {
            reportDirectory.mkdirs();
        }

        String fileName = question.getCategory() + ".txt";
        File report = new File(path + fileName);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(report, true));
            writer.write(question.getRawQuestion());
            writer.write("\n\n");

            writer.write("Correct Answer(s):\n");
            for (String ans : question.getAnswer()) {
                writer.write(ans + "\n");
            }
            writer.write("\n");

            writer.write("Predicted Answer(s):\n");
            for (String pred : predictions) {
                writer.write(pred + "\n");
            }
            writer.write("\n\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
