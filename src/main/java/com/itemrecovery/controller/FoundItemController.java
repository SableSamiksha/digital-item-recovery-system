package com.itemrecovery.controller;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.FoundItem;
import com.itemrecovery.model.User;
import com.itemrecovery.service.FoundItemService;
import com.itemrecovery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for found item operations.
 * Handles reporting, viewing, and managing found items.
 */
@Controller
@RequestMapping("/found-items")
public class FoundItemController {
    
    @Autowired
    private FoundItemService foundItemService;
    
    @Autowired
    private UserService userService;

    /**
     * Display form to report a found item.
     * @param model the model
     * @return report-found page template name
     */
    @GetMapping("/report")
    public String reportFoundItemPage(Model model) {
        return "report-found";
    }

    /**
     * Handle found item submission.
     * @param name item name
     * @param description item description
     * @param date date found
     * @param location location where item was found
     * @param contact contact details
     * @param imageFile uploaded image file
     * @param redirectAttributes redirect attributes
     * @return redirect to dashboard on success
     */
    @PostMapping("/report")
    public String reportFoundItem(@RequestParam String name,
                                  @RequestParam String description,
                                  @RequestParam LocalDate date,
                                  @RequestParam String location,
                                  @RequestParam String contact,
                                  @RequestParam(required = false) MultipartFile imageFile,
                                  RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            foundItemService.createFoundItem(name, description, date, location, contact, imageFile, userId);
            redirectAttributes.addFlashAttribute("message", "Found item reported successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error reporting found item: " + e.getMessage());
        }
        return "redirect:/dashboard";
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

    /**
     * View all found items.
     * @param model the model
     * @return view-items page template name
     */
    @GetMapping("/view")
    public String viewFoundItems(Model model) {
        List<FoundItem> items = foundItemService.getAllFoundItems();
        List<ItemResponse> itemResponses = foundItemService.toItemResponseList(items);
        model.addAttribute("items", itemResponses);
        model.addAttribute("itemType", "found");
        return "view-items";
    }

    /**
     * Delete a found item.
     * @param id the item ID
     * @param redirectAttributes redirect attributes
     * @return redirect to dashboard
     */
    @PostMapping("/delete/{id}")
    public String deleteFoundItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            foundItemService.deleteFoundItem(id, userId);
            redirectAttributes.addFlashAttribute("message", "Found item deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting image: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
