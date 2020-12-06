package models;

public class Result {
    final private String title;
    final private String content;
    final private double score;
    final private String intro;

    private double adjustedScore;

    public Result(String title, String content, String intro, double score) {
        this.title = title;
        this.content = content;
        this.intro = intro;
        this.score = score;
        this.adjustedScore = 0;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public double getScore() {
        return score;
    }

    public String getIntro() {
        return intro;
    }

    public void setAdjustedScore(double score) {
        adjustedScore = score;
    }

    public double getAdjustedScore() {
        return adjustedScore;
    }
}
