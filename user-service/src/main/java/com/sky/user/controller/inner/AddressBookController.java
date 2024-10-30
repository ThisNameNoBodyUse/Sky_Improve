package com.sky.user.controller.inner;

import com.sky.entity.AddressBook;
import com.sky.user.service.AddressBookService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("innerAddressBook")
@RequestMapping("/addressBook")
@Api(tags = "内部地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 根据id查询地址簿
     * @param addressBookId
     * @return
     */
    @GetMapping("/{addressBookId}")
    public AddressBook getAddressBookById(@PathVariable("addressBookId") Long addressBookId) {
        return addressBookService.getById(addressBookId);
    }


}
