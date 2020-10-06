package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealService {
    List<Meal> getAll();

    void add(Meal meal);

    void delete(Long id);

    void update(Long id, String description, LocalDateTime date, int calories);

    Meal getById(Long id);

    Long generateId();
}
