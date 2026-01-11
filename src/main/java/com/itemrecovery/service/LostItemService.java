package com.itemrecovery.service;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.LostItem;
import com.itemrecovery.model.User;
import com.itemrecovery.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for lost item operations.
 * Handles CRUD operations for lost items.
 */
@Service
@Transactional
public class LostItemService {
    
    @Autowired
    private LostItemRepository lostItemRepository;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private UserService userService;

    /**
     * Create a new lost item.
     * @param name item name
     * @param description item description
     * @param date date lost
     * @param location location where item was lost
     * @param contact contact details
     * @param imageFile image file (optional)
     * @param userId user ID who reported the item
     * @return the created lost item
     * @throws IOException if image cannot be saved
     */
    public LostItem createLostItem(String name, String description, LocalDate date,
                                    String location, String contact, MultipartFile imageFile,
                                    Long userId) throws IOException {
        User user = userService.findById(userId);
        
        LostItem item = new LostItem();
        item.setName(name);
        item.setDescription(description);
        item.setDate(date);
        item.setLocation(location);
        item.setContact(contact);
        item.setStatus(ItemStatus.LOST);
        item.setUser(user);

        // Save image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = imageService.saveLostItemImage(imageFile);
            item.setImagePath(imagePath);
        }

        return lostItemRepository.save(item);
    }

    /**
     * Get all lost items.
     * @return list of all lost items
     */
    @Transactional(readOnly = true)
    public List<LostItem> getAllLostItems() {
        return lostItemRepository.findAll();
    }

    /**
     * Get lost items by user.
     * @param userId the user ID
     * @return list of lost items for the user
     */
    @Transactional(readOnly = true)
    public List<LostItem> getLostItemsByUser(Long userId) {
        User user = userService.findById(userId);
        return lostItemRepository.findByUser(user);
    }

    /**
     * Get lost item by ID.
     * @param id the item ID
     * @return the lost item if found
     * @throws IllegalArgumentException if item not found
     */
    @Transactional(readOnly = true)
    public LostItem getLostItemById(Long id) {
        return lostItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lost item not found"));
    }

    /**
     * Delete a lost item.
     * Also deletes the associated image file.
     * @param id the item ID
     * @param userId the user ID (for authorization check)
     * @throws IllegalArgumentException if item not found or user not authorized
     * @throws IOException if image cannot be deleted
     */
    public void deleteLostItem(Long id, Long userId) throws IOException {
        LostItem item = getLostItemById(id);
        User user = userService.findById(userId);

        // Check if user is authorized (owner or admin)
        if (!item.getUser().getId().equals(userId) && user.getRole() != com.itemrecovery.model.Role.ADMIN) {
            throw new IllegalArgumentException("Not authorized to delete this item");
        }

        // Delete image if exists
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            imageService.deleteImage(item.getImagePath());
        }

        lostItemRepository.delete(item);
    }

    /**
     * Update lost item status.
     * @param id the item ID
     * @param status the new status
     */
    public void updateStatus(Long id, ItemStatus status) {
        LostItem item = getLostItemById(id);
        item.setStatus(status);
        lostItemRepository.save(item);
    }

    /**
     * Convert LostItem to ItemResponse.
     * @param item the lost item
     * @return ItemResponse DTO
     */
    public ItemResponse toItemResponse(LostItem item) {
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
        response.setItemType("LOST");
        return response;
    }

    /**
     * Convert list of LostItems to list of ItemResponses.
     * @param items list of lost items
     * @return list of ItemResponse DTOs
     */
    public List<ItemResponse> toItemResponseList(List<LostItem> items) {
        return items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }
}
