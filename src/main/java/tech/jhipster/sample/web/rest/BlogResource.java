package tech.jhipster.sample.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.sample.service.BlogService;
import tech.jhipster.sample.service.dto.BlogDTO;
import tech.jhipster.sample.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link tech.jhipster.sample.domain.Blog}.
 */
@RestController
@RequestMapping("/api")
public class BlogResource {

    private final Logger log = LoggerFactory.getLogger(BlogResource.class);

    private static final String ENTITY_NAME = "blog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlogService blogService;

    public BlogResource(BlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * {@code POST  /blogs} : Create a new blog.
     *
     * @param blogDTO the blogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blogDTO, or with status {@code 400 (Bad Request)} if the blog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/blogs")
    public Mono<ResponseEntity<BlogDTO>> createBlog(@RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to save Blog : {}", blogDTO);
        if (blogDTO.getId() != null) {
            throw new BadRequestAlertException("A new blog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return blogService
            .save(blogDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/blogs/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /blogs} : Updates an existing blog.
     *
     * @param blogDTO the blogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blogDTO,
     * or with status {@code 400 (Bad Request)} if the blogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/blogs")
    public Mono<ResponseEntity<BlogDTO>> updateBlog(@RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to update Blog : {}", blogDTO);
        if (blogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        return blogService
            .save(blogDTO)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
            .map(
                result ->
                    ResponseEntity
                        .ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
            );
    }

    /**
     * {@code PATCH  /blogs} : Updates given fields of an existing blog.
     *
     * @param blogDTO the blogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blogDTO,
     * or with status {@code 400 (Bad Request)} if the blogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the blogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the blogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/blogs", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<BlogDTO>> partialUpdateBlog(@RequestBody BlogDTO blogDTO) throws URISyntaxException {
        log.debug("REST request to update Blog partially : {}", blogDTO);
        if (blogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Mono<BlogDTO> result = blogService.partialUpdate(blogDTO);

        return result
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
            .map(
                res ->
                    ResponseEntity
                        .ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                        .body(res)
            );
    }

    /**
     * {@code GET  /blogs} : get all the blogs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blogs in body.
     */
    @GetMapping("/blogs")
    public Mono<ResponseEntity<List<BlogDTO>>> getAllBlogs(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Blogs");
        return blogService
            .countAll()
            .zipWith(blogService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /blogs/:id} : get the "id" blog.
     *
     * @param id the id of the blogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/blogs/{id}")
    public Mono<ResponseEntity<BlogDTO>> getBlog(@PathVariable Long id) {
        log.debug("REST request to get Blog : {}", id);
        Mono<BlogDTO> blogDTO = blogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blogDTO);
    }

    /**
     * {@code DELETE  /blogs/:id} : delete the "id" blog.
     *
     * @param id the id of the blogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/blogs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteBlog(@PathVariable Long id) {
        log.debug("REST request to delete Blog : {}", id);
        return blogService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
