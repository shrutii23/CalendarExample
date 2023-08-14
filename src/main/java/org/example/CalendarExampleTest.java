package org.example;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CalendarExampleTest {

    @Test
    void testAddEventWithConflictCheck() {
        // Set up initial conditions
        LocalDate date = LocalDate.of(2023, 8, 15); // Example date
        String name = "Test Event";
        String location = "Test Location";
        String categoryString = "WORK";
        CalendarExample.EventTag category = CalendarExample.EventTag.valueOf(categoryString);

        // Create a map to simulate the events map
        Map<LocalDate, String> events = new HashMap<>();

        // Call the modified method with the simulated events map
        CalendarExample.addEventWithConflictCheck();

        // Now you can perform assertions to check if the event was added correctly
        assertTrue(events.containsKey(date));
    }
}
