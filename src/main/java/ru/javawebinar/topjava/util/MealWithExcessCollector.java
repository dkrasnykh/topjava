package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MealWithExcessCollector implements Collector<UserMeal, TreeSet<UserMeal>, List<UserMealWithExcess>> {
    private int caloriesPerDay;
    private LocalTime startTime;
    private LocalTime endTime;

    public MealWithExcessCollector(int caloriesPerDay, LocalTime startTime, LocalTime endTime){
        this.caloriesPerDay = caloriesPerDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Supplier<TreeSet<UserMeal>> supplier() {
        return TreeSet::new;
    }

    @Override
    public BiConsumer<TreeSet<UserMeal>, UserMeal> accumulator() {
        return TreeSet::add;
    }

    @Override
    public BinaryOperator<TreeSet<UserMeal>> combiner() {
        return (l, r) -> { l.addAll(r); return l; };
    }

    @Override
    public Function<TreeSet<UserMeal>, List<UserMealWithExcess>> finisher() {
        return s -> {
            Map<LocalDateTime, Integer> map = s.stream()
                    .collect(Collectors.groupingBy(meal -> meal.getDateTime().truncatedTo(ChronoUnit.DAYS), Collectors.summingInt(meal->meal.getCalories())));

            return s.stream()
                    .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), (int)map.get(meal.getDateTime().truncatedTo(ChronoUnit.DAYS)) > caloriesPerDay))
                    .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.getDateTime().toLocalTime(), startTime, endTime))
                    .collect(Collectors.toList());
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }
}
