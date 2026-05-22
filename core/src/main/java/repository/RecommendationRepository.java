package repository;

import java.util.List;

public interface RecommendationRepository {
    void index(Integer projectId) throws Exception;

    /**
     * @param issueId
     * @return 추천하는 user id 목록
     */
    List<Integer> recommend(Integer issueId) throws Exception;
}
