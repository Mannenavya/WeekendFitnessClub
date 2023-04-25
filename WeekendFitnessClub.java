package org.example;

import java.util.*;

public class WeekendFitnessClub {
    private static final int NUM_DAYS = 16;
    private static final int LESSONS_PER_DAY = 2;
    private static final int MAX_CAPACITY = 5;
    private static String[] DAYS_OF_WEEK = {"Saturday", "Sunday"};
    private static String[] LESSON_TIMES = {"9:00am", "10:30am", "12:00pm", "1:30pm"};
    private static final String[] TYPES_OF_FITNESS = {"SPIN", "YOGA", "BODYSCULPT", "ZUMBA"};
    private static final double[] PRICES = {15.0, 12.0, 10.0, 13.0};
    private static int[][] lessonBookings = new int[NUM_DAYS][LESSONS_PER_DAY * NUM_DAYS];
    private static double[] incomes = new double[TYPES_OF_FITNESS.length];
    private static Review review = new Review(new double[TYPES_OF_FITNESS.length][2]);
    private static final String MENU_SEPARATOR = "---------------------------------------------------";

    private static boolean running = true;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (running) {
            displayMenu();
            int option = getUserOption();

            switch (option) {
                case 1:
                    bookLesson(scanner);
                    break;
                case 2:
                    changeOrCancelBooking(scanner);
                    break;
                case 3:
                    attendLesson(scanner);
                    break;
                case 4:
                    generateMonthlyLessonReport();
                    break;
                case 5:
                    generateMonthlyFitnessTypeReport();
                    break;
                case 0:
                    exit();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("Welcome to the Weekend Fitness Club booking system!");
        System.out.println("Please select an option:");
        System.out.println("1. Book a group fitness lesson");
        System.out.println("2. Change or cancel a booking");
        System.out.println("3. Attend a lesson");
        System.out.println("4. Generate monthly lesson report");
        System.out.println("5. Generate monthly champion fitness type report");
        System.out.println("0. Exit");
    }

    private static int getUserOption() {
        int option = -1;
        while (option < 0 || option > 5) {
            System.out.print("Option: ");
            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid option. Please try again.");
                scanner.nextLine();
            }
        }
        return option;
    }

    private static void exit() {
        running = false;
        System.out.println("Thank you for using the Weekend Fitness Club booking system!");
    }

    private static void bookLesson(Scanner scanner) {
        System.out.print("Please enter your customer ID:");
        int customerId = scanner.nextInt();
        System.out.println(MENU_SEPARATOR);
        System.out.println("Select the way to check the timetable:");
        System.out.println(MENU_SEPARATOR);
        System.out.println("1. By day (Saturday or Sunday)");
        System.out.println("2. By fitness type");

        int option = scanner.nextInt();
        String fitnessTypeChoice = "";
        int[] availableLessons ;
        if (option == 1) {
            int day = 1;
            availableLessons = getAvailableLessonsForDay(day);
        } else if (option == 2) {
            System.out.println("Please enter the fitness type:");
            System.out.println("(Spin,Yoga,BodySculpt,Zumba)");
            fitnessTypeChoice += scanner.next().toUpperCase();
            int day=1;
            availableLessons = getAvailableLessonsForDay(day);
        } else {
            System.out.println("Invalid option. Please try again.");
            return;
        }

        if (availableLessons.length == 0) {
            System.out.println("Sorry, there are no available lessons for your selected option.");
            return;
        }


        System.out.println("Available lessons: ");
        if(option == 1) {
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % TYPES_OF_FITNESS.length;
                String fitnessType = TYPES_OF_FITNESS[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }

        int typeIndex = getFitnessTypeIndex(fitnessTypeChoice);
        if(option==2) {
            for (int i = typeIndex; i < availableLessons.length; i+=TYPES_OF_FITNESS.length) {
                int lessonIndex = availableLessons[i];
                int day = i % 2;
                int lesson = lessonIndex % TYPES_OF_FITNESS.length;
                String fitnessType = TYPES_OF_FITNESS[lesson];

                System.out.println((i + 1) + ". " + fitnessType + " on " + getDayOfWeek(day % 2) +
                        " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);
            }
        }

        System.out.print("Select a lesson :");
        int lessonChoice = scanner.nextInt();

        int lessonIndex = availableLessons[lessonChoice - 1];
        int day = lessonIndex % LESSONS_PER_DAY;
        int lesson = lessonIndex % TYPES_OF_FITNESS.length;
        String fitnessType = TYPES_OF_FITNESS[lesson];


        System.out.println("You have selected " + fitnessType + " on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        if (lessonBookings[day][lessonIndex] >= MAX_CAPACITY) {
            System.out.println("Sorry, the lesson is fully booked.");
            return;
        }

        if(option == 1) {
            lessonBookings[day][lessonIndex]++;
        }
        if(option == 2){
            lessonBookings[day][getFitnessTypeIndex(fitnessTypeChoice)]++;
        }

        System.out.println("Lesson Booking successful! Booking ID is " + generateBookingId(day, lessonIndex, customerId));
    }


    private static void changeOrCancelBooking(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId = scanner.nextInt();
        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % TYPES_OF_FITNESS.length;
        int customerId = bookingDetails[2];

        if(lessonBookings[day][lessonIndex] == 0){
            System.out.println("Booking Id not valid!");
            return;
        }

        String fitnessType = TYPES_OF_FITNESS[lesson];

        System.out.println("You have booked " + fitnessType + " on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson) + " with a price of $" + PRICES[lesson]);

        System.out.println("Please select an option:");
        System.out.println("1. Change booking to another lesson");
        System.out.println("2. Cancel booking");

        int option = scanner.nextInt();

        if (option == 1) {
            int[] availableLessons = getAvailableLessonsForDay(1);

            if (availableLessons.length == 0) {
                System.out.println("Sorry, there are no available lessons for your selected option.");
                return;
            }

            System.out.println("Available lessons: ");
            System.out.println("___________________");
            for (int i = 0; i < availableLessons.length; i++) {
                int lessonIndexA = availableLessons[i];
                int dayA = i % 2;
                int lessonA = lessonIndexA % TYPES_OF_FITNESS.length;
                String fitnessTypeA = TYPES_OF_FITNESS[lessonA];

                System.out.println((i + 1) + ". " + fitnessTypeA + " on " + getDayOfWeek(dayA % 2) +
                        " at " + getTimeOfDay(lessonA) + " with a price of $" + PRICES[lessonA]);
            }

            System.out.println("Please select a new lesson (1-" + availableLessons.length + "):");
            int newLessonChoice = scanner.nextInt();
            int newLessonIndex = availableLessons[newLessonChoice - 1];
            int newDay = newLessonIndex % LESSONS_PER_DAY;
            int newLesson = newLessonIndex % TYPES_OF_FITNESS.length;
            System.out.println("new lesson" + newLessonIndex);

            System.out.println("You have selected " + TYPES_OF_FITNESS[newLesson] + " on " + getDayOfWeek(newDay%2) +
                    " at " + getTimeOfDay(newLesson) + " with a price of $" + PRICES[newLesson]);

            lessonBookings[day][lessonIndex]--;
            lessonBookings[newDay][newLessonIndex]++;
            System.out.println("Booking changed successfully! Your new booking ID is " + generateBookingId(newDay, newLessonIndex, customerId));
        } else if (option == 2) {
            lessonBookings[day][lessonIndex]--;
            int fitnessTypeIndex = getFitnessTypeIndex(fitnessType);
            incomes[fitnessTypeIndex] -= PRICES[lesson];
            System.out.println("Booking cancelled successfully!");
        } else {
            System.out.println("Invalid option. Please try again.");
        }
    }


    private static void attendLesson(Scanner scanner) {
        System.out.println("Please enter your booking ID:");
        int bookingId;
        try {
            bookingId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid booking ID.");
            scanner.next();
            return;
        }

        int[] bookingDetails = parseBookingId(bookingId);
        if (bookingDetails == null) {
            System.out.println("Invalid booking ID. Please try again.");
            return;
        }

        int day = bookingDetails[0];
        int lessonIndex = bookingDetails[1];
        int lesson = lessonIndex % TYPES_OF_FITNESS.length;
        int customerId = bookingDetails[2];

        if (lessonBookings[day][lessonIndex] == 0) {
            System.out.println("Sorry, this lesson is not booked by anyone.");
            return;
        }

        String fitnessType = TYPES_OF_FITNESS[lesson];
        System.out.println("Welcome to the " + fitnessType + " class on " + getDayOfWeek(day%2) +
                " at " + getTimeOfDay(lesson));

        System.out.println("Please rate the lesson (1-5):");
        int rating = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Please provide your feedback:");
        String feedback = scanner.nextLine();
        System.out.println("Thank you for your rating of " + rating + " and feedback: " + feedback);
        double income = PRICES[lesson];
        incomes[lesson] += income;
        System.out.println("Your account will be charged $" + income + " for attending the lesson.");
        System.out.println();

        review.addRating(lesson, rating);
    }


    public static void generateMonthlyLessonReport() {
        int totalBookings = 0;
        double totalIncome = 0;

        System.out.println();
        System.out.println("----------------------");
        System.out.println("Monthly lesson report:");
        System.out.println("----------------------");

        for (int i = 0; i < NUM_DAYS; i++) {
            for (int j = 0; j < LESSONS_PER_DAY * NUM_DAYS; j++) {
                if (lessonBookings[i][j] > 0 && review.getRating(j% TYPES_OF_FITNESS.length) > 0) {
                    int lesson = j % TYPES_OF_FITNESS.length;
                    int fitnessTypeIndex = j % TYPES_OF_FITNESS.length;
                    String fitnessType = TYPES_OF_FITNESS[fitnessTypeIndex];
                    double income = PRICES[fitnessTypeIndex] * lessonBookings[i][j];
                    totalBookings += lessonBookings[i][j];
                    totalIncome += income;
                    System.out.println(fitnessType + " : No of Customers " + lessonBookings[i][j] +
                            " with Avg Rating "+ review.getRating(fitnessTypeIndex));
                }
            }
        }
        System.out.println();
        System.out.println("Total lessonBookings: " + totalBookings);
        System.out.println("Total income: $" + totalIncome);
        System.out.println();
    }

    public static void generateMonthlyFitnessTypeReport() {
        System.out.println();
        System.out.println("-------------------------------------");
        System.out.println("Monthly champion fitness type report:");
        System.out.println("-------------------------------------");
        System.out.println();
        int maxIndex = 0;
        double maxIncome = incomes[0];

        for (int i = 0; i < TYPES_OF_FITNESS.length; i++) {
            if (incomes[i] > maxIncome) {
                maxIndex = i;
                maxIncome = incomes[i];
            }
            System.out.println(TYPES_OF_FITNESS[i] + " : Total income - " + incomes[i] );
        }

        System.out.println();
        if(maxIncome>0) {
            System.out.println("The champion fitness type for this month is " + TYPES_OF_FITNESS[maxIndex] +
                    " with a total income of $" + maxIncome);
        }else {
            System.out.println("No income as of now!");
        }
        System.out.println();
    }

    private static int[] getAvailableLessonsForDay(int day) {
        int[] availableLessons = new int[LESSONS_PER_DAY * NUM_DAYS];
        int index = 0;
        for (int i = 0; i < LESSONS_PER_DAY * NUM_DAYS; i++) {
            if (lessonBookings[day][i] < MAX_CAPACITY) {
                availableLessons[index] = i;
                index++;
            }
        }

        return Arrays.copyOfRange(availableLessons, 0, index);
    }

    //  method to get the index of a fitness type in the TYPES_OF_FITNESS array
    private static int getFitnessTypeIndex(String fitnessType) {
        for (int i = 0; i < TYPES_OF_FITNESS.length; i++) {
            if (TYPES_OF_FITNESS[i].equals(fitnessType)) {
                return i;
            }
        }
        return -1;
    }

    private static int generateBookingId(int day, int lesson, int customerId) {
        return day * 10000 + lesson * 100 + customerId;
    }


    private static int[] parseBookingId(int bookingId) {
        int day = bookingId / 10000;
        int lesson = (bookingId % 10000) / 100;
        int customerId = bookingId % 10;
        if (day < 0 || day >= NUM_DAYS || lesson < 0 || lesson >= (NUM_DAYS * LESSONS_PER_DAY) ||
                customerId <= 0 || customerId > MAX_CAPACITY) {
            System.out.println("Invalid booking ID.");
            return null;
        }

        return new int[] {day, lesson, customerId};
    }

    private static String getDayOfWeek(int day) {
        if (day >= 0 && day < DAYS_OF_WEEK.length) {
            return DAYS_OF_WEEK[day];
        } else {
            return "Invalid day";
        }
    }



    private static String getTimeOfDay(int lesson) {
        if (lesson >= 0 && lesson < LESSON_TIMES.length) {
            return LESSON_TIMES[lesson];
        } else {
            return "Invalid lesson";
        }
    }
}
