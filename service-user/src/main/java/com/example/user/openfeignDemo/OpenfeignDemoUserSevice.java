package com.example.user.openfeignDemo;

import com.example.common.openfeignDemo.UserDTO;
import com.example.common.web.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign演示用户服务接口
 */
public interface OpenfeignDemoUserSevice {

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息响应
     */
    ApiResponse<UserDTO> getUserById(@PathVariable("id") Long id);
}
