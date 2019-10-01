package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        getFilteredWithExceededWithStream(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        ArrayList<UserMeal> filteredMealList = new ArrayList<>();
        HashMap<LocalDate, Integer> dailyCalories = new HashMap<>();

        mealList.forEach(userMeal -> {
            LocalDate currDay = userMeal.getDateTime().toLocalDate();
            if (TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredMealList.add(userMeal);
            }
            if (!dailyCalories.containsKey(currDay)) {
                dailyCalories.put(currDay, userMeal.getCalories());
            } else {
                dailyCalories.put(currDay, dailyCalories.get(currDay) + userMeal.getCalories());
            }
        });
        ArrayList<UserMealWithExceed> userMealWithExceeds = new ArrayList<>();
        filteredMealList.forEach(userMeal -> {
            userMealWithExceeds.add(
                    new UserMealWithExceed(
                            userMeal.getDateTime(),
                            userMeal.getDescription(),
                            userMeal.getCalories(),
                            dailyCalories.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay
                    )
            );
        });
        return userMealWithExceeds;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededWithStream(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dailyCalories =
                mealList
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        userMeal -> userMeal.getDateTime().toLocalDate(),
                                        Collectors.summingInt(UserMeal::getCalories)
                                )
                        );

        return mealList
                .stream()
                .filter(userMeal -> TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal ->
                        new UserMealWithExceed(
                                userMeal.getDateTime(),
                                userMeal.getDescription(),
                                userMeal.getCalories(),
                                dailyCalories.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay
                        )
                )
                .collect(Collectors.toList());
    }
}
