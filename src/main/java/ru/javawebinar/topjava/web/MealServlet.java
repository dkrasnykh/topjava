package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDaoMemoryImpl;
import ru.javawebinar.topjava.dao.MealService;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private String insertOrEdit = "/meal.jsp";
    private String listMeal = "/meals.jsp";
    private MealService dao;

    @Override
    public void init() {
        dao = new MealDaoMemoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "listMeal";
        }

        Long mealId = null;
        String idparam = req.getParameter("id");
        if (!(idparam == null || idparam.isEmpty())) {
            mealId = parseId(req.getParameter("id"));
        }

        switch (action) {
            case "delete":
                dao.delete(mealId);
                log.debug("delete meals with id = " + mealId);
                resp.sendRedirect(req.getContextPath() + "/meals");
                break;
            case "edit":
                req.setAttribute("meal", dao.getById(mealId));
                log.debug("update meals with id = " + mealId);
                req.getRequestDispatcher(insertOrEdit).forward(req, resp);
                break;
            case "insert":
                req.getRequestDispatcher(insertOrEdit).forward(req, resp);
                break;
            case "listMeal":
                req.setAttribute("meals", MealsUtil.filteredByStreams(dao.getAll(), LocalTime.MIN, LocalTime.MAX, 2000));
                req.getRequestDispatcher(listMeal).forward(req, resp);
                break;
            default:
                log.debug("action " + action + " will not be processed");
                req.getRequestDispatcher(listMeal).forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        LocalDateTime date = LocalDateTime.parse(req.getParameter("dateTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));

        String mealId = req.getParameter("id");
        if (mealId == null || mealId.isEmpty()) {
            Meal meal = new Meal(date, description, calories);
            log.debug("Meal " + date.toString() + " " + description + " " + calories + " added");
            dao.add(meal);
        } else {
            long id = parseId(req.getParameter("id"));
            dao.update(new Meal(id, date, description, calories));
        }
        resp.sendRedirect(req.getContextPath() + "/meals");
    }

    private Long parseId(String id) {
        return Long.parseLong(id);
    }
}
