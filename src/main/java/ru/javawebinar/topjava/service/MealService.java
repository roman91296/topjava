package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundForUserWithId;

@Service
public class MealService {

    @Autowired
    private MealRepository repository;

    public void init(int userId) {
        MealsUtil.MEALS.forEach(meal -> repository.save(userId, meal));
    }

    public Meal get(int authUserId, int id) {
        return checkNotFoundForUserWithId(repository.get(authUserId, id), authUserId, id);
    }

    public Meal create(int authUserId, Meal meal) {
        return repository.save(authUserId, meal);
    }

    public void update(int authUserId, Meal meal) {
        checkNotFoundForUserWithId(repository.save(authUserId, meal), authUserId, meal.getId());
    }

    public void delete(int authUserId, int mealId) {
        checkNotFoundForUserWithId(repository.delete(authUserId, mealId), authUserId, mealId);
    }

    public List<MealTo> getAll(int authUserId, int caloriesPerDay) {
        return MealsUtil.getTos(repository.getAll(authUserId), caloriesPerDay);
    }

    public List<MealTo> getFilteredByDate(int authUserId, int caloriesPerDay, LocalDate startDate, LocalDate endDate) {
        return MealsUtil.getTos(repository.getFiltered(authUserId, startDate, endDate), caloriesPerDay);
    }

    public List<MealTo> getFilteredByDateTime(int authUserId, int caloriesPerDay, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return MealsUtil.getFilteredTos(repository.getFiltered(authUserId, startDate, endDate), caloriesPerDay, startTime, endTime);
    }


    public List<MealTo> getFilteredByTime(int authUserId, int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
        return MealsUtil.getFilteredTos(repository.getAll(authUserId), caloriesPerDay, startTime, endTime);
    }
}