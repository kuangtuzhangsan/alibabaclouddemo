package com.example.user.openfeignDemo;

import com.example.common.exception.BizException;
import com.example.common.exception.ErrorCode;
import com.example.common.openfeignDemo.UserDTO;
import com.example.common.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenFeign演示用户服务实现
 * 注意：同时标注@RestController和@Service不推荐，但可以用
 */
@Slf4j
@RestController
@RequestMapping("/openfeigndemoservice/user")
@Service
public class OpenfeignDemoUserSeviceImpl implements OpenfeignDemoUserSevice{

    @Override
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        log.info("OpenFeign演示 - 查询用户, ID: {}", id);
        
        if (id == null || id <= 0) {
            log.warn("OpenFeign演示 - 无效的用户ID: {}", id);
            throw new BizException(ErrorCode.USER_NOT_EXIST);
        }
        
        try {
            UserDTO user = new UserDTO();
            user.setId(id);
            user.setName("User" + id);
            user.setEmail("user" + id + "@example.com");
            
            log.debug("OpenFeign演示 - 返回用户: {}", user);
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("OpenFeign演示 - 查询用户失败, ID: {}", id, e);
            throw new BizException(ErrorCode.USER_NOT_EXIST);
        }
    }
}
