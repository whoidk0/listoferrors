package com.example.linguatrain.models;

public class Test {
    private String id;
    private String title;
    private int lessonId;
    private boolean isUnlocked;
    private String question;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;

    public Test() {}

    public Test(String id, String title, int lessonId, boolean isUnlocked) {
        this.id = id;
        this.title = title;
        this.lessonId = lessonId;
        this.isUnlocked = isUnlocked;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getWrongAnswer1() { return wrongAnswer1; }
    public void setWrongAnswer1(String wrongAnswer1) { this.wrongAnswer1 = wrongAnswer1; }

    public String getWrongAnswer2() { return wrongAnswer2; }
    public void setWrongAnswer2(String wrongAnswer2) { this.wrongAnswer2 = wrongAnswer2; }

    public String getWrongAnswer3() { return wrongAnswer3; }
    public void setWrongAnswer3(String wrongAnswer3) { this.wrongAnswer3 = wrongAnswer3; }
}