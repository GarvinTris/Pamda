package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
    entities = {
        Deck.class, 
        Vocabulary.class, 
        Progress.class, 
        UserStats.class, 
        LearningSession.class, 
        StageAssignment.class
    }, 
    version = 1, 
    exportSchema = false
)
public abstract class VocabularyDatabase extends RoomDatabase {
    public abstract VocabularyDao vocabularyDao();
}
