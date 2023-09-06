package com.alibou.security.controllers;

import com.alibou.security.request.CategoryRequest;
import com.alibou.security.request.ItemRequest;
import com.alibou.security.request.UpdateItemRequest;
import com.alibou.security.response.ItemResponse;
import com.alibou.security.response.Response;
import com.alibou.security.services.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    ItemService itemService;

 @PostMapping("/create")
  public ResponseEntity<?> createItem(HttpServletRequest request, @RequestBody ItemRequest itemRequest){
     Response response=itemService.createItem(request,itemRequest);
     if (Objects.equals(response.getMessage(), "User not found")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "User not activated")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "All fields are required")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }

     if (Objects.equals(response.getMessage(), "Created successfully")) {
         return new ResponseEntity<>(response, HttpStatus.OK);
     }
     response.setMessage("Server error");
     return new ResponseEntity<>(response,HttpStatus.OK);
 }

 @GetMapping("/get/public")
 public ResponseEntity<?> getItems(@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "1") int page){
     ItemResponse response=itemService.getItems(size,page);

     if (Objects.equals(response.getMessage(), "fetch items successfully")) {
         return new ResponseEntity<>(response, HttpStatus.OK);
     }
     response.setMessage("Server error");
     return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
 }

 @DeleteMapping("delete/{id}")
 public ResponseEntity<?> deleteItem(HttpServletRequest request,@PathVariable Long id){
     Response response = itemService.deleteItem(request,id);

     if (Objects.equals(response.getMessage(), "User not found")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "User not activated")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "Item not found")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "User not authorized")) {
         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
     }
     if (Objects.equals(response.getMessage(), "Item deleted successfully")) {
         return new ResponseEntity<>(response, HttpStatus.OK);
     }

     response.setMessage("Server error");
     return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
 }

 @PutMapping("/soft-delete/{id}")
 public ResponseEntity<?> softDeleteItem(HttpServletRequest request,@PathVariable Long id){
     Response response=itemService.softDeleteItem(request,id);
     if (Objects.equals(response.getMessage(), "User not found")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "User not activated")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "Item not found")) {
         return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
     }
     if (Objects.equals(response.getMessage(), "User not authorized")) {
         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
     }
     if (Objects.equals(response.getMessage(), "Item deleted successfully")) {
         return new ResponseEntity<>(response, HttpStatus.OK);
     }

     response.setMessage("Server error");
     return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
 }

    @PutMapping("/soft-delete-recover/{id}")
    public ResponseEntity<?> softDeleteRecoverItem(HttpServletRequest request,@PathVariable Long id){
        Response response=itemService.softDeleteRecoverItem(request,id);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Item not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "Item recovered successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateItem(HttpServletRequest request, @PathVariable Long id, @RequestBody UpdateItemRequest updateItemRequest){
     Response response=itemService.updateItem(request,id,updateItemRequest);

        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Item not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "All fields are required")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Item updated successfully")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @DeleteMapping("/delete-item-image/{id}")
    public ResponseEntity<?> deleteItemPicture(HttpServletRequest request,@PathVariable Long id){
     Response response=itemService.deleteItemPicture(request,id);
        if (Objects.equals(response.getMessage(), "User not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "User not activated")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (Objects.equals(response.getMessage(), "Picture not found")) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(response.getMessage(), "Unable to authorize")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "User not authorized")) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (Objects.equals(response.getMessage(), "Item Picture Successfully deleted")) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }





}
