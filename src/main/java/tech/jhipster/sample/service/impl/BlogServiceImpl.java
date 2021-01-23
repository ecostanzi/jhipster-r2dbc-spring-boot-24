package tech.jhipster.sample.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.sample.domain.Blog;
import tech.jhipster.sample.repository.BlogRepository;
import tech.jhipster.sample.service.BlogService;
import tech.jhipster.sample.service.dto.BlogDTO;
import tech.jhipster.sample.service.mapper.BlogMapper;

/**
 * Service Implementation for managing {@link Blog}.
 */
@Service
@Transactional
public class BlogServiceImpl implements BlogService {

    private final Logger log = LoggerFactory.getLogger(BlogServiceImpl.class);

    private final BlogRepository blogRepository;

    private final BlogMapper blogMapper;

    public BlogServiceImpl(BlogRepository blogRepository, BlogMapper blogMapper) {
        this.blogRepository = blogRepository;
        this.blogMapper = blogMapper;
    }

    @Override
    public Mono<BlogDTO> save(BlogDTO blogDTO) {
        log.debug("Request to save Blog : {}", blogDTO);
        return blogRepository.save(blogMapper.toEntity(blogDTO)).map(blogMapper::toDto);
    }

    @Override
    public Mono<BlogDTO> partialUpdate(BlogDTO blogDTO) {
        log.debug("Request to partially update Blog : {}", blogDTO);

        return blogRepository
            .findById(blogDTO.getId())
            .map(
                existingBlog -> {
                    if (blogDTO.getName() != null) {
                        existingBlog.setName(blogDTO.getName());
                    }

                    return existingBlog;
                }
            )
            .flatMap(blogRepository::save)
            .map(blogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<BlogDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Blogs");
        return blogRepository.findAllBy(pageable).map(blogMapper::toDto);
    }

    public Mono<Long> countAll() {
        return blogRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BlogDTO> findOne(Long id) {
        log.debug("Request to get Blog : {}", id);
        return blogRepository.findById(id).map(blogMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Blog : {}", id);
        return blogRepository.deleteById(id);
    }
}
