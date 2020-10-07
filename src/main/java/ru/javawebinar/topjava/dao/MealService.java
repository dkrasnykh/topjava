package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealService {
    List<Meal> getAll();

    Meal add(Meal meal);

    void delete(Long id);

    Meal update(Meal meal);

    Meal getById(Long id);
}
