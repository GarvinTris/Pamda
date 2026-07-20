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

    private static volatile VocabularyDatabase INSTANCE;

    public static VocabularyDatabase getDatabase(final android.content.Context context) {
        if (INSTANCE == null) {
            synchronized (VocabularyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = androidx.room.Room.databaseBuilder(context.getApplicationContext(),
                            VocabularyDatabase.class, "pamda_db")
                            .createFromAsset("databases/pamda_db.sqlite3")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
