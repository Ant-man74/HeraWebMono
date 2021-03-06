package com.heraco.hera.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.heraco.hera.domain.BasketItem;
import com.heraco.hera.domain.Product;
import com.heraco.hera.service.MailService;
import com.heraco.hera.service.OrderService;
import com.heraco.hera.service.PDFService;
import com.heraco.hera.service.ProductService;
import com.heraco.hera.web.rest.errors.BadRequestAlertException;
import com.heraco.hera.web.rest.util.HeaderUtil;
import com.heraco.hera.web.rest.util.PaginationUtil;
import com.heraco.hera.service.dto.OrderAndProductsDTO;
import com.heraco.hera.service.dto.OrderDTO;
import com.heraco.hera.service.dto.ProductDTO;

import io.github.jhipster.web.util.ResponseUtil;

import org.bson.internal.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;

/**
 * REST controller for managing Order.
 */
@RestController
@RequestMapping("/api")
public class OrderResource {

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);

    private static final String ENTITY_NAME = "order";

    private final OrderService orderService;

    private MailService mailService;

    private final PDFService pdfService;

    private final ProductService productService;

    public OrderResource(OrderService orderService, MailService mailService, PDFService pdfService, ProductService p) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.pdfService = pdfService;
        this.productService = p;

    }

    /**
     * POST /orders : Create a new order.
     *
     * @param orderDTO the orderDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new
     *         orderDTO, or with status 400 (Bad Request) if the order has already
     *         an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/orders")
    @Timed
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) throws URISyntaxException {
        log.debug("REST request to save Order : {}", orderDTO);
        if (orderDTO.getId() != null) {
            throw new BadRequestAlertException("A new order cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderDTO result = orderService.save(orderDTO);
        mailService.sendOrderConfirmationMail(result);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString())).body(result);
    }

    /**
     * PUT /orders : Updates an existing order.
     *
     * @param orderDTO the orderDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated
     *         orderDTO, or with status 400 (Bad Request) if the orderDTO is not
     *         valid, or with status 500 (Internal Server Error) if the orderDTO
     *         couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/orders")
    @Timed
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody OrderDTO orderDTO) throws URISyntaxException {
        log.debug("REST request to update Order : {}", orderDTO);
        if (orderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OrderDTO result = orderService.save(orderDTO);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderDTO.getId().toString()))
                .body(result);
    }

    /**
     * GET /orders : get all the orders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of orders in
     *         body
     */
    @GetMapping("/orders")
    @Timed
    public ResponseEntity<List<OrderDTO>> getAllOrders(Pageable pageable) {
        log.debug("REST request to get a page of Orders");
        Page<OrderDTO> page = orderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET /orders/:id : get the "id" order.
     *
     * @param id the id of the orderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderDTO,
     *         or with status 404 (Not Found)
     */
    @GetMapping("/orders/{id}")
    @Timed
    public ResponseEntity<OrderDTO> getOrder(@PathVariable String id) {
        log.debug("REST request to get Order : {}", id);
        Optional<OrderDTO> orderDTO = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderDTO);
    }

    /**
     * GET /orders/pdf/:id : get the pdf for "id" order.
     *
     * @param id the id of the orderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the byte array
     *         pdf, or with status 404 (Not Found)
     */
    @GetMapping("/orders/pdf/{id}")
    @Timed
    public ResponseEntity<byte[]> getPDFOrder(@PathVariable String id) {
        log.debug("REST request to get PDF for Order : {}", id);
        Optional<OrderDTO> orderDTO = orderService.findOne(id);
        byte[] result = pdfService.generatePDF(orderDTO.get());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + orderDTO.get().getId() + ".pdf");
        headers.setContentLength(result.length);
        return new ResponseEntity<byte[]>(result, headers, HttpStatus.OK);
    }

    /**
     * DELETE /orders/:id : delete the "id" order.
     *
     * @param id the id of the orderDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        log.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    @GetMapping("/orders/id/{id}")
    @Timed
    public ResponseEntity<OrderAndProductsDTO> getOrderById(@PathVariable String id) {
        log.debug("REST request to get the order and products for query {}", id);
        OrderDTO orderDTO = orderService.findOne(id).get();
        ArrayList<ProductDTO> products = new ArrayList<>();
        for (BasketItem b : orderDTO.getOrderLine()) {
            products.add(productService.findOne(b.getProd()).get());
        }
        List<ProductDTO> cart = products;
        OrderAndProductsDTO ret = new OrderAndProductsDTO();
        ret.setOrder(orderDTO);
        ret.setProducts(cart);
        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @GetMapping("/orders/user/{user}")
    @Timed
    public ResponseEntity<List<OrderAndProductsDTO>> getOrdersByUser(@PathVariable String user, Pageable pageable) {
        log.debug("REST request to search for a page of Order for query {}", user);
        HashMap<String, ProductDTO> map = new HashMap<>();
        Page<OrderDTO> page = orderService.findOrdersByUser(user, pageable);
        ArrayList<OrderAndProductsDTO> ret = new ArrayList<>();
        for (OrderDTO order : page.getContent()) {
            ArrayList<ProductDTO> prods = new ArrayList<>();
            for (BasketItem item : order.getOrderLine()) {
                if (map.get(item.getProd()) == null) {
                    map.put(item.getProd(), this.productService.findOne(item.getProd()).get());
                }
                prods.add(map.get(item.getProd()));
            }
            OrderAndProductsDTO newItem = new OrderAndProductsDTO();
            newItem.setOrder(order);
            newItem.setProducts(prods);
            ret.add(newItem);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/order");
        return new ResponseEntity<>(ret, headers, HttpStatus.OK);
    }

    /**
     * SEARCH /_search/orders?query=:query : search for the order corresponding to
     * the query.
     *
     * @param query    the query of the order search
     * @param pageable the pagination information
     * @return the result of the search
     *//*
        * @GetMapping("/_search/orders")
        * 
        * @Timed public ResponseEntity<List<OrderDTO>> searchOrders(@RequestParam
        * String query, Pageable pageable) {
        * log.debug("REST request to search for a page of Orders for query {}", query);
        * Page<OrderDTO> page = orderService.search(query, pageable); HttpHeaders
        * headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page,
        * "/api/_search/orders"); return new ResponseEntity<>(page.getContent(),
        * headers, HttpStatus.OK); }
        */

}
