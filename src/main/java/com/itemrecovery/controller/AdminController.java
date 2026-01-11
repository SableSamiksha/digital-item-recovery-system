package com.itemrecovery.controller;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.service.FoundItemService;
import com.itemrecovery.service.LostItemService;
import com.itemrecovery.service.MatchService;
import com.itemrecovery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for admin operations.
 * Handles admin dashboard, item approval, rejection, and recovery marking.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private LostItemService lostItemService;
    
    @Autowired
    private FoundItemService foundItemService;
    
    @Autowired
    private MatchService matchService;
    
    @Autowired
    private UserService userService;

    /**
     * Display admin dashboard.
     * Shows all lost and found items for admin review.
     * @param model the model
     * @return admin-dashboard page template name
     */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get all lost items
        List<com.itemrecovery.model.LostItem> lostItems = lostItemService.getAllLostItems();
        List<ItemResponse> lostItemResponses = lostItemService.toItemResponseList(lostItems);
        
        // Get all found items
        List<com.itemrecovery.model.FoundItem> foundItems = foundItemService.getAllFoundItems();
        List<ItemResponse> foundItemResponses = foundItemService.toItemResponseList(foundItems);
        
        // Combine all items
        List<ItemResponse> allItems = new ArrayList<>();
        allItems.addAll(lostItemResponses);
        allItems.addAll(foundItemResponses);
        
        model.addAttribute("items", allItems);
        
        return "admin-dashboard";
    }

    /**
     * Update item status (approve/reject/recover).
     * @param itemType the item type (lost or found)
     * @param id the item ID
     * @param status the new status
     * @param redirectAttributes redirect attributes
     * @return redirect to admin dashboard
     */
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String itemType,
                               @RequestParam Long id,
                               @RequestParam ItemStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            if ("lost".equalsIgnoreCase(itemType)) {
                lostItemService.updateStatus(id, status);
            } else if ("found".equalsIgnoreCase(itemType)) {
                foundItemService.updateStatus(id, status);
            }
            redirectAttributes.addFlashAttribute("message", "Item status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating status: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * Delete an item (admin only).
     * @param itemType the item type (lost or found)
     * @param id the item ID
     * @param redirectAttributes redirect attributes
     * @return redirect to admin dashboard
     */
    @PostMapping("/delete-item")
    public String deleteItem(@RequestParam String itemType,
                            @RequestParam Long id,
                            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            if ("lost".equalsIgnoreCase(itemType)) {
                lostItemService.deleteLostItem(id, userId);
            } else if ("found".equalsIgnoreCase(itemType)) {
                foundItemService.deleteFoundItem(id, userId);
            }
            redirectAttributes.addFlashAttribute("message", "Item deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting item: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * Get current authenticated user ID.
     * @return user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        com.itemrecovery.model.User user = userService.findByUsername(username);
        return user.getId();
    }
}
