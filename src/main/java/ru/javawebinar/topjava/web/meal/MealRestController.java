package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.getAuthUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public Meal get(int id) {
        log.info("get {}, userId = {}", id, getAuthUserId());
        return service.get(getAuthUserId(), id);
    }

    public Meal create(Meal meal) {
        log.info("create {}, userId = {}", meal, getAuthUserId());
        checkNew(meal);
        return service.create(getAuthUserId(), meal);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}, userId = {}", meal, id, getAuthUserId());
        assureIdConsistent(meal, id);
        service.update(getAuthUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete {}, userId = {}", id, getAuthUserId());
        service.delete(getAuthUserId(), id);
    }

    public List<MealTo> getAll() {
        log.info("getAll, userId = {}", getAuthUserId());
        return MealsUtil.getTos(service.getAll(getAuthUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getFiltered by date [{} - {}] and time [{} - {}], userId = {}", startDate, endDate, startTime, endTime, getAuthUserId());
        List<Meal> filteredByDateMeals = service.getBetweenDates(getAuthUserId(), startDate, endDate);
        return MealsUtil.getFilteredTos(filteredByDateMeals, authUserCaloriesPerDay(), startTime, endTime);
    }

}