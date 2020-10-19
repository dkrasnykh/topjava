package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_MEAL_ID = START_SEQ + 2;
    public static final int ADMIN_MEAL_ID = USER_MEAL_ID + 7;
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.OCTOBER, 16);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.OCTOBER, 16);

    public static final Meal USER_MEAL_151000 = new Meal(USER_MEAL_ID, LocalDateTime.of(2020, Month.OCTOBER, 15, 10, 0), "USER breakfast", 500);
    public static final Meal USER_MEAL_151300 = new Meal(USER_MEAL_ID + 1, LocalDateTime.of(2020, Month.OCTOBER, 15, 13, 0), "USER lunch", 1000);
    public static final Meal USER_MEAL_152000 = new Meal(USER_MEAL_ID + 2, LocalDateTime.of(2020, Month.OCTOBER, 15, 20, 0), "USER dinner", 500);
    public static final Meal USER_MEAL_160000 = new Meal(USER_MEAL_ID + 3, LocalDateTime.of(2020, Month.OCTOBER, 16, 0, 0), "USER night food", 100);
    public static final Meal USER_MEAL_161000 = new Meal(USER_MEAL_ID + 4, LocalDateTime.of(2020, Month.OCTOBER, 16, 10, 0), "USER breakfast", 500);
    public static final Meal USER_MEAL_161300 = new Meal(USER_MEAL_ID + 5, LocalDateTime.of(2020, Month.OCTOBER, 16, 13, 0), "USER lunch", 1000);
    public static final Meal USER_MEAL_162000 = new Meal(USER_MEAL_ID + 6, LocalDateTime.of(2020, Month.OCTOBER, 16, 20, 0), "USER dinner", 500);
    //public static final Meal ADMIN_MEAL_151000 = new Meal(ADMIN_MEAL_ID, LocalDateTime.of(2020, Month.OCTOBER, 15, 10, 0), "ADMIN breakfast", 500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 15, 0, 0), "newDescription", 350);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(USER_MEAL_151000);
        updated.setDescription("UpdatedDescription");
        updated.setDateTime(LocalDateTime.of(2020, Month.OCTOBER, 15, 0, 0));
        updated.setCalories(350);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
