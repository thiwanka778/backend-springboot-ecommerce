package com.alibou.security.controllers;

import com.alibou.security.models.Category;
import com.alibou.security.request.CategoryRequest;
import com.alibou.security.response.GetAllCategoryResponse;
import com.alibou.security.response.Response;
import com.alibou.security.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(HttpServletRequest request, @RequestBody CategoryRequest categoryRequest){

        Response response=categoryService.createCategory(request,categoryRequest);

        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Not Authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "All fields are required")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Category created successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @GetMapping("/get/public")
    public ResponseEntity<?> getAllCategory(){

        GetAllCategoryResponse response = categoryService.getAllCategory();

        if (Objects.equals(response.getMessage(), "Categories retrieved successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(HttpServletRequest request,@PathVariable Long id,@RequestBody CategoryRequest categoryRequest){

        Response response= categoryService.updateCategory(request,id,categoryRequest);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Not Authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "All fields are required")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Category not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "category updated successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDelete(HttpServletRequest request,@PathVariable Long id){
        Response response= categoryService.softDelete(request,id);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Not Authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(response.getMessage(), "Category not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "category deleted successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping("/soft-delete-recover/{id}")
    public ResponseEntity<?> softDeleteRecover(HttpServletRequest request,@PathVariable Long id){
        Response response= categoryService.softDeleteRecover(request,id);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Not Authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(response.getMessage(), "Category not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "category recovered successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(HttpServletRequest request,@PathVariable Long id){
        Response response =categoryService.deleteCategory(request,id);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Not Authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(response.getMessage(), "Category not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(response.getMessage(), "category deleted successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }


        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
