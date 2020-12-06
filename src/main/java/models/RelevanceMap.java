package models;

public class RelevanceMap {
    private final int docOrderIndex;
    private final int relevanceScale;

    public RelevanceMap(int docOrderIndex, int relevanceScale) {
        this.docOrderIndex = docOrderIndex;
        this.relevanceScale = relevanceScale;
    }

    public int getDocOrderIndex() {
        return docOrderIndex;
    }

    public int getRelevanceScale() {
        return relevanceScale;
    }
}
