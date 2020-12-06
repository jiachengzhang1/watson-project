package utils;

import models.Question;
import models.RelevanceMap;
import models.Result;

import java.util.*;

public class Performance {
    public static double reciprocalRank(List<Result> results, Question question) {
        List<RelevanceMap> scales = question.getRelevanceScales(results);
        for (RelevanceMap scale : scales) {
            if (scale.getRelevanceScale() == 1) {
                return (double) 1/scale.getDocOrderIndex();
            }
        }
        return 0;
    }

    public static double meanReciprocalRank(List<Double> ranks) {
        double sum = 0;
        for (double rank : ranks) {
            sum += rank;
        }
        return sum / ranks.size();
    }
}
