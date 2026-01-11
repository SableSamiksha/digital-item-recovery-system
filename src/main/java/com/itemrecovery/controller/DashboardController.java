package com.itemrecovery.controller;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.User;
import com.itemrecovery.service.FoundItemService;
import com.itemrecovery.service.LostItemService;
import com.itemrecovery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller for dashboard operations.
 * Displays user dashboard with their lost and found items.
 */
@Controller
public class DashboardController {
    
    @Autowired
    private LostItemService lostItemService;
    
    @Autowired
    private FoundItemService foundItemService;
    
    @Autowired
    private UserService userService;

    /**
     * Display user dashboard.
     * Shows user's lost and found items.
     * @param model the model
     * @return dashboard page template name
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long userId = getCurrentUserId();
        
        // Get user's lost items
        List<com.itemrecovery.model.LostItem> lostItems = lostItemService.getLostItemsByUser(userId);
        List<ItemResponse> lostItemResponses = lostItemService.toItemResponseList(lostItems);
        
        // Get user's found items
        List<com.itemrecovery.model.FoundItem> foundItems = foundItemService.getFoundItemsByUser(userId);
        List<ItemResponse> foundItemResponses = foundItemService.toItemResponseList(foundItems);
        
        model.addAttribute("lostItems", lostItemResponses);
        model.addAttribute("foundItems", foundItemResponses);
        
        return "dashboard";
    }

    /**
     * Get current authenticated user ID.
     * @return user ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }
}
