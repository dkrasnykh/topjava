package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println();
        List<UserMealWithExcess> mTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mTo.forEach(System.out::println);

        System.out.println();
        List<UserMealWithExcess> mTo1 = filteredByMealWithExcessCollector(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mTo1.forEach(System.out::println);

    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDateTime, Integer> map = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().truncatedTo(ChronoUnit.DAYS), Collectors.summingInt(meal->meal.getCalories())));
        for(UserMeal userMeal: meals){
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)){
                LocalDateTime date = userMeal.getDateTime().truncatedTo(ChronoUnit.DAYS);
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), (int)map.get(date) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDateTime, Integer> map = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().truncatedTo(ChronoUnit.DAYS), Collectors.summingInt(meal->meal.getCalories())));

        List<UserMealWithExcess> result = meals.stream()
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), (int)map.get(meal.getDateTime().truncatedTo(ChronoUnit.DAYS)) > caloriesPerDay))
                .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());

        return result;
    }

    public static List<UserMealWithExcess> filteredByMealWithExcessCollector(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new MealWithExcessCollector(caloriesPerDay, startTime, endTime));
    }
}
