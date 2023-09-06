package com.alibou.security.services;

import com.alibou.security.config.JwtTokenExtractor;
import com.alibou.security.models.Category;
import com.alibou.security.repositories.CategoryRepository;
import com.alibou.security.request.CategoryRequest;
import com.alibou.security.response.GetAllCategoryResponse;
import com.alibou.security.response.Response;
import com.alibou.security.response.UpdateUserResponse;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.aot.ApplicationContextAotGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    JwtTokenExtractor tokenExtractor;

    @Autowired
    UserRepository repository;
    @Autowired
    CategoryRepository categoryRepository;
    public Response createCategory(HttpServletRequest request, CategoryRequest categoryRequest) {
        String token = request.getHeader("Authorization");
        String userEmail = tokenExtractor.extractUserEmail(token);

        if (userEmail == null) {

            return Response.builder()
                    .message("User not found")
                    .build();

        }
        Optional<User> userOptional=repository.findByEmail(userEmail);
        if(userOptional.isEmpty()){
            return Response.builder()
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();
        if(!user.isActive()){
            return Response.builder()
                    .message("User not activated")
                    .build();
        }
        if (user.getRole() != Role.ADMIN) {
            return Response.builder()
                    .message("Not Authorized")
                    .build();
        }

        if(categoryRequest.getTitle()==null || categoryRequest.getTitle().isEmpty()
        || categoryRequest.getDes()==null || categoryRequest.getDes().isEmpty()
        || categoryRequest.getImageUrl()==null || categoryRequest.getImageUrl().isEmpty()){
            return Response.builder()
                    .message("All fields are required")
                    .build();
        }

        // Create a Category object
        Category category = new Category();
        category.setTitle(categoryRequest.getTitle());
        category.setDes(categoryRequest.getDes());
        category.setImageUrl(categoryRequest.getImageUrl());
        category.setUserId(user.getId());

        // Save the Category in the database
        categoryRepository.save(category);
//


        // Return a success response
        return Response.builder()
                .message("Category created successfully")
                .build();


    }


    public GetAllCategoryResponse getAllCategory() {
       Optional<List<Category>>  categoryList = categoryRepository.findByIsDeletedFalse();
       if(categoryList.isPresent()){
           return GetAllCategoryResponse.builder()
                   .message("Categories retrieved successfully")
                   .categoryList(categoryList.get())
                   .build();
       }

       return GetAllCategoryResponse.builder()
               .message("Failed to retrieve data")
               .build();


    }

    public Response updateCategory(HttpServletRequest request, Long categoryId, CategoryRequest categoryRequest) {
        String token = request.getHeader("Authorization");
        String userEmail = tokenExtractor.extractUserEmail(token);


        if (userEmail == null) {

            return Response.builder()
                    .message("User not found")
                    .build();

        }
        Optional<User> userOptional=repository.findByEmail(userEmail);
        if(userOptional.isEmpty()){
            return Response.builder()
                    .message("User not found")
                    .build();
        }
            User user = userOptional.get();


        if(!user.isActive()){
            return Response.builder()
                    .message("User not activated")
                    .build();
        }
        if (user.getRole() != Role.ADMIN) {
            return Response.builder()
                    .message("Not Authorized")
                    .build();
        }
        if(categoryRequest.getTitle()==null || categoryRequest.getTitle().isEmpty()
                || categoryRequest.getDes()==null || categoryRequest.getDes().isEmpty()
                || categoryRequest.getImageUrl()==null || categoryRequest.getImageUrl().isEmpty()){
            return Response.builder()
                    .message("All fields are required")
                    .build();
        }

        Optional <Category> categoryOptional=categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()){
            return Response.builder()
                    .message("Category not found")
                    .build();
        }
        Category category=categoryOptional.get();

        if(!Objects.equals(category.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }


        category.setTitle(categoryRequest.getTitle());
        category.setDes(categoryRequest.getDes());
        category.setImageUrl(categoryRequest.getImageUrl());

        Category updatedCategory=categoryRepository.save(category);
        if(updatedCategory!=null){
            return Response.builder()
                    .message("category updated successfully")
                    .build();
        }

        return Response.builder()
                .message("Failed to update category")
                .build();


    }

    public Response softDelete(HttpServletRequest request, Long categoryId) {
        String token = request.getHeader("Authorization");
        String userEmail = tokenExtractor.extractUserEmail(token);


        if (userEmail == null) {

            return Response.builder()
                    .message("User not found")
                    .build();

        }
        Optional<User> userOptional=repository.findByEmail(userEmail);
        if(userOptional.isEmpty()){
            return Response.builder()
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();


        if(!user.isActive()){
            return Response.builder()
                    .message("User not activated")
                    .build();
        }
        if (user.getRole() != Role.ADMIN) {
            return Response.builder()
                    .message("Not Authorized")
                    .build();
        }

        Optional <Category> categoryOptional=categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()){
            return Response.builder()
                    .message("Category not found")
                    .build();
        }
        Category category=categoryOptional.get();

        if(!Objects.equals(category.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        category.setDeleted(true);
        Category updatedCategory=categoryRepository.save(category);

        if(updatedCategory!=null){
            return Response.builder()
                    .message("category deleted successfully")
                    .build();
        }

        return Response.builder()
                .message("Failed to update category")
                .build();

    }

    public Response softDeleteRecover(HttpServletRequest request, Long categoryId) {
        String token = request.getHeader("Authorization");
        String userEmail = tokenExtractor.extractUserEmail(token);


        if (userEmail == null) {

            return Response.builder()
                    .message("User not found")
                    .build();

        }
        Optional<User> userOptional=repository.findByEmail(userEmail);
        if(userOptional.isEmpty()){
            return Response.builder()
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();


        if(!user.isActive()){
            return Response.builder()
                    .message("User not activated")
                    .build();
        }
        if (user.getRole() != Role.ADMIN) {
            return Response.builder()
                    .message("Not Authorized")
                    .build();
        }

        Optional <Category> categoryOptional=categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()){
            return Response.builder()
                    .message("Category not found")
                    .build();
        }
        Category category=categoryOptional.get();

        if(!Objects.equals(category.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        category.setDeleted(false);
        Category updatedCategory=categoryRepository.save(category);

        if(updatedCategory!=null){
            return Response.builder()
                    .message("category recovered successfully")
                    .build();
        }

        return Response.builder()
                .message("Failed to update category")
                .build();
    }

    public Response deleteCategory(HttpServletRequest request, Long categoryId) {
        String token = request.getHeader("Authorization");
        String userEmail = tokenExtractor.extractUserEmail(token);


        if (userEmail == null) {

            return Response.builder()
                    .message("User not found")
                    .build();

        }
        Optional<User> userOptional=repository.findByEmail(userEmail);
        if(userOptional.isEmpty()){
            return Response.builder()
                    .message("User not found")
                    .build();
        }
        User user = userOptional.get();


        if(!user.isActive()){
            return Response.builder()
                    .message("User not activated")
                    .build();
        }
        if (user.getRole() != Role.ADMIN) {
            return Response.builder()
                    .message("Not Authorized")
                    .build();
        }

        Optional <Category> categoryOptional=categoryRepository.findById(categoryId);
        if(categoryOptional.isEmpty()){
            return Response.builder()
                    .message("Category not found")
                    .build();
        }
        Category category=categoryOptional.get();

        if(!Objects.equals(category.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        categoryRepository.deleteById(category.getId());

        Optional<Category> deletedCategory=categoryRepository.findById(categoryId);
        if(deletedCategory.isEmpty()){
            return Response.builder()
                    .message("category deleted successfully")
                    .build();
        }

        return Response.builder()
                .message("failed to delete")
                .build();


    }
}
