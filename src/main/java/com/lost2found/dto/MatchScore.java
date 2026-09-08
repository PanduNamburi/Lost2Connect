package com.lost2found.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Breakdown of similarity scores for a potential match between a Lost Item and a Found Item.
 */
public class MatchScore {

    private double totalScore;
    private double categoryScore;
    private double textSimilarityScore;
    private double locationScore;
    private double dateScore;

    @JsonProperty("isMatch")
    private boolean isMatch;

    public MatchScore() {
    }

    public MatchScore(double totalScore, double categoryScore, double textSimilarityScore, double locationScore, double dateScore, boolean isMatch) {
        this.totalScore = Math.round(totalScore * 100.0) / 100.0;
        this.categoryScore = Math.round(categoryScore * 100.0) / 100.0;
        this.textSimilarityScore = Math.round(textSimilarityScore * 100.0) / 100.0;
        this.locationScore = Math.round(locationScore * 100.0) / 100.0;
        this.dateScore = Math.round(dateScore * 100.0) / 100.0;
        this.isMatch = isMatch;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public double getCategoryScore() {
        return categoryScore;
    }

    public void setCategoryScore(double categoryScore) {
        this.categoryScore = categoryScore;
    }

    public double getTextSimilarityScore() {
        return textSimilarityScore;
    }

    public void setTextSimilarityScore(double textSimilarityScore) {
        this.textSimilarityScore = textSimilarityScore;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = locationScore;
    }

    public double getDateScore() {
        return dateScore;
    }

    public void setDateScore(double dateScore) {
        this.dateScore = dateScore;
    }

    @JsonProperty("isMatch")
    public boolean isMatch() {
        return isMatch;
    }

    public void setMatch(boolean match) {
        isMatch = match;
    }
}
