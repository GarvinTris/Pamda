package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vocabulary_table")
public class Vocabulary {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String word;
    public String pinyin;
    public String definition;
    public int stage;

    public Vocabulary(String word, String pinyin, String definition, int stage) {
        this.word = word;
        this.pinyin = pinyin;
        this.definition = definition;
        this.stage = stage;
    }

    public String getDisplayHanzi() {
        if (word == null) return "";
        return word.replaceAll("[0-9]", "");
    }
}