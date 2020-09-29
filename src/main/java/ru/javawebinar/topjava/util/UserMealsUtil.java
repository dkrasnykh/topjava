package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
        mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println();
        mealsTo = filteredByMealWithExcessCollector(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = new HashMap<>();
        for (UserMeal meal : meals) {
            caloriesByDays.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                LocalDate date = userMeal.getDateTime().toLocalDate();
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), caloriesByDays.get(date) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDays = meals.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesByDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByMealWithExcessCollector(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class MealWithExcessCollector implements Collector<UserMeal, Map<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> {
            private final int caloriesPerDay;
            private final LocalTime startTime;
            private final LocalTime endTime;

            public MealWithExcessCollector(int caloriesPerDay, LocalTime startTime, LocalTime endTime){
                this.caloriesPerDay = caloriesPerDay;
                this.startTime = startTime;
                this.endTime = endTime;
            }

            @Override
            public Supplier<Map<LocalDate, List<UserMeal>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, List<UserMeal>>, UserMeal> accumulator() {
                return (c, e) -> {
                    LocalDate key = e.getDateTime().toLocalDate();
                    if (c.containsKey(key)){
                        c.get(key).add(e);
                    } else {
                        c.put(key, new ArrayList<>(Collections.singletonList(e)));
                    }
                };
            }

            @Override
            public BinaryOperator<Map<LocalDate, List<UserMeal>>> combiner() {
                return (c1, c2) -> {
                    for (Map.Entry<LocalDate, List<UserMeal>> entry : c2.entrySet()){
                        c1.merge(entry.getKey(), entry.getValue(), (prev, curr) -> {
                            prev.addAll(curr);
                            return prev;
                        });
                    }
                    return c1;
                };
            }

            @Override
            public Function<Map<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> finisher() {
                return (m) ->{
                    List<UserMealWithExcess> result = new ArrayList<>();
                    for(Map.Entry<LocalDate, List<UserMeal>> entry : m.entrySet()){
                        int caloriesByDay = entry.getValue().stream().mapToInt(UserMeal::getCalories).sum();
                        result.addAll(entry.getValue().stream()
                                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(),
                                        userMeal.getCalories(), caloriesByDay > caloriesPerDay))
                                .collect(Collectors.toList()));
                    }
                    return result;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        }
        return meals.stream().collect(new MealWithExcessCollector(caloriesPerDay, startTime, endTime));
    }
}
