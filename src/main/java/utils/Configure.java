package utils;

import org.apache.commons.cli.*;

public class Configure {

    private static final String LEMMATIZED = "L";
    private static final String USE_CATEGORY = "C";
    private static final String USE_QUERY_BOOSTING = "Q";
    private static final String USE_TFITF = "T";
    private static final String REPORT = "r";
    private static final String SHOW = "s";

    private static final String BEST = "b";

    public static Configuration configure(String[] args) {
        Configuration config = new Configuration();
        CommandLine cmd = parse(args);

        if (cmd.hasOption(REPORT)) {
            config.setWriteReport(true);
            config.setReportName(cmd.getOptionValue(REPORT));
        }

        config.setShowDetails(cmd.hasOption(SHOW));

        if (cmd.hasOption(BEST)) {
            config.setLemmatize(true);
            useCategory(config);
        } else {
            config.setLemmatize(cmd.hasOption(LEMMATIZED));
            config.setQueryBoosting(cmd.hasOption(USE_QUERY_BOOSTING));

            if (cmd.hasOption(USE_CATEGORY)) {
                useCategory(config);
            }

            if (cmd.hasOption(USE_TFITF)) {
                config.setUseBM25(false);
            }
        }
        return config;
    }

    private static void useCategory(Configuration config) {
        config.setUseCategory(true);
        config.setRerank(true);
    }

    private static CommandLine parse(String[] args) {
        Options options = new Options();

        options.addOption(LEMMATIZED, false, "use lemmatized index");
        options.addOption(USE_CATEGORY, false, "use clue category");
        options.addOption(USE_QUERY_BOOSTING, false, "use query boosting");
        options.addOption(USE_TFITF, false, "use TFITF Similarity Scoring");
        options.addOption(SHOW, false, "show prediction details");
        options.addOption(new Option(REPORT, true, "generate report using <arg> as the name"));

        options.addOption(BEST, false, "use the best configuration");

        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return null;
    }
}
