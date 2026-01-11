package com.itemrecovery.service;

import com.itemrecovery.dto.ItemResponse;
import com.itemrecovery.model.FoundItem;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.LostItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for matching lost and found items.
 * Suggests potential matches based on description similarity, location, and date proximity.
 */
@Service
@Transactional
public class MatchService {
    
    @Autowired
    private LostItemService lostItemService;
    
    @Autowired
    private FoundItemService foundItemService;

    /**
     * Find potential matches for a lost item.
     * @param lostItemId the lost item ID
     * @return list of potential matches (found items)
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> findMatchesForLostItem(Long lostItemId) {
        LostItem lostItem = lostItemService.getLostItemById(lostItemId);
        List<FoundItem> allFoundItems = foundItemService.getAllFoundItems();
        
        List<ItemResponse> matches = new ArrayList<>();
        
        for (FoundItem foundItem : allFoundItems) {
            // Skip if already matched or recovered
            if (foundItem.getStatus() == ItemStatus.MATCHED || 
                foundItem.getStatus() == ItemStatus.RECOVERED) {
                continue;
            }
            
            double score = calculateMatchScore(lostItem, foundItem);
            
            // If match score is above threshold, add to matches
            if (score >= 0.5) {
                ItemResponse response = foundItemService.toItemResponse(foundItem);
                matches.add(response);
            }
        }
        
        // Sort by match score (descending)
        matches.sort((a, b) -> {
            // Simple sorting - could be enhanced with actual score storage
            return 0;
        });
        
        return matches;
    }

    /**
     * Find potential matches for a found item.
     * @param foundItemId the found item ID
     * @return list of potential matches (lost items)
     */
    @Transactional(readOnly = true)
    public List<ItemResponse> findMatchesForFoundItem(Long foundItemId) {
        FoundItem foundItem = foundItemService.getFoundItemById(foundItemId);
        List<LostItem> allLostItems = lostItemService.getAllLostItems();
        
        List<ItemResponse> matches = new ArrayList<>();
        
        for (LostItem lostItem : allLostItems) {
            // Skip if already matched or recovered
            if (lostItem.getStatus() == ItemStatus.MATCHED || 
                lostItem.getStatus() == ItemStatus.RECOVERED) {
                continue;
            }
            
            double score = calculateMatchScore(lostItem, foundItem);
            
            // If match score is above threshold, add to matches
            if (score >= 0.5) {
                ItemResponse response = lostItemService.toItemResponse(lostItem);
                matches.add(response);
            }
        }
        
        return matches;
    }

    /**
     * Calculate match score between a lost item and found item.
     * Score is based on:
     * - Description similarity (50%)
     * - Location similarity (30%)
     * - Date proximity (20%)
     * @param lostItem the lost item
     * @param foundItem the found item
     * @return match score between 0.0 and 1.0
     */
    private double calculateMatchScore(LostItem lostItem, FoundItem foundItem) {
        double descriptionScore = calculateDescriptionSimilarity(
            lostItem.getDescription().toLowerCase(),
            foundItem.getDescription().toLowerCase()
        );
        
        double locationScore = calculateLocationSimilarity(
            lostItem.getLocation().toLowerCase(),
            foundItem.getLocation().toLowerCase()
        );
        
        double dateScore = calculateDateProximity(
            lostItem.getDate(),
            foundItem.getDate()
        );
        
        // Weighted average
        return (descriptionScore * 0.5) + (locationScore * 0.3) + (dateScore * 0.2);
    }

    /**
     * Calculate description similarity using simple word matching.
     * @param desc1 first description
     * @param desc2 second description
     * @return similarity score between 0.0 and 1.0
     */
    private double calculateDescriptionSimilarity(String desc1, String desc2) {
        String[] words1 = desc1.split("\\s+");
        String[] words2 = desc2.split("\\s+");
        
        int matches = 0;
        int totalWords = Math.max(words1.length, words2.length);
        
        for (String word1 : words1) {
            if (word1.length() > 3) { // Only consider words longer than 3 characters
                for (String word2 : words2) {
                    if (word2.length() > 3 && word1.equals(word2)) {
                        matches++;
                        break;
                    }
                }
            }
        }
        
        return totalWords > 0 ? (double) matches / totalWords : 0.0;
    }

    /**
     * Calculate location similarity.
     * @param loc1 first location
     * @param loc2 second location
     * @return similarity score between 0.0 and 1.0
     */
    private double calculateLocationSimilarity(String loc1, String loc2) {
        if (loc1.equals(loc2)) {
            return 1.0;
        }
        
        // Check if locations contain common words
        String[] words1 = loc1.split("\\s+");
        String[] words2 = loc2.split("\\s+");
        
        int commonWords = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.length() > 2 && word1.equals(word2)) {
                    commonWords++;
                    break;
                }
            }
        }
        
        int totalWords = Math.max(words1.length, words2.length);
        return totalWords > 0 ? (double) commonWords / totalWords : 0.0;
    }

    /**
     * Calculate date proximity score.
     * Closer dates have higher scores.
     * @param date1 first date
     * @param date2 second date
     * @return proximity score between 0.0 and 1.0
     */
    private double calculateDateProximity(LocalDate date1, LocalDate date2) {
        long daysDiff = Math.abs(ChronoUnit.DAYS.between(date1, date2));
        
        // Score decreases as days difference increases
        // Within 7 days: score 1.0
        // Within 30 days: score 0.7
        // Within 90 days: score 0.4
        // More than 90 days: score 0.1
        if (daysDiff <= 7) {
            return 1.0;
        } else if (daysDiff <= 30) {
            return 0.7;
        } else if (daysDiff <= 90) {
            return 0.4;
        } else {
            return 0.1;
        }
    }

    /**
     * Mark items as matched.
     * @param lostItemId the lost item ID
     * @param foundItemId the found item ID
     */
    public void markAsMatched(Long lostItemId, Long foundItemId) {
        lostItemService.updateStatus(lostItemId, ItemStatus.MATCHED);
        foundItemService.updateStatus(foundItemId, ItemStatus.MATCHED);
    }
}
