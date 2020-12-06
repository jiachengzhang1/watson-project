package utils;

public class Configuration {
    private boolean test;
    private String testWikiDirectory;
    private String testQuestionFile;

    private boolean positional;
    private boolean lemmatize;
    private boolean queryBoosting;
    private boolean useBM25;
    private boolean writeReport;
    private boolean useCategory;
    private boolean rerank;
    private boolean showDetails;

    private String indexPath;
    private String rawIndexPath;
    private String positionalIndexPath;
    private String wikiDirectory;
    private String questionFile;
    private String reportDirectory;
    private String reportName;

    public final boolean production = true;

    // default configuration
    public Configuration() {
        test = false;
        testWikiDirectory = "wiki-test";
        testQuestionFile = "wiki-test";
        positional = false;
        lemmatize = false;
        queryBoosting = false;
        useBM25 = true;
        writeReport = false;
        useCategory = false;
        rerank = false;
        showDetails = false;
        indexPath = "index";
        rawIndexPath = "index_raw";
        positionalIndexPath = "index_positional";
        wikiDirectory = "wiki-data";
        questionFile = "questions.txt";
        reportDirectory = "report";
    }

    @Override
    public String toString() {
        return "lemmatize: " + lemmatize + "\n" +
                "queryBoosting: " + queryBoosting + "\n" +
                "useBM25: " + useBM25 + "\n" +
                "useCategory: " + useCategory + "\n" +
                "writeReport: " + writeReport + "\n" +
                "showPredictionDetail: " + showDetails + "\n";
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getPositionalIndexPath() {
        return positionalIndexPath;
    }

    public void setPositionalIndexPath(String positionalIndexPath) {
        this.positionalIndexPath = positionalIndexPath;
    }

    public boolean isPositional() {
        return positional;
    }

    public void setPositional(boolean positional) {
        this.positional = positional;
    }

    public void setRerank(boolean rerank) {
        this.rerank = rerank;
    }

    public String getRawIndexPath() {
        return rawIndexPath;
    }

    public void setRawIndexPath(String rawIndexPath) {
        this.rawIndexPath = rawIndexPath;
    }

    public void setTestWikiDirectory(String testWikiDirectory) {
        this.testWikiDirectory = testWikiDirectory;
    }

    public void setTestQuestionFile(String testQuestionFile) {
        this.testQuestionFile = testQuestionFile;
    }

    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    public void setWikiDirectory(String wikiDirectory) {
        this.wikiDirectory = wikiDirectory;
    }

    public void setQuestionFile(String questionFile) {
        this.questionFile = questionFile;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public void setLemmatize(boolean lemmatize) {
        this.lemmatize = lemmatize;
    }

    public void setQueryBoosting(boolean queryBoosting) {
        this.queryBoosting = queryBoosting;
    }

    public void setUseBM25(boolean useBM25) {
        this.useBM25 = useBM25;
    }

    public void setWriteReport(boolean writeReport) {
        this.writeReport = writeReport;
    }

    public void setUseCategory(boolean useCategory) {
        this.useCategory = useCategory;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isTest() {
        return test;
    }

    public String getTestWikiDirectory() {
        return testWikiDirectory;
    }

    public String getTestQuestionFile() {
        return testQuestionFile;
    }

    public boolean isLemmatize() {
        return lemmatize;
    }

    public boolean isQueryBoosting() {
        return queryBoosting;
    }

    public boolean isUseBM25() {
        return useBM25;
    }

    public boolean isWriteReport() {
        return writeReport;
    }

    public boolean isUseCategory() {
        return useCategory;
    }

    public String getIndexPath() {
        return indexPath;
    }

    public String getWikiDirectory() {
        return wikiDirectory;
    }

    public String getQuestionFile() {
        return questionFile;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public boolean isRerank() {
        return rerank;
    }
}
