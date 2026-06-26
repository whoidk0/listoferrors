package com.example.linguatrain.models;

public class Word {
    private String id;
    private String enWord;
    private String ruTranslate;

    public Word() {}

    public Word(String id, String enWord, String ruTranslate) {
        this.id = id;
        this.enWord = enWord;
        this.ruTranslate = ruTranslate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEnWord() { return enWord; }
    public void setEnWord(String enWord) { this.enWord = enWord; }
    public String getRuTranslate() { return ruTranslate; }
    public void setRuTranslate(String ruTranslate) { this.ruTranslate = ruTranslate; }
}