package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vocabulary") // Matching user snippet
public class Vocabulary {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String hanzi;
    public String pinyin;
    public String meaning;
    public int hskLevel;
    public int stage;

    public Vocabulary(String hanzi, String pinyin, String meaning, int hskLevel, int stage) {
        this.hanzi = hanzi;
        this.pinyin = pinyin;
        this.meaning = meaning;
        this.hskLevel = hskLevel;
        this.stage = stage;
    }

    public String getDisplayHanzi() {
        if (hanzi == null) return "";
        return hanzi.replaceAll("[0-9]", "");
    }
}