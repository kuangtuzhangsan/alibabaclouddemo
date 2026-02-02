package com.example.order.openfeignDemo;

import com.example.common.web.ApiResponse;
import com.example.order.config.FeignConfig;
import com.example.common.openfeignDemo.UserDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "service-user",
        url = "${service-user.url:http://localhost:28081}",
        configuration = FeignConfig.class
)
public interface UserClient {
    @GetMapping("/openfeigndemo/user/{id}")
    ApiResponse<UserDTO> getUser(@PathVariable("id") Long id);
}
