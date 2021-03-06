package com.heraco.hera.service.impl;

import com.heraco.hera.service.MailService;
import com.heraco.hera.service.OrderService;
import com.heraco.hera.domain.Order;
import com.heraco.hera.repository.OrderRepository;
import com.heraco.hera.service.dto.OrderDTO;
import com.heraco.hera.service.error.InvalidOrderException;
import com.heraco.hera.service.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service Implementation for managing Order.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final MailService mailService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, MailService mailService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.mailService = mailService;
    }

    /**
     * Save a order.
     *
     * @param orderDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        OrderDTO result = null;
            order = orderRepository.save(order);
            result = orderMapper.toDto(order);
            this.mailService.sendOrderConfirmationMail(result);
        return result;
    }

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    /**
     * Get one order by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<OrderDTO> findOne(String id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    /**
     * Delete the order by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    /**
     * Search for the order corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     *//*
        * @Override public Page<OrderDTO> search(String query, Pageable pageable) {
        * log.debug("Request to search for a page of Orders for query {}", query);
        * return orderSearchRepository.search(queryStringQuery(query), pageable)
        * .map(orderMapper::toDto); }
        */

    /**
     * Search for the product corresponding to the query.
     *
     * @param query    the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<OrderDTO> findOrdersByUser(String user, Pageable pageable) {
        log.debug("Request to search for a page of Order for query {}", user);
        return orderRepository.findAllByUserOrderByDateDesc(pageable, user).map(orderMapper::toDto);
    }

    public boolean checkOrderValidity(OrderDTO order){
        boolean valid = true;
        if(order.getAddress() == null)
            valid = false;
        else if(order.getPaymentMethod() == null)
            valid = false;
        else if(order.getTransportationMethod() == null)
            valid = false;
        else if(order.getOrderLine() == null)
            valid = false;

        return valid;
    }
}
