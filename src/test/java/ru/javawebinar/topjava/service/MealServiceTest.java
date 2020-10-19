package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.ADMIN_MEAL_ID;
import static ru.javawebinar.topjava.MealTestData.END_DATE;
import static ru.javawebinar.topjava.MealTestData.START_DATE;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_151000;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_151300;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_152000;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_160000;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_161000;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_161300;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_162000;
import static ru.javawebinar.topjava.MealTestData.USER_MEAL_ID;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.getUpdated;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.NOT_FOUND;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})

@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_ID, USER_ID);
        assertMatch(meal, MealTestData.USER_MEAL_151000);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotFoundAdminMeal() {
        assertThrows(NotFoundException.class, () -> service.get(ADMIN_MEAL_ID, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deletedNotFoundAdminMeal() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(ADMIN_MEAL_ID, USER_ID));
    }


    @Test
    public void getBetweenInclusive() {
        List<Meal> actuals = service.getBetweenInclusive(START_DATE, END_DATE, USER_ID);
        assertMatch(actuals, USER_MEAL_162000, USER_MEAL_161300, USER_MEAL_161000, USER_MEAL_160000);
    }

    @Test
    public void getBetweenInclusiveNullFiltres() {
        List<Meal> actuals = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(actuals, USER_MEAL_162000, USER_MEAL_161300, USER_MEAL_161000, USER_MEAL_160000, USER_MEAL_152000, USER_MEAL_151300, USER_MEAL_151000);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, USER_MEAL_162000, USER_MEAL_161300, USER_MEAL_161000,
                USER_MEAL_160000, USER_MEAL_152000, USER_MEAL_151300, USER_MEAL_151000);
    }

    @Test
    public void update() {
        Meal updated = new Meal(USER_MEAL_151000);
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL_ID, USER_ID), updated);
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, NOT_FOUND));
    }

    @Test
    public void updateNotFoundAdminMeal() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 15, 10, 30), "created meal", 100);
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createWithDuplicateKey() {
        Meal meal = new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 15, 10, 0), "dublicate breakfast", 400);
        assertThrows(DuplicateKeyException.class, () -> service.create(meal, USER_ID));
    }
}