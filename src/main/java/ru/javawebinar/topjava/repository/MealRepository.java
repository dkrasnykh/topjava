package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealRepository {
    // null if not found, when updated
    Meal save(Meal meal, Integer userID);

    // false if not found
    boolean delete(int id, Integer userID);

    // null if not found
    Meal get(int id, Integer userID);

    Collection<Meal> getAll(Integer userID);
}
