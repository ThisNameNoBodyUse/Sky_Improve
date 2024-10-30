package com.sky.api.client;

import com.sky.entity.AddressBook;
import com.sky.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("user-service")
public interface UserClient {

    @GetMapping("/addressBook/{addressBookId}")
    AddressBook getAddressBookById(@PathVariable("addressBookId") Long addressBookId);

    @GetMapping("/user/{userId}")
    User getUserById(@PathVariable Long userId);

    @GetMapping("/user/total")
    Integer countByMap(@RequestParam Map map);

}
