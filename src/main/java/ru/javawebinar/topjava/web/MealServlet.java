package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private static String INSERT_OR_EDIT = "/meal.jsp";
    private static String LIST_MEAL = "/meals.jsp";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private MealDao dao;

    public MealServlet(){
        super();
        dao = new MealDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String forward="";
        String action = req.getParameter("action");
        if(action == null) action = "listMeal";
        List<MealTo> mealsTo;
        if (action.equalsIgnoreCase("delete")){
            long mealId = Long.parseLong(req.getParameter("id"));
            dao.delete(mealId);
            forward = LIST_MEAL;
            mealsTo = MealsUtil.filteredByStreams(dao.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);
            req.setAttribute("meals", mealsTo);
        } else if (action.equalsIgnoreCase("edit")){
            forward = INSERT_OR_EDIT;
            long mealId = Long.parseLong(req.getParameter("id"));
            Meal meal = dao.getById(mealId);
            req.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("listMeal")){
            forward = LIST_MEAL;
            mealsTo = MealsUtil.filteredByStreams(dao.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000);
            req.setAttribute("meals", mealsTo);
        } else {
            forward = INSERT_OR_EDIT;
        }
        RequestDispatcher view = req.getRequestDispatcher(forward);
        view.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        LocalDateTime date = LocalDateTime.parse(req.getParameter("dateTime"), formatter);
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));

        String mealId = req.getParameter("id");
        if (mealId == null || mealId.isEmpty()){
            Meal meal = new Meal(MealDao.generateId(), date, description, calories);
            dao.add(meal);
        } else {
            long id = Long.parseLong(req.getParameter("id"));
            dao.update(id, description, date, calories);
        }
        RequestDispatcher view = req.getRequestDispatcher(LIST_MEAL);
        req.setAttribute("meals", MealsUtil.filteredByStreams(dao.getAll(), LocalTime.of(0, 0), LocalTime.of(23, 59), 2000));
        view.forward(req, resp);
    }
}
