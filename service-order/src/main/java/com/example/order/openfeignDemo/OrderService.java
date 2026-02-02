package com.example.order.openfeignDemo;

import com.example.common.exception.RemoteServiceException;
import com.example.common.openfeignDemo.UserDTO;
import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private UserClient userClient;

    public void createOrder(Long userId, Long skuId) {
        try {
            ApiResponse<UserDTO> response = userClient.getUser(userId);
            if (response.getCode() != 0) {
                throw new RemoteServiceException(400, response.getMessage(), response.getCode());
            }

            UserDTO user = response.getData();
            log.info("获取用户信息：{} {}", user.getName(), user.getEmail());
        } catch (RemoteServiceException e) {
            log.error("调用 service-user 失败, code={}, message={}", e.getCode(), e.getMessage());
            throw e;
        }
    }
}
