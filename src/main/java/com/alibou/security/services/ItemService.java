package com.alibou.security.services;

import com.alibou.security.config.JwtTokenExtractor;
import com.alibou.security.models.Item;
import com.alibou.security.models.ItemPicture;
import com.alibou.security.repositories.ItemPictureRepository;
import com.alibou.security.repositories.ItemRepository;
import com.alibou.security.request.ItemRequest;
import com.alibou.security.request.UpdateItemRequest;
import com.alibou.security.response.ItemResponse;
import com.alibou.security.response.ItemResponseObject;
import com.alibou.security.response.Response;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {

    @Autowired
    JwtTokenExtractor tokenExtractor;

    @Autowired
    UserRepository repository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemPictureRepository itemPictureRepository;


    public Response createItem(HttpServletRequest request, ItemRequest itemRequest) {
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

        if(itemRequest.getPrice()==null || itemRequest.getTitle()==null ||
        itemRequest.getTitle().isEmpty() || itemRequest.getDes()==null ||
        itemRequest.getDes().isEmpty() || itemRequest.getImageArray().isEmpty() ||
                itemRequest.getCategoryId()==null){
            return Response.builder()
                    .message("All fields are required")
                    .build();
        }

         Item item=new Item();
        item.setPrice(itemRequest.getPrice());
        item.setTitle(itemRequest.getTitle());
        item.setDes(itemRequest.getDes());
        item.setCategoryId(itemRequest.getCategoryId());
        item.setStockQuantity(itemRequest.getStockQuantity());
        item.setUserId(user.getId());
       Item createdItem=itemRepository.save(item);


        for(String imageUrl : itemRequest.getImageArray()){
            ItemPicture itemPicture=new ItemPicture();
            itemPicture.setUrl(imageUrl);
            itemPicture.setItemId(createdItem.getId());
            itemPictureRepository.save(itemPicture);

        }

        return Response.builder()
                .message("Created successfully")
                .build();


    }

    public ItemResponse getItems(int size, int page) {
        // Calculate the offset based on the page and size
        int offset = (page - 1) * size;
        List<ItemResponseObject> itemResponseObjectList=new ArrayList<>();

        Optional<List<Item>> existingItems=itemRepository.findByIsDeletedFalse();
        List<Item> items=new ArrayList<>();
        if(existingItems.isPresent()){
            items=existingItems.get();
        }

        // Calculate the total number of items
        long totalItems = items.size();
        // Check if the offset exceeds the total number of items
        if (offset >= items.size()) {
            // Return an empty list if there are no more items to retrieve
            return ItemResponse.builder()
                    .message("fetch items successfully")
                    .itemList(Collections.emptyList())
                    .totalItems(totalItems)
                    .build();
        }

// Calculate the end index based on the offset and size
        int endIndex = Math.min(offset + size, items.size());

        // Extract a sublist based on the offset and size
        List<Item> paginatedItems = items.subList(offset, endIndex);

        if(paginatedItems.isEmpty()){
            return ItemResponse.builder()
                    .message("fetch items successfully")
                    .itemList(Collections.emptyList())
                    .build();
        }

        for(Item item : paginatedItems){
            Optional<List<ItemPicture>> existingImages=itemPictureRepository.findAllByItemId(item.getId());
            List<ItemPicture> itemPictureArray=new ArrayList<>();
            if(existingImages.isPresent()){
                itemPictureArray=existingImages.get();
            }
            List<String> itemUrls=new ArrayList<>();
            for(ItemPicture itemPicture: itemPictureArray){
                itemUrls.add(itemPicture.getUrl());
            }

            ItemResponseObject itemResponseObject= ItemResponseObject.builder()
                    .id(item.getId())
                    .title(item.getTitle())
                    .price(item.getPrice())
                    .des(item.getDes())
                    .categoryId(item.getCategoryId())
                    .userId(item.getUserId())
                    .isDelete(item.isDeleted())
                    .stockQuantity(item.getStockQuantity())
                    .imageArray(itemUrls)
                    .build();
            itemResponseObjectList.add(itemResponseObject);


        }
        return ItemResponse.builder()
                .message("fetch items successfully")
                .itemList(itemResponseObjectList)
                .totalItems(totalItems)
                .build();

    }


    public Response deleteItem(HttpServletRequest request, Long itemId) {

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

      Optional <Item> existingItem=itemRepository.findById(itemId);
        if(existingItem.isEmpty()){
            return Response.builder()
                    .message("Item not found")
                    .build();
        }
        Item item=existingItem.get();

        if(!Objects.equals(item.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }

        Optional <List<ItemPicture>> existingItemPictures=itemPictureRepository.findAllByItemId(item.getId());
        if(existingItemPictures.isPresent()){
           List <ItemPicture> itemPictures=existingItemPictures.get();
           itemPictureRepository.deleteAll(itemPictures);
        }
        itemRepository.deleteById(item.getId());

        Optional<Item> deletedItem=itemRepository.findById(itemId);
        if(deletedItem.isEmpty()){
            return Response.builder()
                    .message("Item deleted successfully")
                    .build();
        }else {
            return Response.builder()
                    .message("Failed to delete")
                    .build();
        }






    }

    public Response softDeleteItem(HttpServletRequest request, Long itemId) {
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

        Optional <Item> existingItem=itemRepository.findById(itemId);
        if(existingItem.isEmpty()){
            return Response.builder()
                    .message("Item not found")
                    .build();
        }
        Item item=existingItem.get();

        if(!Objects.equals(item.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        item.setDeleted(true);
        Item updatedItem=itemRepository.save(item);
        if(updatedItem.isDeleted()){
            return Response.builder()
                    .message("Item deleted successfully")
                    .build();
        }else{
            return Response.builder()
                    .message("failed to delete")
                    .build();
        }

    }

    public Response softDeleteRecoverItem(HttpServletRequest request, Long itemId) {
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

        Optional <Item> existingItem=itemRepository.findById(itemId);
        if(existingItem.isEmpty()){
            return Response.builder()
                    .message("Item not found")
                    .build();
        }
        Item item=existingItem.get();

        if(!Objects.equals(item.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        item.setDeleted(false);
        Item updatedItem=itemRepository.save(item);
        if(!updatedItem.isDeleted()){
            return Response.builder()
                    .message("Item recovered successfully")
                    .build();
        }else{
            return Response.builder()
                    .message("failed to delete")
                    .build();
        }
    }

    public Response updateItem(HttpServletRequest request, Long itemId, UpdateItemRequest updateItemRequest) {
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

        Optional <Item> existingItem=itemRepository.findById(itemId);
        if(existingItem.isEmpty()){
            return Response.builder()
                    .message("Item not found")
                    .build();
        }
        Item item=existingItem.get();

        if(!Objects.equals(item.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }
        if(updateItemRequest.getCategoryId() == null ||
                updateItemRequest.getTitle() == null ||
                updateItemRequest.getTitle().isEmpty() ||
                updateItemRequest.getDes() == null ||
                updateItemRequest.getDes().isEmpty() ||
                updateItemRequest.getPrice() == null ||
                updateItemRequest.getImageArray().isEmpty())
        {
            return Response.builder()
                    .message("All fields are required")
                    .build();

        }
        item.setTitle(updateItemRequest.getTitle());
        item.setDes(updateItemRequest.getDes());
        item.setPrice(updateItemRequest.getPrice());
        item.setStockQuantity(updateItemRequest.getStockQuantity());
        item.setCategoryId(updateItemRequest.getCategoryId());
        Item updatedItem=itemRepository.save(item);

        List<String> imageArray=updateItemRequest.getImageArray();
        for(String image:imageArray){
            ItemPicture itemPicture=new ItemPicture();
            itemPicture.setUrl(image);
            itemPicture.setItemId(itemId);
            itemPictureRepository.save(itemPicture);
        }
        if(Objects.equals(updatedItem.getTitle(), updateItemRequest.getTitle()) &&
                Objects.equals(updatedItem.getDes(), updateItemRequest.getDes()) &&
                Objects.equals(updatedItem.getPrice(), updateItemRequest.getPrice()) &&
                Objects.equals(updatedItem.getCategoryId(), updateItemRequest.getCategoryId()) &&
        updatedItem.getStockQuantity()==updateItemRequest.getStockQuantity()){
            return Response.builder()
                    .message("Item updated successfully")
                    .build();
        }else{
            return Response.builder()
                    .message("Failed to update")
                    .build();
        }

    }

    public Response deleteItemPicture(HttpServletRequest request, Long pictureId) {
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

        Optional<ItemPicture> existingItemPicture=itemPictureRepository.findById(pictureId);
        if(existingItemPicture.isEmpty()){
            return Response.builder()
                    .message("Picture not found")
                    .build();
        }
        ItemPicture itemPicture=existingItemPicture.get();
        Optional<Item> itemOptional=itemRepository.findById(itemPicture.getItemId());
        if(itemOptional.isEmpty()){
            return Response.builder()
                    .message("Unable to authorize")
                    .build();
        }
        Item item=itemOptional.get();
        if(!Objects.equals(item.getUserId(), user.getId())){
            return Response.builder()
                    .message("User not authorized")
                    .build();
        }

        itemPictureRepository.deleteById(pictureId);
        Optional<ItemPicture> deletedItemOptional=itemPictureRepository.findById(pictureId);
        if(deletedItemOptional.isEmpty()){
            return Response.builder()
                    .message("Item Picture Successfully deleted")
                    .build();
        }
        return Response.builder()
                .message("Unable to delete")
                .build();



    }
}
