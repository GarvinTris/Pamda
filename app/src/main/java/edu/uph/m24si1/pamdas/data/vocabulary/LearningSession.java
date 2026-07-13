package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "database_learningsession",
    foreignKeys = {
        @ForeignKey(entity = Deck.class, parentColumns = "id", childColumns = "deck_id", onDelete = ForeignKey.NO_ACTION),
        @ForeignKey(entity = Vocabulary.class, parentColumns = "id", childColumns = "last_vocabulary_id", onDelete = ForeignKey.NO_ACTION)
    },
    indices = {
        @Index(value = {"deck_id"}, unique = true),
        @Index(value = {"last_vocabulary_id"}, name = "database_learningsession_last_vocabulary_id_1e37c1be")
    }
)
public class LearningSession {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @ColumnInfo(name = "current_stage")
    public int currentStage;
    
    @NonNull
    @ColumnInfo(name = "created_at")
    public String createdAt;
    
    @NonNull
    @ColumnInfo(name = "last_played")
    public String lastPlayed;
    
    @ColumnInfo(name = "deck_id")
    public long deckId;
    
    @ColumnInfo(name = "total_stage")
    public int totalStage;
    
    @ColumnInfo(name = "last_vocabulary_id")
    public Long lastVocabularyId;

    public LearningSession(long id, int currentStage, @NonNull String createdAt, @NonNull String lastPlayed, long deckId, int totalStage, Long lastVocabularyId) {
        this.id = id;
        this.currentStage = currentStage;
        this.createdAt = createdAt;
        this.lastPlayed = lastPlayed;
        this.deckId = deckId;
        this.totalStage = totalStage;
        this.lastVocabularyId = lastVocabularyId;
    }
}
