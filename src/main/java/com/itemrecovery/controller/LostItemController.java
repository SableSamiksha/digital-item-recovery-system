package com.itemrecovery.controller;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.LostItem;
import com.itemrecovery.model.User;
import com.itemrecovery.service.LostItemService;
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
 * Controller for lost item operations.
 * Handles reporting, viewing, and managing lost items.
 */
@Controller
@RequestMapping("/lost-items")
public class LostItemController {
    
    @Autowired
    private LostItemService lostItemService;
    
    @Autowired
    private UserService userService;

    /**
     * Display form to report a lost item.
     * @param model the model
     * @return report-lost page template name
     */
    @GetMapping("/report")
    public String reportLostItemPage(Model model) {
        return "report-lost";
    }

    /**
     * Handle lost item submission.
     * @param name item name
     * @param description item description
     * @param date date lost
     * @param location location where item was lost
     * @param contact contact details
     * @param imageFile uploaded image file
     * @param redirectAttributes redirect attributes
     * @return redirect to dashboard on success
     */
    @PostMapping("/report")
    public String reportLostItem(@RequestParam String name,
                                 @RequestParam String description,
                                 @RequestParam LocalDate date,
                                 @RequestParam String location,
                                 @RequestParam String contact,
                                 @RequestParam(required = false) MultipartFile imageFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            lostItemService.createLostItem(name, description, date, location, contact, imageFile, userId);
            redirectAttributes.addFlashAttribute("message", "Lost item reported successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error reporting lost item: " + e.getMessage());
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
     * View all lost items.
     * @param model the model
     * @return view-items page template name
     */
    @GetMapping("/view")
    public String viewLostItems(Model model) {
        List<LostItem> items = lostItemService.getAllLostItems();
        List<ItemResponse> itemResponses = lostItemService.toItemResponseList(items);
        model.addAttribute("items", itemResponses);
        model.addAttribute("itemType", "lost");
        return "view-items";
    }

    /**
     * Delete a lost item.
     * @param id the item ID
     * @param redirectAttributes redirect attributes
     * @return redirect to dashboard
     */
    @PostMapping("/delete/{id}")
    public String deleteLostItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            lostItemService.deleteLostItem(id, userId);
            redirectAttributes.addFlashAttribute("message", "Lost item deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting image: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
