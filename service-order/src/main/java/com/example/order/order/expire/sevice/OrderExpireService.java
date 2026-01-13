package com.example.order.order.expire.sevice;

import com.example.order.order.entity.OrderInfo;
import com.example.order.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderExpireService {

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public void expireOrders() {

        LocalDateTime expireTime =
                LocalDateTime.now().minusMinutes(15);

        List<OrderInfo> orders =
                orderMapper.selectExpiredOrders(expireTime);

        for (OrderInfo order : orders) {
            orderMapper.cancelOrder(order.getId());
        }
    }
}

