package com.example.user.openfeignDemo;

import com.example.common.exception.BizException;
import com.example.common.exception.ErrorCode;
import com.example.common.openfeignDemo.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 把实现类当 Controller（不推荐，但可以用）
 */
@RestController
@RequestMapping("/openfeigndemoservice/user")
@Service
public class OpenfeignDemoUserSeviceImpl implements OpenfeignDemoUserSevice{


    @Override
    @GetMapping("/{id}")
    public UserDTO getUserById(Long id) {
        if (id <= 0) throw new BizException(ErrorCode.USER_NOT_EXIST);
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }
}
