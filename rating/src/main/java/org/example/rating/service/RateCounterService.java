package org.example.rating.service;

import org.example.rating.dto.read.RateReadDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateCounterService {
    public Double countRating(List<RateReadDto> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0D;
        }
        double sum = 0D;
        for (RateReadDto rating : ratings) {
            sum += rating.rating();
        }
        return sum/ratings.size();
    }
}
