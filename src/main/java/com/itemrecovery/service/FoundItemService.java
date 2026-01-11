package com.itemrecovery.service;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.FoundItem;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.User;
import com.itemrecovery.repository.FoundItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for found item operations.
 * Handles CRUD operations for found items.
 */
@Service
@Transactional
public class FoundItemService {
    
    @Autowired
    private FoundItemRepository foundItemRepository;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private UserService userService;

    /**
     * Create a new found item.
     * @param name item name
     * @param description item description
     * @param date date found
     * @param location location where item was found
     * @param contact contact details
     * @param imageFile image file (optional)
     * @param userId user ID who reported the item
     * @return the created found item
     * @throws IOException if image cannot be saved
     */
    public FoundItem createFoundItem(String name, String description, LocalDate date,
                                      String location, String contact, MultipartFile imageFile,
                                      Long userId) throws IOException {
        User user = userService.findById(userId);
        
        FoundItem item = new FoundItem();
        item.setName(name);
        item.setDescription(description);
        item.setDate(date);
        item.setLocation(location);
        item.setContact(contact);
        item.setStatus(ItemStatus.FOUND);
        item.setUser(user);

        // Save image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = imageService.saveFoundItemImage(imageFile);
            item.setImagePath(imagePath);
        }

        return foundItemRepository.save(item);
    }

    /**
     * Get all found items.
     * @return list of all found items
     */
    @Transactional(readOnly = true)
    public List<FoundItem> getAllFoundItems() {
        return foundItemRepository.findAll();
    }

    /**
     * Get found items by user.
     * @param userId the user ID
     * @return list of found items for the user
     */
    @Transactional(readOnly = true)
    public List<FoundItem> getFoundItemsByUser(Long userId) {
        User user = userService.findById(userId);
        return foundItemRepository.findByUser(user);
    }

    /**
     * Get found item by ID.
     * @param id the item ID
     * @return the found item if found
     * @throws IllegalArgumentException if item not found
     */
    @Transactional(readOnly = true)
    public FoundItem getFoundItemById(Long id) {
        return foundItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Found item not found"));
    }

    /**
     * Delete a found item.
     * Also deletes the associated image file.
     * @param id the item ID
     * @param userId the user ID (for authorization check)
     * @throws IllegalArgumentException if item not found or user not authorized
     * @throws IOException if image cannot be deleted
     */
    public void deleteFoundItem(Long id, Long userId) throws IOException {
        FoundItem item = getFoundItemById(id);
        User user = userService.findById(userId);

        // Check if user is authorized (owner or admin)
        if (!item.getUser().getId().equals(userId) && user.getRole() != com.itemrecovery.model.Role.ADMIN) {
            throw new IllegalArgumentException("Not authorized to delete this item");
        }

        // Delete image if exists
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            imageService.deleteImage(item.getImagePath());
        }

        foundItemRepository.delete(item);
    }

    /**
     * Update found item status.
     * @param id the item ID
     * @param status the new status
     */
    public void updateStatus(Long id, ItemStatus status) {
        FoundItem item = getFoundItemById(id);
        item.setStatus(status);
        foundItemRepository.save(item);
    }

    /**
     * Convert FoundItem to ItemResponse.
     * @param item the found item
     * @return ItemResponse DTO
     */
    public ItemResponse toItemResponse(FoundItem item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setDate(item.getDate());
        response.setLocation(item.getLocation());
        response.setContact(item.getContact());
        response.setImagePath(item.getImagePath());
        response.setStatus(item.getStatus());
        response.setUsername(item.getUser().getUsername());
        response.setUserId(item.getUser().getId());
        response.setItemType("FOUND");
        return response;
    }

    /**
     * Convert list of FoundItems to list of ItemResponses.
     * @param items list of found items
     * @return list of ItemResponse DTOs
     */
    public List<ItemResponse> toItemResponseList(List<FoundItem> items) {
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }
}
