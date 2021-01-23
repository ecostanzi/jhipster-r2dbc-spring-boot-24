package tech.jhipster.sample.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.jhipster.sample.IntegrationTest;
import tech.jhipster.sample.domain.Blog;
import tech.jhipster.sample.repository.BlogRepository;
import tech.jhipster.sample.service.EntityManager;
import tech.jhipster.sample.service.dto.BlogDTO;
import tech.jhipster.sample.service.mapper.BlogMapper;

/**
 * Integration tests for the {@link BlogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BlogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Blog blog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Blog createEntity(EntityManager em) {
        Blog blog = new Blog().name(DEFAULT_NAME);
        return blog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Blog createUpdatedEntity(EntityManager em) {
        Blog blog = new Blog().name(UPDATED_NAME);
        return blog;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Blog.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        blog = createEntity(em);
    }

    @Test
    void createBlog() throws Exception {
        int databaseSizeBeforeCreate = blogRepository.findAll().collectList().block().size();
        // Create the Blog
        BlogDTO blogDTO = blogMapper.toDto(blog);
        webTestClient
            .post()
            .uri("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(blogDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeCreate + 1);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createBlogWithExistingId() throws Exception {
        // Create the Blog with an existing ID
        blog.setId(1L);
        BlogDTO blogDTO = blogMapper.toDto(blog);

        int databaseSizeBeforeCreate = blogRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(blogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllBlogs() {
        // Initialize the database
        blogRepository.save(blog).block();

        // Get all the blogList
        webTestClient
            .get()
            .uri("/api/blogs?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(blog.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getBlog() {
        // Initialize the database
        blogRepository.save(blog).block();

        // Get the blog
        webTestClient
            .get()
            .uri("/api/blogs/{id}", blog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(blog.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingBlog() {
        // Get the blog
        webTestClient
            .get()
            .uri("/api/blogs/{id}", Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void updateBlog() throws Exception {
        // Initialize the database
        blogRepository.save(blog).block();

        int databaseSizeBeforeUpdate = blogRepository.findAll().collectList().block().size();

        // Update the blog
        Blog updatedBlog = blogRepository.findById(blog.getId()).block();
        updatedBlog.name(UPDATED_NAME);
        BlogDTO blogDTO = blogMapper.toDto(updatedBlog);

        webTestClient
            .put()
            .uri("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(blogDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void updateNonExistingBlog() throws Exception {
        int databaseSizeBeforeUpdate = blogRepository.findAll().collectList().block().size();

        // Create the Blog
        BlogDTO blogDTO = blogMapper.toDto(blog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri("/api/blogs")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(blogDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBlogWithPatch() throws Exception {
        // Initialize the database
        blogRepository.save(blog).block();

        int databaseSizeBeforeUpdate = blogRepository.findAll().collectList().block().size();

        // Update the blog using partial update
        Blog partialUpdatedBlog = new Blog();
        partialUpdatedBlog.setId(blog.getId());

        webTestClient
            .patch()
            .uri("/api/blogs")
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBlog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void fullUpdateBlogWithPatch() throws Exception {
        // Initialize the database
        blogRepository.save(blog).block();

        int databaseSizeBeforeUpdate = blogRepository.findAll().collectList().block().size();

        // Update the blog using partial update
        Blog partialUpdatedBlog = new Blog();
        partialUpdatedBlog.setId(blog.getId());

        partialUpdatedBlog.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri("/api/blogs")
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBlog))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Blog in the database
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeUpdate);
        Blog testBlog = blogList.get(blogList.size() - 1);
        assertThat(testBlog.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void partialUpdateBlogShouldThrown() throws Exception {
        // Update the blog without id should throw
        Blog partialUpdatedBlog = new Blog();

        webTestClient
            .patch()
            .uri("/api/blogs")
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBlog))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void deleteBlog() {
        // Initialize the database
        blogRepository.save(blog).block();

        int databaseSizeBeforeDelete = blogRepository.findAll().collectList().block().size();

        // Delete the blog
        webTestClient
            .delete()
            .uri("/api/blogs/{id}", blog.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Blog> blogList = blogRepository.findAll().collectList().block();
        assertThat(blogList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
