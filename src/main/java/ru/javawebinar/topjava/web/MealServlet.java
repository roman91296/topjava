package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.web.SecurityUtil.getAuthUserId;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController mealController;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            mealController = appCtx.getBean(MealRestController.class);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")), getAuthUserId());

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) {
            mealController.create(meal);
        } else {
            mealController.update(meal, meal.getId());
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action;

        String button = request.getParameter("cancelBtn");
        if (button != null && button.equalsIgnoreCase("cancelBtn")) {
            action = "all";
        } else {
            action = request.getParameter("action");
        }

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                mealController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000, getAuthUserId()) :
                        mealController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                log.info("get filtered");
                LocalDate startDate = !StringUtils.isEmpty(request.getParameter("startDate")) ? LocalDate.parse(request.getParameter("startDate")) : null;
                LocalDate endDate = !StringUtils.isEmpty(request.getParameter("endDate")) ? LocalDate.parse(request.getParameter("endDate")) : null;
                LocalTime startTime = !StringUtils.isEmpty(request.getParameter("startTime")) ? LocalTime.parse(request.getParameter("startTime")) : null;
                LocalTime endTime = !StringUtils.isEmpty(request.getParameter("endTime")) ? LocalTime.parse(request.getParameter("endTime")) : null;
                List<MealTo> mealTos = null;
                if (startDate != null && endDate != null) {
                    if (startTime == null && endTime == null) {
                        mealTos = mealController.getFilteredByDate(startDate, endDate);
                    } else {
                        mealTos = mealController.getFilteredByDateTime(startDate, endDate, startTime, endTime);
                    }
                } else if (startTime != null && endTime != null) {
                    mealTos = mealController.getFilteredByTime(startTime, endTime);
                } else {
                    mealTos = mealController.getAll();
                }

                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("startTime", startTime);
                request.setAttribute("endTime", endTime);

                request.setAttribute("meals", mealTos);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("startDate", null);
                request.setAttribute("endDate", null);
                request.setAttribute("startTime", null);
                request.setAttribute("endTime", null);
                request.setAttribute("meals", mealController.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
