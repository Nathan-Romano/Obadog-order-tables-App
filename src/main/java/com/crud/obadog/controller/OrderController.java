package com.crud.obadog.controller;

import com.crud.obadog.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping({"","/"})
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "orders/index";
    }

}
