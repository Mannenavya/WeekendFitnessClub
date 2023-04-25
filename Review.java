package org.example;

public class Review {
    private static double[][] avgRating;

    public Review(double[][] avgRating) {
        this.avgRating = avgRating;
        for (int i = 0; i < avgRating.length; i++) {
            avgRating[i][1] = 0.0;
            avgRating[i][0] = 0.0;
        }
    }

    public static double[][] getRating() {
        return avgRating;
    }

    public static void setRating(double[][] rating) {
        Review.avgRating = rating;
    }

    public double getRating(int index){
        return avgRating[index][0];
    }
    public void addRating(int index, int rating){
        double totRating = avgRating[index][0] * avgRating[index][1];
        avgRating[index][0] = (totRating+rating) / (avgRating[index][1]+1);
        avgRating[index][1]++;
    }
}
