package repository.lucene;

import domain.Issue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import repository.IssueFilter;
import repository.IssueRepository;
import repository.RecommendationRepository;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class LuceneRecommendationRepository implements RecommendationRepository {
    @NonNull
    private IssueRepository issueRepository;

    private final Directory directory = new ByteBuffersDirectory();
    private final Analyzer analyzer = new StandardAnalyzer();

    @Override
    public void index(Integer projectId) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        try (IndexWriter writer = new IndexWriter(directory, config)) {
            List<Issue> issues = issueRepository.search(
                    new IssueFilter(projectId, null, null, null, null, null, null)
            );
            for (Issue issue : issues) {
                if (issue.getFixerId() == null) continue;
                Document doc = new Document();
                String text = nullValue(issue.getTitle()) + " " + nullValue(issue.getDescription());
                doc.add(new TextField("content", text, Field.Store.NO));
                doc.add(new StoredField("fixerId", issue.getFixerId()));
                writer.addDocument(doc);
            }
        }
    }

    @Override
    public List<Integer> recommend(Integer issueId) throws Exception {
        Issue issue = issueRepository.load(issueId);
        String text = nullValue(issue.getTitle()) + " " + nullValue(issue.getDescription());

        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            MoreLikeThis mlt = new MoreLikeThis(reader);
            mlt.setAnalyzer(analyzer);
            mlt.setMinTermFreq(1);
            mlt.setMinDocFreq(1);
            mlt.setFieldNames(new String[]{"content"});

            Query query = mlt.like("content", new StringReader(text));
            TopDocs topDocs = searcher.search(query, 10);

            Set<Integer> result = new LinkedHashSet<>();
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document doc = searcher.storedFields().document(sd.doc);
                result.add(doc.getField("fixerId").numericValue().intValue());
            }
            return new ArrayList<>(result);
        }
    }

    private String nullValue(String s) {
        return s != null ? s : "";
    }
}
