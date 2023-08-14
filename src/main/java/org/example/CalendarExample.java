package org.example;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class CalendarExample {
    public enum EventTag {
        WORK,
        PERSONAL,
        MEETING,
        CALL,
        TODO,
        HEALTH,
        OTHER
    }

    private static Map<LocalDate, String> events = new HashMap<>();
    private static Map<LocalDateTime, String> reminders = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();

        while (true) {
            printCalendar(currentYear, currentMonth);

            System.out.println("\n\nOptions:");
            System.out.println("1. Next Month");
            System.out.println("2. Previous Month");
            System.out.println("3. Next Year");
            System.out.println("4. Previous Year");
            System.out.println("5. Add Event");
            System.out.println("6. Show Events");
            System.out.println("7. Set Reminder");
            System.out.println("8. Receive Notifications");
            System.out.println("9. View Current Date");
            System.out.println("10. Quit\n");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            if (choice == 1) {
                currentMonth++;
                if (currentMonth > 12) {
                    currentMonth = 1;
                    currentYear++;
                }
            } else if (choice == 2) {
                currentMonth--;
                if (currentMonth < 1) {
                    currentMonth = 12;
                    currentYear--;
                }
            } else if (choice == 3) {
                currentYear++;
            } else if (choice == 4) {
                currentYear--;
            } else if (choice == 5) {
                addEventWithConflictCheck();
            } else if (choice == 6) {
                showEvents();
            } else if (choice == 7) {
                setReminder();
            } else if (choice == 8) {
                receiveNotifications();
            } else if (choice == 9) {
                viewCurrentDate(currentDate);
            } else if (choice == 10) {
                break;
            }
            currentDate = LocalDate.of(currentYear, currentMonth, 1);
        }

        System.out.println("Exiting CalendarExample.");
    }

    public static void showEvents() {
        if (events.isEmpty()) {
            System.out.println("\nNo events\n");
        } else {
            for (LocalDate key : events.keySet()) {
                System.out.println("Event date: " + key);
                System.out.println("Event description: " + events.get(key));
            }
        }
    }

    public static void viewCurrentDate(LocalDate currentDate) {
        System.out.println("Current Date:");
        System.out.println("Day: " + currentDate.getDayOfMonth());
        System.out.println("Month: " + currentDate.getMonth());
        System.out.println("Year: " + currentDate.getYear());
    }

    public static void printCalendar(int year, int month) {
        System.out.println("\n " + Month.of(month) + " " + year);
        System.out.println(" Su Mo Tu We Th Fr Sa");

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i < dayOfWeek; i++) {
            System.out.print("   ");
        }

        int daysInMonth = firstDayOfMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            System.out.printf("%3d", day);

            if (firstDayOfMonth.plusDays(day - 1).getDayOfWeek().getValue() == 7) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public static void addEventWithConflictCheck() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Event Name: ");
        String name = scanner.nextLine();

        System.out.print("Date (yyyy-MM-dd): ");
        String dateString = scanner.nextLine();
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Check for existing events on the selected date
        if (events.containsKey(date)) {
            System.out.println("There is already an event on this date: " + events.get(date));
            return;
        }

        System.out.print("Location: ");
        String location = scanner.nextLine();

        // Check for available time slots
        List<LocalTime> availableTimes = findAvailableTimeSlots(date);

        if (availableTimes.isEmpty()) {
            System.out.println("No available time slots on this date.");
            return;
        }

        System.out.println("Available time slots on " + date + ":");
        for (LocalTime time : availableTimes) {
            System.out.println(time);
        }

        System.out.print("Choose an available time (HH:mm): ");
        String timeString = scanner.nextLine();
        LocalTime eventTime = LocalTime.parse(timeString);

        LocalDateTime eventDateTime = LocalDateTime.of(date, eventTime);

        System.out.print("Notes: ");
        String notes = scanner.nextLine();

        System.out.print("Category (WORK, PERSONAL, MEETING, CALL, TODO, HEALTH, OTHER): ");
        String categoryString = scanner.nextLine();
        EventTag category = EventTag.valueOf(categoryString);

        events.put(date, name + " - " + location + " (" + category + ")");
        reminders.put(eventDateTime, "Don't forget: " + name);

        System.out.println("Event added successfully!\n");
    }

    public static List<LocalTime> findAvailableTimeSlots(LocalDate date) {
        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0); // Example: Start from 9 AM
        LocalTime endTime = LocalTime.of(18, 0);  // Example: End at 6 PM

        // Adjust based on your work hours

        while (startTime.isBefore(endTime)) {
            LocalDateTime potentialSlot = LocalDateTime.of(date, startTime);
            boolean slotAvailable = true;

            for (LocalDateTime existingEvent : reminders.keySet()) {
                if (Math.abs(ChronoUnit.MINUTES.between(potentialSlot, existingEvent)) < 30) {
                    slotAvailable = false;
                    break;
                }
            }

            if (slotAvailable) {
                availableTimes.add(startTime);
            }

            startTime = startTime.plusMinutes(30); // Increment by 30 minutes
        }

        return availableTimes;
    }

    public static void setReminder() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter event date (yyyy-MM-dd): ");
        String dateString = scanner.next();

        LocalDate eventDate = LocalDate.parse(dateString);

        System.out.print("Enter event time (HH:mm): ");
        String timeString = scanner.next();

        LocalTime eventTime = LocalTime.parse(timeString);

        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

        System.out.print("Enter reminder description: ");
        scanner.nextLine(); // Consume the newline
        String reminderDescription = scanner.nextLine();

        reminders.put(eventDateTime, reminderDescription);
        System.out.println("Reminder set for " + eventDateTime);
    }

    public static void receiveNotifications() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (LocalDateTime reminderDateTime : reminders.keySet()) {
            long minutesUntilReminder = ChronoUnit.MINUTES.between(currentDateTime, reminderDateTime);

            if (minutesUntilReminder > 0 && minutesUntilReminder <= 30) {
                String reminderDescription = reminders.get(reminderDateTime);

                System.out.println("Notification: " + reminderDescription);
                System.out.println("Reminder for " + reminderDateTime);
                System.out.println("Event is in " + minutesUntilReminder + " minutes.");
                System.out.println();
            }
        }
    }
}
