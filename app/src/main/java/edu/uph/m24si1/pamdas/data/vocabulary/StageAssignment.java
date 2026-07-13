package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "database_stageassignment",
    foreignKeys = {
        @ForeignKey(entity = LearningSession.class, parentColumns = "id", childColumns = "session_id", onDelete = ForeignKey.NO_ACTION),
        @ForeignKey(entity = Vocabulary.class, parentColumns = "id", childColumns = "vocabulary_id", onDelete = ForeignKey.NO_ACTION)
    },
    indices = {
        @Index(value = {"session_id", "vocabulary_id"}, unique = true, name = "database_stageassignment_session_id_vocabulary_id_6e9572a5_uniq"),
        @Index(value = {"session_id"}, name = "database_stageassignment_session_id_b893f75b"),
        @Index(value = {"vocabulary_id"}, name = "database_stageassignment_vocabulary_id_4652f926"),
        @Index(value = {"session_id", "stage"}, name = "database_st_session_740e04_idx")
    }
)
public class StageAssignment {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public int stage;
    
    @ColumnInfo(name = "session_id")
    public long sessionId;
    
    @ColumnInfo(name = "vocabulary_id")
    public long vocabularyId;

    public StageAssignment(long id, int stage, long sessionId, long vocabularyId) {
        this.id = id;
        this.stage = stage;
        this.sessionId = sessionId;
        this.vocabularyId = vocabularyId;
    }
}
