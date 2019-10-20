package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.checkNotFoundForUserWithId;

@Service
public class MealService {

    @Autowired
    private MealRepository repository;


    public Meal get(int authUserId, int id) {
        return checkNotFoundForUserWithId(repository.get(authUserId, id), id);
    }

    public Meal create(int authUserId, Meal meal) {
        return repository.save(authUserId, meal);
    }

    public void update(int authUserId, Meal meal) {
        checkNotFoundForUserWithId(repository.save(authUserId, meal), meal.getId());
    }

    public void delete(int authUserId, int mealId) {
        checkNotFoundForUserWithId(repository.delete(authUserId, mealId), mealId);
    }

    public List<Meal> getAll(int authUserId) {
        return repository.getAll(authUserId);
    }

    public List<Meal> getBetweenDates(int authUserId, LocalDate startDate, LocalDate endDate) {
        return repository.getBetween(
                authUserId,
                DateTimeUtil.createLocalDateTime(startDate, DateTimeUtil.MIN_DATE, LocalTime.MIN),
                DateTimeUtil.createLocalDateTime(endDate, DateTimeUtil.MAX_DATE, LocalTime.MAX)
        );
    }
}