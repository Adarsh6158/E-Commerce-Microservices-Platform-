package com.ecommerce.catalog_service.Config;

import com.ecommerce.catalog_service.Domain.Category;
import com.ecommerce.catalog_service.Domain.Product;
import com.ecommerce.catalog_service.Event.ProductEvent;
import com.ecommerce.catalog_service.Event.ProductEventPublisher;
import com.ecommerce.catalog_service.Repository.CategoryRepository;
import com.ecommerce.catalog_service.Repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Seeds realistic product data on startup if the product collection is empty.
 * Publishes Kafka events for each seeded product so search service indexes them.
 * Runs AFTER CategoryDataSeeder (order = 2).
 */
@Component
@Order(2)
public class ProductDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductDataSeeder.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventPublisher eventPublisher;

    public ProductDataSeeder(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             ProductEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        productRepository.count().subscribe(count -> {
            if (count > 0) {
                log.info("Products already seeded ({} found). Skipping product seeder.", count);
                latch.countDown();
                return;
            }

            log.info("No products found. Seeding catalog...");

            categoryRepository.findByActiveTrue().collectList().subscribe(cats -> {
                if (cats.isEmpty()) {
                    log.warn("No categories found. Cannot seed products.");
                    latch.countDown();
                    return;
                }

                String electronicsId = findCategoryId(cats, "electronics");
                String clothingId = findCategoryId(cats, "clothing");
                String homeId = findCategoryId(cats, "home-garden");
                String booksId = findCategoryId(cats, "books");
                String sportsId = findCategoryId(cats, "sports-outdoors");

                List<Product> products = buildProducts(electronicsId, clothingId, homeId, booksId, sportsId);

                Flux.fromIterable(products)
                        .flatMap(p -> productRepository.save(p)
                                        .doOnSuccess(saved -> {
                                            eventPublisher.publish(ProductEvent.created(
                                                    saved.getId(), saved.getSku(), saved.getName(),
                                                    saved.getDescription(), saved.getBrand(),
                                                    saved.getCategoryId(), saved.getBasePrice(),
                                                    saved.getImageUrl(), saved.getAttributes(), "seeder"));
                                        })
                                , 4)
                        .collectList()
                        .subscribe(
                                saved -> {
                                    log.info("Seeded {} products across all categories.", saved.size());
                                    latch.countDown();
                                },
                                err -> {
                                    log.error("Failed to seed products", err);
                                    latch.countDown();
                                }
                        );

            }, err -> {
                log.error("Failed to load categories for seeding", err);
                latch.countDown();
            });

        }, err -> {
            log.error("Failed to count products", err);
            latch.countDown();
        });

        latch.await(30, TimeUnit.SECONDS);
    }

    private String findCategoryId(List<Category> cats, String slug) {
        return cats.stream()
                .filter(c -> slug.equals(c.getSlug()))
                .map(Category::getId)
                .findFirst()
                .orElse(cats.get(0).getId());
    }

    private List<Product> buildProducts(String elec, String cloth, String home, String books, String sports) {
        return List.of(
                // — Electronics —
                product("ELEC-001", "Premium Wireless Headphones",
                        "High-fidelity Bluetooth headphones with active noise cancellation, 30-hour battery life, and premium comfort pads.",
                        elec, "Sony", 299.99, Map.of("color", "Black", "wireless", true, "batteryLife", "30h")),

                product("ELEC-002", "Ultra HD Smart TV 55\"",
                        "Crystal clear 4K UHD display with smart TV features, HDR10+, and built-in streaming apps.",
                        elec, "Samsung", 799.99, Map.of("screenSize", "55 inch", "resolution", "4K UHD", "hdr", true)),

                product("ELEC-003", "Professional DSLR Camera",
                        "24.2MP full-frame sensor with 4K video recording and advanced autofocus system.",
                        elec, "Canon", 1499.99, Map.of("megapixels", 24.2, "videoResolution", "4K")),

                product("ELEC-004", "Wireless Charging Pad",
                        "Fast wireless charger compatible with all Qi-enabled devices. Slim design with LED indicator.",
                        elec, "Anker", 29.99, Map.of("wattage", 15, "standard", "Qi")),

                product("ELEC-005", "Portable Bluetooth Speaker",
                        "Waterproof speaker with 360-degree sound, 12-hour playtime, and built-in microphone.",
                        elec, "JBL", 89.99, Map.of("waterproof", true, "batteryLife", "12h")),

                product("ELEC-006", "Mechanical Gaming Keyboard",
                        "RGB backlit mechanical keyboard with Cherry MX switches and programmable macros.",
                        elec, "Corsair", 149.99, Map.of("switchType", "Cherry MX Red", "rgb", true)),

                product("ELEC-007", "Wireless Gaming Mouse",
                        "Ultra-lightweight wireless mouse with 25,000 DPI sensor and 70-hour battery.",
                        elec, "Logitech", 79.99, Map.of("dpi", 25000, "wireless", true)),

                product("ELEC-008", "USB-C Hub Adapter",
                        "8-in-1 USB-C hub with HDMI, USB 3.0, SD card reader, and Power Delivery.",
                        elec, "Anker", 49.99, Map.of("ports", 8, "hdmi", true)),

                product("ELEC-009", "Noise Cancelling Earbuds",
                        "True wireless earbuds with adaptive noise cancellation and spatial audio.",
                        elec, "Apple", 249.99, Map.of("anc", true, "batteryLife", "6h")),

                product("ELEC-010", "Smartwatch Fitness Tracker",
                        "Advanced health monitoring with GPS, heart rate, SpO2, and 14-day battery life.",
                        elec, "Garmin", 349.99, Map.of("gps", true, "batteryLife", "14 days")),

                // — Clothing —
                product("CLTH-001", "Classic Fit Cotton T-Shirt",
                        "Premium 100% organic cotton t-shirt with a relaxed fit. Available in multiple colors.",
                        cloth, "Nike", 34.99, Map.of("material", "Organic Cotton", "fit", "Classic")),

                product("CLTH-002", "Slim Fit Denim Jeans",
                        "Stretch comfort denim with modern slim fit. Dark wash with whiskering details.",
                        cloth, "Levi's", 69.99, Map.of("material", "Stretch Denim", "fit", "Slim")),

                product("CLTH-003", "Waterproof Winter Jacket",
                        "Insulated waterproof jacket with detachable hood. Rated to -20°C.",
                        cloth, "The North Face", 249.99, Map.of("waterproof", true, "tempRating", "-20C")),

                product("CLTH-004", "Running Performance Shoes",
                        "Lightweight running shoes with responsive cushioning and breathable mesh upper.",
                        cloth, "Adidas", 129.99, Map.of("type", "Running", "weight", "255g")),

                product("CLTH-005", "Cashmere V-Neck Sweater",
                        "Luxurious 100% cashmere sweater with rib-knit trim. Perfect for layering.",
                        cloth, "Ralph Lauren", 189.99, Map.of("material", "Cashmere", "style", "V-Neck")),

                product("CLTH-006", "Leather Belt",
                        "Genuine Italian leather belt with brushed steel buckle. Timeless design.",
                        cloth, "Hugo Boss", 59.99, Map.of("material", "Italian Leather", "width", "35mm")),

                product("CLTH-007", "Athletic Jogger Pants",
                        "Quick-dry joggers with zip pockets and tapered fit for active lifestyles.",
                        cloth, "Under Armour", 54.99, Map.of("material", "Polyester Blend", "quickDry", true)),

                product("CLTH-008", "Wool Dress Coat",
                        "Tailored wool-blend overcoat with peak lapels. Perfect business attire.",
                        cloth, "Hugo Boss", 399.99, Map.of("material", "Wool Blend", "style", "Overcoat")),

                product("CLTH-009", "Hiking Boots",
                        "Durable leather hiking boots with Vibram soles and waterproof Gore-Tex lining.",
                        cloth, "Timberland", 179.99, Map.of("waterproof", true, "sole", "Vibram")),

                product("CLTH-010", "Silk Tie Collection",
                        "Handmade Italian silk tie with intricate patterns. Comes in premium gift box.",
                        cloth, "Gucci", 95.00, Map.of("material", "Italian Silk", "length", "150cm")),

                // — Home & Garden —
                product("HOME-001", "Robot Vacuum Cleaner",
                        "Smart mapping robot vacuum with app control, auto-empty station, and HEPA filter.",
                        home, "iRobot", 499.99, Map.of("mapping", true, "autoEmpty", true)),

                product("HOME-002", "Memory Foam Pillow Set",
                        "Ergonomic memory foam pillows with cooling gel layer. Set of 2.",
                        home, "Tempur-Pedic", 89.99, Map.of("quantity", 2, "cooling", true)),

                product("HOME-003", "Stainless Steel Cookware Set",
                        "12-piece professional-grade stainless steel cookware with aluminum core.",
                        home, "All-Clad", 349.99, Map.of("pieces", 12, "material", "Stainless Steel")),

                product("HOME-004", "Smart LED Light Bulbs",
                        "Color-changing WiFi smart bulbs compatible with Alexa and Google Home. 4-pack.",
                        home, "Philips", 49.99, Map.of("quantity", 4, "smartHome", true)),

                product("HOME-005", "Organic Cotton Bedsheet Set",
                        "300-thread-count organic cotton sheets. King size, includes fitted, flat, and pillow cases.",
                        home, "Brooklinen", 129.99, Map.of("threadCount", 300, "size", "King")),

                product("HOME-006", "Espresso Machine",
                        "Semi-automatic espresso machine with built-in grinder and milk frother.",
                        home, "Breville", 699.99, Map.of("grinder", true, "milkFrother", true)),

                product("HOME-007", "Indoor Herb Garden Kit",
                        "Self-watering indoor garden with LED grow lights. Grows herbs year-round.",
                        home, "AeroGarden", 79.99, Map.of("selfWatering", true, "ledLights", true)),

                product("HOME-008", "Cast Iron Dutch Oven",
                        "Enameled cast iron dutch oven. 7-quart capacity. Oven safe to 500°F.",
                        home, "Le Creuset", 369.99, Map.of("capacity", "7 quart", "ovenSafe", "500F")),

                product("HOME-009", "Cordless Vacuum",
                        "Lightweight cordless vacuum with 60-min runtime and wall-mount charging dock.",
                        home, "Dyson", 449.99, Map.of("cordless", true, "runtime", "60 min")),

                product("HOME-010", "Smart Thermostat",
                        "AI-powered thermostat that learns your schedule and saves up to 23% on energy.",
                        home, "Nest", 249.99, Map.of("aiPowered", true, "energySavings", "23%")),

                // — Books —
                product("BOOK-001", "The Art of Programming",
                        "Comprehensive guide to modern software development practices and design patterns.",
                        books, "O'Reilly", 49.99, Map.of("pages", 680, "format", "Hardcover")),

                product("BOOK-002", "Data Science Fundamentals",
                        "From basic statistics to machine learning. Hands-on approach with Python examples.",
                        books, "O'Reilly", 44.99, Map.of("pages", 520, "format", "Paperback")),

                product("BOOK-003", "The Creative Mindset",
                        "Unlock creativity and innovation in your personal and professional life.",
                        books, "Penguin", 24.99, Map.of("pages", 320, "format", "Paperback")),

                product("BOOK-004", "World History Encyclopedia",
                        "Beautifully illustrated comprehensive guide spanning 5,000 years of civilization.",
                        books, "DK Publishing", 39.99, Map.of("pages", 960, "format", "Hardcover")),

                product("BOOK-005", "Mastering Microservices",
                        "Build scalable distributed systems with Spring Boot, Docker, and Kubernetes.",
                        books, "Manning", 54.99, Map.of("pages", 480, "format", "Paperback")),

                product("BOOK-006", "Mindful Leadership",
                        "Evidence-based strategies for leading with empathy and purpose.",
                        books, "Harvard Press", 29.99, Map.of("pages", 280, "format", "Hardcover")),

                product("BOOK-007", "Organic Gardening Guide",
                        "Year-round organic gardening techniques for beginners and experts.",
                        books, "Rodale", 22.99, Map.of("pages", 350, "format", "Paperback")),

                product("BOOK-008", "Science of Cooking",
                        "The chemistry and physics behind great cooking. 200+ recipes with science explanations.",
                        books, "America's Test Kitchen", 35.99, Map.of("pages", 500, "recipes", 200)),

                product("BOOK-009", "Financial Freedom",
                        "Step-by-step guide to achieving financial independence and early retirement.",
                        books, "Crown Business", 19.99, Map.of("pages", 300, "format", "Paperback")),

                product("BOOK-010", "Digital Photography Masterclass",
                        "From beginner to pro: composition, lighting, editing, and post-processing.",
                        books, "National Geographic", 42.99, Map.of("pages", 420, "format", "Hardcover")),

                // — Sports & Outdoors —
                product("SPRT-001", "Carbon Fiber Road Bike",
                        "Ultra-light carbon frame road bike with Shimano Ultegra groupset. Size M.",
                        sports, "Trek", 2499.99, Map.of("frameMaterial", "Carbon Fiber", "groupset", "Shimano Ultegra")),

                product("SPRT-002", "Yoga Mat Premium",
                        "Extra thick 6mm non-slip yoga mat with alignment lines and carrying strap.",
                        sports, "Manduka", 69.99, Map.of("thickness", "6mm", "nonSlip", true)),

                product("SPRT-003", "Camping Tent 4-Person",
                        "Waterproof 4-person tent with easy setup and built-in ventilation.",
                        sports, "Coleman", 159.99, Map.of("capacity", "4 person", "waterproof", true)),

                product("SPRT-004", "Adjustable Dumbbell Set",
                        "Quick-change adjustable dumbbells from 5 to 52.5 lbs each. Space-saving design.",
                        sports, "Bowflex", 349.99, Map.of("weightRange", "5-52.5 lbs", "adjustable", true)),

                product("SPRT-005", "Trail Running Backpack",
                        "Lightweight 10L hydration-ready trail pack with breathable mesh back panel.",
                        sports, "Osprey", 89.99, Map.of("capacity", "10L", "hydrationReady", true)),

                product("SPRT-006", "Swimming Goggles Pro",
                        "Anti-fog UV protection swim goggles with adjustable nose bridge and quick-release strap.",
                        sports, "Speedo", 24.99, Map.of("antiFog", true, "uvProtection", true)),

                product("SPRT-007", "Basketball Official Size",
                        "Indoor/outdoor composite leather basketball. NCAA official size and weight.",
                        sports, "Spalding", 34.99, Map.of("size", "Official", "material", "Composite Leather")),

                product("SPRT-008", "Trekking Poles Pair",
                        "Ultralight carbon fiber trekking poles with cork grips and tungsten tips.",
                        sports, "Black Diamond", 129.99, Map.of("material", "Carbon Fiber", "gripType", "Cork")),

                product("SPRT-009", "Inflatable Kayak",
                        "2-person inflatable kayak with high-pressure floor. Includes pump and paddles.",
                        sports, "Intex", 199.99, Map.of("capacity", "2 person", "inflatable", true)),

                product("SPRT-010", "Resistance Band Set",
                        "5-level resistance band set with handles, door anchor, and carry bag.",
                        sports, "TheraBand", 29.99, Map.of("levels", 5, "includesHandles", true))
        );
    }

    private static Product product(String sku, String name, String description,
                                   String categoryId, String brand, double price,
                                   Map<String, Object> attributes) {

        Product p = new Product();
        p.setSku(sku);
        p.setName(name);
        p.setDescription(description);
        p.setCategoryId(categoryId);
        p.setBrand(brand);
        p.setBasePrice(BigDecimal.valueOf(price));
        p.setActive(true);
        p.setAttributes(attributes);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        int seed = Math.abs(sku.hashCode());
        List<String> urls = new java.util.ArrayList<>();
        for (int i = 0; i < 6; i++) {
            urls.add("https://picsum.photos/seed/" + sku.toLowerCase() + "-" + i + "/600/600");
        }
        p.setImageUrl(urls.get(0));
        p.setImageUrls(urls);

        return p;
    }
}
