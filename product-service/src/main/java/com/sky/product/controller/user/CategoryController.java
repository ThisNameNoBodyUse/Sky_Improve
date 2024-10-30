package com.sky.product.controller.user;


import com.sky.entity.Category;
import com.sky.product.service.CategoryService;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类查询
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("分类查询接口")
    public Result<List<Category>> list(Integer type) {
        log.info("查询分类......");
        log.info("type:{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
