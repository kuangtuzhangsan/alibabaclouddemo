package com.example.user.openfeignDemo;

import com.example.common.openfeignDemo.UserDTO;
import org.springframework.web.bind.annotation.PathVariable;


public interface OpenfeignDemoUserSevice {

    UserDTO getUserById(@PathVariable("id") Long id);
}
