package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MealDao {
    private static List<Meal> meals = Collections.synchronizedList(new ArrayList<>(Arrays.asList(
            new Meal(1L, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(2L, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(3L, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(4L, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(5L, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(6L, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(7L, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    )));
    private static Long lastid = 8L;

    public synchronized static long generateId() {
        return lastid++;
    }

    public List<Meal> getAll() {
        return meals;
    }

    public void add(Meal meal) {
        meals.add(meal);
    }

    public void delete(Long id) {
        Meal removed = meals.stream().filter(m -> m.getId() == id).findFirst().get();
        if (removed == null) {
            throw new RuntimeException("Meal with id = " + id + " does not exist");
        }
        meals.remove(removed);
    }

    public void update(Long id, String description, LocalDateTime date, int calories) {
        Meal updated = getById(id);
        if (updated == null) {
            throw new RuntimeException("Meal with id = " + id + " does not exist");
        }
        updated.setDescription(description);
        updated.setDateTime(date);
        updated.setCalories(calories);
    }

    public Meal getById(long id) {
        return meals.stream().filter(m -> m.getId() == id).findFirst().get();
    }
}
