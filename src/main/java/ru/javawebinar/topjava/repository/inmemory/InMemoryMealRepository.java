package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {

    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Meal save(int userId, Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            Map<Integer, Meal> meals = new HashMap<>();
            meals.put(meal.getId(), meal);
            repository.putIfAbsent(userId, meals);
            repository.get(userId).putIfAbsent(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but not present in storage
        if (meal.getUserId() != userId) {
            return null;
        }
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        if (!repository.containsKey(userId) && repository.get(userId).getOrDefault(id, null) == null) {
            return false;
        }
        return repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        if (!repository.containsKey(userId) && repository.get(userId).getOrDefault(id, null) == null) {
            return null;
        }
        return repository.get(userId).get(id);
    }

    public Collection<Meal> getFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        return getAll(userId).stream().filter(meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate)).collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return Collections.unmodifiableCollection(
                (List<Meal>) repository.getOrDefault(userId, Collections.EMPTY_MAP).values()
                        .stream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList())
        );
    }
}

