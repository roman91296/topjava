package ru.javawebinar.topjava.web.user;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.web.SecurityUtil.getAuthUserId;

@Controller
public class ProfileRestController extends AbstractUserController {

    public User get() {
        return super.get(getAuthUserId());
    }

    public void delete() {
        super.delete(getAuthUserId());
    }

    public void update(User user) {
        super.update(user, getAuthUserId());
    }
}