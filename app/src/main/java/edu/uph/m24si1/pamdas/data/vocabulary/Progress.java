package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "database_progress",
    foreignKeys = {
        @ForeignKey(entity = LearningSession.class, parentColumns = "id", childColumns = "session_id", onDelete = ForeignKey.NO_ACTION),
        @ForeignKey(entity = Vocabulary.class, parentColumns = "id", childColumns = "vocabulary_id", onDelete = ForeignKey.NO_ACTION)
    },
    indices = {
        @Index(value = {"session_id", "vocabulary_id"}, unique = true, name = "database_progress_session_id_vocabulary_id_c22f7d75_uniq"),
        @Index(value = {"session_id"}, name = "database_progress_session_id_2039a80d"),
        @Index(value = {"vocabulary_id"}, name = "database_progress_vocabulary_id_624e6a32")
    }
)
public class Progress {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public int mastery;
    
    @ColumnInfo(name = "first_try_correct")
    public boolean firstTryCorrect;
    
    @ColumnInfo(name = "review_count")
    public int reviewCount;
    
    @NonNull
    @ColumnInfo(name = "last_review")
    public String lastReview;
    
    @ColumnInfo(name = "session_id")
    public long sessionId;
    
    @ColumnInfo(name = "vocabulary_id")
    public long vocabularyId;

    public Progress(long id, int mastery, boolean firstTryCorrect, int reviewCount, @NonNull String lastReview, long sessionId, long vocabularyId) {
        this.id = id;
        this.mastery = mastery;
        this.firstTryCorrect = firstTryCorrect;
        this.reviewCount = reviewCount;
        this.lastReview = lastReview;
        this.sessionId = sessionId;
        this.vocabularyId = vocabularyId;
    }
}
