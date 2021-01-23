package tech.jhipster.sample.service;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.sample.service.dto.BlogDTO;

/**
 * Service Interface for managing {@link tech.jhipster.sample.domain.Blog}.
 */
public interface BlogService {
    /**
     * Save a blog.
     *
     * @param blogDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<BlogDTO> save(BlogDTO blogDTO);

    /**
     * Partially updates a blog.
     *
     * @param blogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<BlogDTO> partialUpdate(BlogDTO blogDTO);

    /**
     * Get all the blogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<BlogDTO> findAll(Pageable pageable);

    /**
     * Returns the number of blogs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" blog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<BlogDTO> findOne(Long id);

    /**
     * Delete the "id" blog.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
