package t16_json.exercises.ex03;

import java.util.Optional;

public interface GameAssetDao {
    int insert(GameAsset asset) throws Exception;
    Optional<GameAsset> findById(int id) throws Exception;
    boolean deleteById(int id) throws Exception;
}
