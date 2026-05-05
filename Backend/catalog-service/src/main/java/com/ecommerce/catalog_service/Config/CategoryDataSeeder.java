package com.ecommerce.catalog_service.Config;

import com.ecommerce.catalog_service.Domain.Category;
import com.ecommerce.catalog_service.Repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Seeds default categories on startup if the collection is empty.
 * Idempotent - skips seeding if any categories already exist.
 */
@Component
@Order(1)
public class CategoryDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategoryDataSeeder.class);

    private final CategoryRepository categoryRepository;

    public CategoryDataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        categoryRepository.count().subscribe(count -> {
            if (count > 0) {
                log.info("Categories already seeded ({} found). Skipping.", count);
                latch.countDown();
                return;
            }

            log.info("No categories found. Seeding defaults...");

            List<Category> defaults = List.of(
                    category("Electronics", "electronics", "Electronic devices and accessories"),
                    category("Clothing", "clothing", "Apparel and fashion accessories"),
                    category("Home & Garden", "home-garden", "Home furnishings and garden supplies"),
                    category("Books", "books", "Books, eBooks, and audiobooks"),
                    category("Sports & Outdoors", "sports-outdoors", "Sports equipment and outdoor gear")
            );

            categoryRepository.saveAll(defaults)
                    .collectList()
                    .subscribe(
                            saved -> {
                                log.info("Seeded {} default categories.", saved.size());
                                latch.countDown();
                            },
                            err -> {
                                log.error("Failed to seed categories", err);
                                latch.countDown();
                            }
                    );

        }, err -> {
            log.error("Failed to count categories", err);
            latch.countDown();
        });

        latch.await(10, TimeUnit.SECONDS);
    }

    private static Category category(String name, String slug, String description) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDescription(description);
        c.setActive(true);
        c.setCreatedAt(Instant.now());
        return c;
    }
}
