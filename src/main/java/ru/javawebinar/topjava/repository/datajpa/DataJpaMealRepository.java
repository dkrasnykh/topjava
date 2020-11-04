package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudMealRepository;
    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudMealRepository, CrudUserRepository crudUserRepository) {
        this.crudMealRepository = crudMealRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getOne(userId));
        if (meal.isNew()) {
            return crudMealRepository.save(meal);
        } else {
            Meal exist = get(meal.getId(), userId);
            if (exist == null) {
                return null;
            }
            exist.setCalories(meal.getCalories());
            exist.setDescription(meal.getDescription());
            exist.setDateTime(meal.getDateTime());
            return crudMealRepository.save(exist);
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudMealRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudMealRepository.getMealByIdAndUser(id, crudUserRepository.getOne(userId));
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudMealRepository.findAllByUserOrderByDateTimeDesc(crudUserRepository.getOne(userId));
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudMealRepository.findAllByUserAndDateTimeGreaterThanEqualAndDateTimeBeforeOrderByDateTimeDesc(crudUserRepository.getOne(userId), startDateTime, endDateTime);
    }
}
