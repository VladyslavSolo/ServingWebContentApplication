package com.training.controllers;

import com.training.models.internal.User;
import com.training.services.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AuthorizationController {

    private final UserService userService;

    @GetMapping("/registration")
    public ModelAndView registrationPage() {
        return new ModelAndView("/registration");
    }

    @PostMapping("/registration")
    public ModelAndView registration(@NotNull User user, Model model) {
        ModelAndView registrationPage = registrationPage();
        User userFromBd = userService.getUserByUsername(user.getUsername());

        if (userFromBd != null) {
            model.addAttribute("message", "User already exists");
            return registrationPage;
        }
        userService.registerCurrentUser(user);
        return new ModelAndView("redirect:/login");
    }

    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

}
