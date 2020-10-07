package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealDaoMemoryImpl implements MealService {
    private Map<Long, Meal> meals = new ConcurrentHashMap<Long, Meal>() {{
        put(1L, new Meal(1L, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        put(2L, new Meal(2L, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        put(3L, new Meal(3L, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        put(4L, new Meal(4L, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        put(5L, new Meal(5L, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        put(6L, new Meal(6L, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        put(7L, new Meal(7L, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }};

    private final AtomicLong lasted = new AtomicLong(8);

    public Long generateId() {
        return lasted.getAndIncrement();
    }

    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    public Meal add(Meal meal) {
        meal.setId(generateId());
        meals.put(meal.getId(), meal);
        return meal;
    }

    public void delete(Long id) {
        if (meals.containsKey(id)) {
            meals.remove(id);
        }
    }

    public Meal update(Meal meal) {
        Meal updated = getById(meal.getId());
        if (updated == null) return null;
        updated.setDescription(meal.getDescription());
        updated.setDateTime(meal.getDateTime());
        updated.setCalories(meal.getCalories());
        return updated;
    }

    public Meal getById(Long id) {
        return meals.get(id);
    }
}
