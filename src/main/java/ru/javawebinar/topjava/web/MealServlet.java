package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealService;
import ru.javawebinar.topjava.model.Meal;
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

public class MealServlet extends HttpServlet {
    private final static String INSERT_OR_EDIT = "/meal.jsp";
    private final static String LIST_MEAL = "/meals.jsp";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private MealService dao;

    @Override
    public void init() throws ServletException {
        super.init();
        dao = new MealDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "listMeal";
        }

        Long mealId = null;
        Meal meal = null;
        String idparam = req.getParameter("id");
        if (!(idparam == null || idparam.isEmpty())) {
            mealId = Long.parseLong(req.getParameter("id"));
            meal = dao.getById(mealId);
        }

        switch (action) {
            case "delete":
                dao.delete(mealId);
                resp.sendRedirect(req.getContextPath() + "/meals");
                break;
            case "edit":
                req.setAttribute("meal", meal);
                req.getRequestDispatcher(INSERT_OR_EDIT).forward(req, resp);
                break;
            case "listMeal":
                req.setAttribute("meals", MealsUtil.filteredByStreams(dao.getAll(), LocalTime.of(0, 0),
                        LocalTime.of(23, 59), 2000));
                req.getRequestDispatcher(LIST_MEAL).forward(req, resp);
                break;
            default:
                req.getRequestDispatcher(INSERT_OR_EDIT).forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        LocalDateTime date = LocalDateTime.parse(req.getParameter("dateTime"), formatter);
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));

        String mealId = req.getParameter("id");
        if (mealId == null || mealId.isEmpty()) {
            Meal meal = new Meal(dao.generateId(), date, description, calories);
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
