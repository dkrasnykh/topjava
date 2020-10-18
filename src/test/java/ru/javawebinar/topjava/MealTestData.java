package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = START_SEQ + 2;
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.OCTOBER, 16);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.OCTOBER, 16);

    public static final Meal meal151000 = new Meal(MEAL_ID, LocalDateTime.of(2020, Month.OCTOBER, 15, 10, 0), "breakfast", 500);
    public static final Meal meal151300 = new Meal(MEAL_ID + 1, LocalDateTime.of(2020, Month.OCTOBER, 15, 13, 0), "lunch", 1000);
    public static final Meal meal152000 = new Meal(MEAL_ID + 2, LocalDateTime.of(2020, Month.OCTOBER, 15, 20, 0), "dinner", 500);
    public static final Meal meal160000 = new Meal(MEAL_ID + 3, LocalDateTime.of(2020, Month.OCTOBER, 16, 00, 0), "night food", 100);
    public static final Meal meal161000 = new Meal(MEAL_ID + 4, LocalDateTime.of(2020, Month.OCTOBER, 16, 10, 0), "breakfast", 500);
    public static final Meal meal161300 = new Meal(MEAL_ID + 5, LocalDateTime.of(2020, Month.OCTOBER, 16, 13, 0), "lunch", 1000);
    public static final Meal meal162000 = new Meal(MEAL_ID + 6, LocalDateTime.of(2020, Month.OCTOBER, 16, 20, 0), "dinner", 500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.now(), "newDescription", 500);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal151000);
        updated.setDescription("UpdatedDescription");
        updated.setDateTime(LocalDateTime.now());
        updated.setCalories(600);
        return updated;
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertEquals(actual, Arrays.asList(expected));
    }
}
