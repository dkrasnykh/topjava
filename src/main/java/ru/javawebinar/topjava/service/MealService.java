package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundWithId;

@Service
public class MealService {
    private final MealRepository repository;

    @Autowired
    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, Integer userId) {
        return repository.save(meal, userId);
    }

    public void delete(int id, Integer userID) {
        checkNotFoundWithId(repository.delete(id, userID), id);
    }

    public Meal get(int id, Integer userID) {
        return checkNotFoundWithId(repository.get(id, userID), id);
    }

    public List<Meal> getAll(Integer userID) {
        return new ArrayList<>(repository.getAll(userID));
    }

    public void update(Meal meal, Integer userID) {
        checkNotFoundWithId(repository.save(meal, userID), meal.getId());
    }
}