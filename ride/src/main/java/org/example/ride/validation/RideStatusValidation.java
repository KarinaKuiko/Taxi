package org.example.ride.validation;

import lombok.RequiredArgsConstructor;
import org.example.ride.constants.AppConstants;
import org.example.ride.dto.read.RideStatusDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.RideStatus;
import org.example.ride.exception.ride.CanceledRideStatusException;
import org.example.ride.exception.ride.InvalidRideStatusForChangingException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideStatusValidation {
    private final MessageSource messageSource;

    private void checkLogicUpdatingStatus(RideStatus current, RideStatus proposed, RideStatus potential) {
        if (proposed != potential && proposed != RideStatus.CANCELED) {
            throw new InvalidRideStatusForChangingException(messageSource.getMessage(
                    AppConstants.INVALID_PROPOSED_STATUS,
                    new Object[]{current, proposed},
                    LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
        }
    }

    private void checkCanceledStatus(RideStatus status) {
        if (status == RideStatus.CANCELED) {
            throw new CanceledRideStatusException(messageSource.getMessage(
                    AppConstants.CANCELED_STATUS,
                    new Object[]{},
                    LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
        }
    }

    public void validateUpdatingStatus(Ride ride, RideStatusDto rideStatusDto) {
        RideStatus current = ride.getRideStatus();
        checkCanceledStatus(current);

        RideStatus proposed = rideStatusDto.rideStatus();

        switch (current) {
            case CREATED:
                checkLogicUpdatingStatus(current, proposed, RideStatus.ACCEPTED);
                break;
            case ACCEPTED:
                checkLogicUpdatingStatus(current, proposed, RideStatus.ON_WAY_FOR_PASSENGER);
                break;
            case ON_WAY_FOR_PASSENGER:
                checkLogicUpdatingStatus(current, proposed, RideStatus.ON_WAY_TO_DESTINATION);
                break;
            case ON_WAY_TO_DESTINATION:
                checkLogicUpdatingStatus(current, proposed, RideStatus.COMPLETED);
        }
    }
}
