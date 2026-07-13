package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface VocabularyDao {
    @Query("SELECT * FROM database_vocabulary")
    LiveData<List<Vocabulary>> getAllVocabulary();

    @Query("SELECT * FROM database_vocabulary WHERE deck_id = :deckId")
    List<Vocabulary> getVocabularyByDeck(long deckId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vocabulary vocabulary);

    @Query("SELECT * FROM database_progress WHERE vocabulary_id = :vocabId AND session_id = :sessionId")
    Progress getProgressForVocabularyAndSession(long vocabId, long sessionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(Progress progress);

    @Update
    void updateProgress(Progress progress);

    @Query("SELECT * FROM database_learningsession WHERE deck_id = :deckId LIMIT 1")
    LearningSession getSessionForDeck(long deckId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSession(LearningSession session);

    @Query("SELECT * FROM user_stats WHERE id = 1")
    UserStats getUserStats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserStats(UserStats stats);

    @Update
    void updateUserStats(UserStats stats);
}
