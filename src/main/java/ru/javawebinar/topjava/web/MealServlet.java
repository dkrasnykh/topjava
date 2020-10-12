package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private MealRestController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            controller = appCtx.getBean(MealRestController.class);
        }
        for (Meal meal : MealsUtil.meals) {
            controller.create(meal);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")), SecurityUtil.authUserId());

        if (meal.isNew()) {
            log.info("Create {}", meal);
            controller.create(meal);
        } else {
            log.info("Update {}", meal);
            controller.update(meal, getId(request));
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String date1 = request.getParameter("date1");
        String date2 = request.getParameter("date2");
        String time1 = request.getParameter("time1");
        String time2 = request.getParameter("time2");

        LocalDate startDate = null;
        LocalTime startTime = null;
        LocalDate endDate = null;
        LocalTime endTime = null;

        if (date1 != null && date2 != null && time1 != null && time2 != null) {
            action = "filterOut";
            startDate = LocalDate.parse(date1, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            startTime = LocalTime.parse(time1, DateTimeFormatter.ofPattern("HH:mm"));
            endDate = LocalDate.parse(date2, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endTime = LocalTime.parse(time2, DateTimeFormatter.ofPattern("HH:mm"));
        }

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, SecurityUtil.authUserId()) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filterOut":
                request.setAttribute("meals", controller.getAllWithFiltres(startDate, endDate, startTime, endTime));
                response.sendRedirect("meals");
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals", controller.getAllTo());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
