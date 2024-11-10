package org.example.rating.unit.service;

import org.example.rating.dto.read.RateReadDto;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.service.RateCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class RateCounterServiceTest {

    private RateCounterService rateCounterService;

    @BeforeEach
    public void setUp() {
        rateCounterService = new RateCounterService();
    }

    @Test
    void countRating_whenRatingsIsNull_shouldReturnZero() {
        double result = rateCounterService.countRating(null);

        assertThat(result).isEqualTo(0D);
    }

    @Test
    void countRating_whenRatingsIsEmpty_shouldReturnZero() {
        double result = rateCounterService.countRating(Collections.emptyList());

        assertThat(result).isEqualTo(0D);
    }

    @Test
    void countRating_whenSingleRating_shouldReturnThatRating() {
        RateReadDto rating = new RateReadDto(1L, 101L, "Great ride!", 5, 1001L, UserType.PASSENGER);
        List<RateReadDto> ratings = Collections.singletonList(rating);

        double result = rateCounterService.countRating(ratings);

        assertThat(result).isEqualTo(5.0);
    }

    @Test
    void countRating_whenMultipleRatings_shouldReturnAverage() {
        RateReadDto rating1 = new RateReadDto(1L, 101L, "Great ride!", 5, 1001L, UserType.PASSENGER);
        RateReadDto rating2 = new RateReadDto(2L, 102L, "Okay experience.", 3, 1001L, UserType.PASSENGER);
        RateReadDto rating3 = new RateReadDto(3L, 103L, "Worst ride ever.", 1, 1001L, UserType.PASSENGER);
        List<RateReadDto> ratings = Arrays.asList(rating1, rating2, rating3);

        double result = rateCounterService.countRating(ratings);

        assertThat(result).isEqualTo(3.0);
    }
}
