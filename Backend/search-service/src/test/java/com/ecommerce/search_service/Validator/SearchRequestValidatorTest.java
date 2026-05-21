package com.ecommerce.search_service.Validator;

import com.ecommerce.search_service.Config.SearchProperties;
import com.ecommerce.search_service.Exception.InvalidSearchRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchRequestValidatorTest {

    private SearchRequestValidator validator;

    @BeforeEach
    void setUp() {
        SearchProperties properties = new SearchProperties();
        properties.setMaxPageSize(100);
        properties.setMaxRecommendations(20);
        validator = new SearchRequestValidator(properties);
    }

    @Nested
    @DisplayName("validateAndClampSize()")
    class ValidateAndClampSizeTests {

        @Test
        @DisplayName("should return same size when within limit")
        void validSize() {
            assertThat(validator.validateAndClampSize(50)).isEqualTo(50);
        }

        @Test
        @DisplayName("should clamp size to max when exceeding limit")
        void clampOversizedPage() {
            assertThat(validator.validateAndClampSize(200)).isEqualTo(100);
        }

        @Test
        @DisplayName("should accept size of 1")
        void minimumValidSize() {
            assertThat(validator.validateAndClampSize(1)).isEqualTo(1);
        }

        @Test
        @DisplayName("should accept size equal to max")
        void maxValidSize() {
            assertThat(validator.validateAndClampSize(100)).isEqualTo(100);
        }

        @Test
        @DisplayName("should throw for zero size")
        void zeroSize() {
            assertThatThrownBy(() -> validator.validateAndClampSize(0))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("at least 1");
        }

        @Test
        @DisplayName("should throw for negative size")
        void negativeSize() {
            assertThatThrownBy(() -> validator.validateAndClampSize(-1))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("at least 1");
        }
    }

    @Nested
    @DisplayName("validatePage()")
    class ValidatePageTests {

        @Test
        @DisplayName("should accept page zero")
        void pageZero() {
            validator.validatePage(0);
        }

        @Test
        @DisplayName("should accept positive page number")
        void positivePage() {
            validator.validatePage(5);
        }

        @Test
        @DisplayName("should throw for negative page number")
        void negativePage() {
            assertThatThrownBy(() -> validator.validatePage(-1))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("non-negative");
        }

        @Test
        @DisplayName("should throw for large negative page number")
        void largeNegativePage() {
            assertThatThrownBy(() -> validator.validatePage(-100))
                    .isInstanceOf(InvalidSearchRequestException.class);
        }
    }

    @Nested
    @DisplayName("validatePriceRange()")
    class ValidatePriceRangeTests {

        @Test
        @DisplayName("should accept valid price range")
        void validRange() {
            validator.validatePriceRange(BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("should accept equal min and max")
        void equalMinMax() {
            validator.validatePriceRange(BigDecimal.valueOf(50), BigDecimal.valueOf(50));
        }

        @Test
        @DisplayName("should accept null min and max")
        void nullBoth() {
            validator.validatePriceRange(null, null);
        }

        @Test
        @DisplayName("should accept null min with valid max")
        void nullMin() {
            validator.validatePriceRange(null, BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("should accept valid min with null max")
        void nullMax() {
            validator.validatePriceRange(BigDecimal.valueOf(10), null);
        }

        @Test
        @DisplayName("should accept zero as min price")
        void zeroMinPrice() {
            validator.validatePriceRange(BigDecimal.ZERO, BigDecimal.valueOf(100));
        }

        @Test
        @DisplayName("should throw when min exceeds max")
        void minExceedsMax() {
            assertThatThrownBy(() -> validator.validatePriceRange(BigDecimal.valueOf(200), BigDecimal.valueOf(100)))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("cannot exceed");
        }

        @Test
        @DisplayName("should throw for negative min price")
        void negativeMinPrice() {
            assertThatThrownBy(() -> validator.validatePriceRange(BigDecimal.valueOf(-10), BigDecimal.valueOf(100)))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("non-negative");
        }

        @Test
        @DisplayName("should throw for negative max price")
        void negativeMaxPrice() {
            assertThatThrownBy(() -> validator.validatePriceRange(null, BigDecimal.valueOf(-50)))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("non-negative");
        }
    }

    @Nested
    @DisplayName("validateQuery()")
    class ValidateQueryTests {

        @Test
        @DisplayName("should accept valid query")
        void validQuery() {
            validator.validateQuery("wireless headphones");
        }

        @Test
        @DisplayName("should accept single character query")
        void singleCharQuery() {
            validator.validateQuery("a");
        }

        @Test
        @DisplayName("should throw for null query")
        void nullQuery() {
            assertThatThrownBy(() -> validator.validateQuery(null))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("not be empty");
        }

        @Test
        @DisplayName("should throw for empty query")
        void emptyQuery() {
            assertThatThrownBy(() -> validator.validateQuery(""))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("not be empty");
        }

        @Test
        @DisplayName("should throw for blank query")
        void blankQuery() {
            assertThatThrownBy(() -> validator.validateQuery("   "))
                    .isInstanceOf(InvalidSearchRequestException.class)
                    .hasMessageContaining("not be empty");
        }
    }

    @Nested
    @DisplayName("clampRecommendationSize()")
    class ClampRecommendationSizeTests {

        @Test
        @DisplayName("should return same size when within limit")
        void validSize() {
            assertThat(validator.clampRecommendationSize(8)).isEqualTo(8);
        }

        @Test
        @DisplayName("should clamp to max recommendations")
        void clampToMax() {
            assertThat(validator.clampRecommendationSize(50)).isEqualTo(20);
        }

        @Test
        @DisplayName("should return 1 for zero size")
        void zeroSize() {
            assertThat(validator.clampRecommendationSize(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("should return 1 for negative size")
        void negativeSize() {
            assertThat(validator.clampRecommendationSize(-5)).isEqualTo(1);
        }
    }
}
