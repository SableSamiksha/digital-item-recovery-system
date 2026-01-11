package com.itemrecovery.repository;

import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.LostItem;
import com.itemrecovery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for LostItem entity.
 * Provides database operations for lost item management.
 */
@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    
    /**
     * Find all lost items by user
     * @param user the user who reported the items
     * @return list of lost items for the user
     */
    List<LostItem> findByUser(User user);
    
    /**
     * Find all lost items by status
     * @param status the status to filter by
     * @return list of lost items with the specified status
     */
    List<LostItem> findByStatus(ItemStatus status);
    
    /**
     * Find all lost items by user and status
     * @param user the user who reported the items
     * @param status the status to filter by
     * @return list of lost items for the user with the specified status
     */
    List<LostItem> findByUserAndStatus(User user, ItemStatus status);
}
