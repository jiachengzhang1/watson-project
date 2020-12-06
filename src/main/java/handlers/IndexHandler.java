package handlers;

import edu.stanford.nlp.simple.Sentence;
import models.Result;
import models.WikiPage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.FSDirectory;
import utils.Configuration;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class IndexHandler {
    final private Configuration config;
    final private Analyzer analyzer;
    private FSDirectory store;
    private IndexWriter indexWriter;

    public IndexHandler(Configuration config) {
        this.config = config;
        if (config.isLemmatize()) {
            analyzer = new WhitespaceAnalyzer();
        } else {
            analyzer = new StandardAnalyzer();
        }

        try {
            String indexPath = config.isLemmatize() ? config.getIndexPath() : config.getRawIndexPath();

            if (config.isLemmatize() && config.isPositional()) {
                indexPath = config.getPositionalIndexPath();
            }

            store = FSDirectory.open(Paths.get(indexPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create() {
        open(OpenMode.CREATE);
    }

    public void open() {
        open(OpenMode.CREATE_OR_APPEND);
    }

    public void close() {
        try {
            store.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAll() {
        try {
            indexWriter.close();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(WikiPage wikiPage) {
        try {
            indexWriter.addDocument(addDoc(wikiPage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Result> run(String queryString) {
        return match(queryString, "content");
    }

    public List<Result> match(String queryString, String fieldName) {
        List<Result> results = new LinkedList<>();
        try {
            Query query = new QueryParser(fieldName, analyzer).parse(queryString);
            int hitsPerPage = 20;
            String indexPath = config.isLemmatize() ? config.getIndexPath() : config.getRawIndexPath();

            if (config.isLemmatize() && config.isPositional()) {
                indexPath = config.getPositionalIndexPath();
            }

            DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher searcher = new IndexSearcher(reader);
            if (! config.isUseBM25()) {
                searcher.setSimilarity(new TFIDFSimilarity() {
                    @Override
                    public float tf(float freq) {
                        return (float) (1 + Math.log10(freq));
                    }

                    @Override
                    public float idf(long docFreq, long numDocs) {
                        return (float) Math.log10((double) numDocs / docFreq);
                    }

                    @Override
                    public float lengthNorm(int sumOfSquaredWeights) {
                        return (float) Math.sqrt(sumOfSquaredWeights);
                    }
                });
            }
            TopDocs topDocs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = topDocs.scoreDocs;

            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document doc = searcher.doc(docId);
                results.add(new Result(doc.get("title"), doc.get("content"), doc.get("intro"), hit.score));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void open(OpenMode mode) {
        IndexWriterConfig config;
        if (this.config.isUseBM25()) {
            config = new IndexWriterConfig(analyzer);
        } else {
            config = new IndexWriterConfig(analyzer).setSimilarity(new TFIDFSimilarity() {
                @Override
                public float tf(float freq) {
                    return (float) (1 + Math.log10(freq));
                }

                @Override
                public float idf(long docFreq, long numDocs) {
                    return (float) Math.log10((double) numDocs / docFreq);
                }

                @Override
                public float lengthNorm(int sumOfSquaredWeights) {
                    return (float) Math.sqrt(sumOfSquaredWeights);
                }
            });
        }
        config.setOpenMode(mode);
        try {
            indexWriter = new IndexWriter(store, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document addDoc(WikiPage wikiPage) {
        Document doc = new Document();

        String title = wikiPage.getTitle();
        String intro = wikiPage.getFirstParagraph();
        String content = wikiPage.getContentString();

        if (config.isLemmatize()) {
            intro = lemmatize(intro);
            content = lemmatize(content);
        }

        doc.add(new StringField("title", title, Field.Store.YES));


        if (config.isPositional()) {
            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            ft.setStoreTermVectors(true);
            ft.setStoreTermVectorPositions(true);
            ft.setStoreTermVectorOffsets(true);

            doc.add(new Field("intro", intro, ft));
            doc.add(new Field("content", content, ft));
        } else {
            doc.add(new TextField("intro", intro, Field.Store.YES));
            doc.add(new TextField("content", content, Field.Store.YES));
        }

        return doc;
    }

    private String lemmatize(String text) {
        edu.stanford.nlp.simple.Document doc = new edu.stanford.nlp.simple.Document(text.toLowerCase());
        StringBuilder str = new StringBuilder();
        for (Sentence sentence : doc.sentences()) {
            str.append(" ").append(String.join(" ", sentence.lemmas()));
        }
        return str.toString();
    }
}
