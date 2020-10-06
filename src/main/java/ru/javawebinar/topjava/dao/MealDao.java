package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MealDao implements MealService {
    private Map<Long, Meal> meals = Collections.synchronizedMap(new HashMap<Long, Meal>() {{
        put(1L, new Meal(1L, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        put(2L, new Meal(2L, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        put(3L, new Meal(3L, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        put(4L, new Meal(4L, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        put(5L, new Meal(5L, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        put(6L, new Meal(6L, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        put(7L, new Meal(7L, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }});

    private final AtomicLong lasted = new AtomicLong(8);

    public Long generateId() {
        return lasted.getAndIncrement();
    }

    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    public void add(Meal meal) {
        meals.put(meal.getId(), meal);
    }

    public synchronized void delete(Long id) {
        if (meals.containsKey(id)) {
            meals.remove(id);
        } else {
            throw new RuntimeException("Meal with id = " + id + " does not exist");
        }
    }

    public synchronized void update(Long id, String description, LocalDateTime date, int calories) {
        Meal updated = getById(id);

        if (updated == null) {
            throw new RuntimeException("Meal with id = " + id + " does not exist");
        }
        updated.setDescription(description);
        updated.setDateTime(date);
        updated.setCalories(calories);
    }

    public Meal getById(Long id) {
        return meals.get(id);
    }
}
