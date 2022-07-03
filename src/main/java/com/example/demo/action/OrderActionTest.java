package com.example.demo.action;

import com.example.demo.annotation.OrderAction;

/**
 * @author dong
 * @date 2022/5/12 21:30
 */
public class OrderActionTest {

    @OrderAction
    public void orderCreate() {
        System.out.println("创建订单");
    }

}
