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
    @Query("SELECT * FROM vocabulary")
    LiveData<List<Vocabulary>> getAllVocabulary();

    @Query("SELECT * FROM vocabulary WHERE hskLevel = :hsk AND stage = :stage")
    List<Vocabulary> getVocabularyByHskAndStage(int hsk, int stage);

    @Query("SELECT COUNT(*) FROM vocabulary WHERE hskLevel = :hsk")
    int getVocabularyCountForHsk(int hsk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Vocabulary vocabulary);

    @Query("SELECT * FROM progress_table WHERE vocabularyId = :vocabId")
    Progress getProgressForVocabulary(int vocabId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(Progress progress);

    @Update
    void updateProgress(Progress progress);

    @Query("SELECT AVG(mastery) FROM progress_table p JOIN vocabulary v ON p.vocabularyId = v.id WHERE v.stage = :stage")
    int getAverageMasteryForStage(int stage);

    @Query("SELECT COUNT(*) FROM progress_table p JOIN vocabulary v ON p.vocabularyId = v.id WHERE v.stage = :stage AND p.mastery >= 100")
    int getCompletedCountForStage(int stage);

    @Query("SELECT * FROM user_stats WHERE id = 1")
    UserStats getUserStats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserStats(UserStats stats);

    @Update
    void updateUserStats(UserStats stats);
}