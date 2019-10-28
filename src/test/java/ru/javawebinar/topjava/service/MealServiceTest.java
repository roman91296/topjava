package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal excepted = MealTestData.MEALS.get(0);
        Meal actual = service.get(excepted.getId(), USER_ID);
        assertThat(actual).isEqualTo(excepted);
    }

    @Test(expected = NotFoundException.class)
    public void getAlien() {
        Meal excepted = MealTestData.MEALS.get(0);
        service.get(excepted.getId(), 1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        service.get(1, USER_ID);
    }

    @Test
    public void delete() {
        final int indexFirstElement = 0;
        service.delete(MealTestData.MEALS.get(indexFirstElement).getId(), USER_ID);
        List<Meal> expectedList = new ArrayList<>();
        expectedList.addAll(MealTestData.MEALS);
        expectedList.remove(indexFirstElement);
        assertThat(service.getAll(USER_ID)).isEqualTo(expectedList);
    }

    @Test(expected = NotFoundException.class)
    public void deleteAlien() {
        final int indexFirstElement = 0;
        service.delete(MealTestData.MEALS.get(indexFirstElement).getId(), 1);
    }

    @Test(expected = NotFoundException.class)
    public void deletedNotFound() {
        service.delete(0, USER_ID);
    }

    @Test
    public void getBetweenDates() {
        LocalDate startDate = LocalDate.of(2019, Month.OCTOBER, 11);
        LocalDate endDate = LocalDate.of(2019, Month.OCTOBER, 11);
        List<Meal> expected = MealTestData.MEALS
                .stream()
                .filter(meal -> Util.isBetweenInclusive(meal.getDate(), startDate, endDate))
                .collect(Collectors.toList());
        assertThat(service.getBetweenDates(startDate, endDate, USER_ID)).isEqualTo(expected);
    }

    @Test
    public void getAll() {
        assertThat(service.getAll(USER_ID)).isEqualTo(MealTestData.MEALS);
    }

    @Test
    public void update() {
        Meal expected = MealTestData.MEALS.get(0);
        expected.setDescription("Обновленное блюдо");
        service.update(expected, USER_ID);
        assertThat(service.get(expected.getId(), USER_ID)).isEqualTo(expected);
    }

    @Test(expected = NotFoundException.class)
    public void updateAlien() {
        Meal expected = MealTestData.MEALS.get(0);
        expected.setDescription("Обновленное блюдо");
        service.update(expected, 1);
    }

    @Test
    public void create() {
        Meal expected = new Meal(LocalDateTime.now(), "Новое блюдо", 191);
        Meal actual = service.create(expected, USER_ID);
        expected.setId(actual.getId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeCreate() {
        service.create(new Meal(MealTestData.MEALS.get(0).getDateTime(), "Duplicate dish", 170), USER_ID);
    }
}