package com.example.linguatrain.models;

import java.util.List;

public class Lesson {
    private String id;
    private String title;
    private boolean isUnlocked;
    private List<Word> words;

    public Lesson() {}

    public Lesson(String id, String title, boolean isUnlocked, List<Word> words) {
        this.id = id;
        this.title = title;
        this.isUnlocked = isUnlocked;
        this.words = words;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    public List<Word> getWords() { return words; }
    public void setWords(List<Word> words) { this.words = words; }
}