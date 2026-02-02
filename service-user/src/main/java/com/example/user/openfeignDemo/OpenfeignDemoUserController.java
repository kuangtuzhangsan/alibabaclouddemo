package com.example.user.openfeignDemo;

import com.example.common.exception.BizException;
import com.example.common.exception.ErrorCode;
import com.example.common.openfeignDemo.UserDTO;
import com.example.common.web.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openfeigndemo/user")
public class OpenfeignDemoUserController {

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        if (id <= 0) throw new BizException(ErrorCode.USER_NOT_EXIST);
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@example.com");
        return ApiResponse.success(user);
    }
}
