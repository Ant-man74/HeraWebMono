package com.heraco.hera.service;

import com.heraco.hera.service.dto.ProductDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Product.
 */
public interface ProductService {

    /**
     * Save a product.
     *
     * @param productDTO the entity to save
     * @return the persisted entity
     */
    ProductDTO save(ProductDTO productDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProductDTO> findAll(Pageable pageable);


    /**
     * Get the "id" product.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ProductDTO> findOne(String id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the product corresponding to the query.
     *
     * @param query    the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    // Page<ProductDTO> search(String query, Pageable pageable);

    /**
     * Search for the product corresponding to the category.
     *
     * @param category the category to fetch
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProductDTO> findCategory(String categories, Pageable pageable);

    public Page<ProductDTO> findByBasket(List<String> id, Pageable pageable);

    public Page<ProductDTO> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    public Page<ProductDTO> findByCategoriesInAndNameIgnoreCaseContainingAndPriceBetween(List<String> categories, String name, double from, double to, Pageable pageable);


}
