package com.heraco.hera.service;

import com.heraco.hera.service.dto.OrderDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Order.
 */
public interface OrderService {

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save
     * @return the persisted entity
     */
    OrderDTO save(OrderDTO orderDTO);

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OrderDTO> findAll(Pageable pageable);


    /**
     * Get the "id" order.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<OrderDTO> findOne(String id);

    /**
     * Delete the "id" order.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the order corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    // Page<OrderDTO> search(String query, Pageable pageable);

    /**
     * Search for the orders who belongs to user.
     *
     * @param user the user from who we want to display the orders;
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OrderDTO> findOrdersByUser(String user, Pageable pageable);
}
