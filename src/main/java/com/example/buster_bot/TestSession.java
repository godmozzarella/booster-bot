package com.example.buster_bot;

public class TestSession {
    private int questionIndex;
    private int score;

    public TestSession() {
        this.questionIndex = 0;
        this.score = 0;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void nextQuestion() {
        this.questionIndex++;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int value) {
        this.score += value;
    }
}