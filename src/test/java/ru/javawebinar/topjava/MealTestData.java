package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MealTestData {
    public static final int DEFAULT_CALORIES_PER_DAY = 2000;

    public static final List<Meal> MEALS = Arrays.asList(
            new Meal(100002, LocalDateTime.of(2019, Month.OCTOBER, 11, 9, 0), "Завтрак", 500),
            new Meal(100003, LocalDateTime.of(2019, Month.OCTOBER, 11, 12, 0), "Обед", 1000),
            new Meal(100004, LocalDateTime.of(2019, Month.OCTOBER, 11, 19, 0), "Ужин", 500),
            new Meal(100005, LocalDateTime.of(2019, Month.OCTOBER, 12, 9, 20), "Завтрак", 1000),
            new Meal(100006, LocalDateTime.of(2019, Month.OCTOBER, 12, 13, 0), "Обед", 500),
            new Meal(100007, LocalDateTime.of(2019, Month.OCTOBER, 12, 20, 0), "Ужин", 510)
    );

    static {
        MEALS.sort(Comparator.comparing(Meal::getDateTime).reversed());
    }

}
