package com.itemrecovery.repository;

import com.itemrecovery.model.FoundItem;
import com.itemrecovery.model.ItemStatus;
import com.itemrecovery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for FoundItem entity.
 * Provides database operations for found item management.
 */
@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {
    
    /**
     * Find all found items by user
     * @param user the user who reported the items
     * @return list of found items for the user
     */
    List<FoundItem> findByUser(User user);
    
    /**
     * Find all found items by status
     * @param status the status to filter by
     * @return list of found items with the specified status
     */
    List<FoundItem> findByStatus(ItemStatus status);
    
    /**
     * Find all found items by user and status
     * @param user the user who reported the items
     * @param status the status to filter by
     * @return list of found items for the user with the specified status
     */
    List<FoundItem> findByUserAndStatus(User user, ItemStatus status);
}
