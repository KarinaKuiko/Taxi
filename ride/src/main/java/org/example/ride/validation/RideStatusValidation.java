package org.example.ride.validation;

import lombok.RequiredArgsConstructor;
import org.example.ride.constants.AppConstants;
import org.example.ride.dto.create.RideStatusDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.exception.ride.CanceledRideStatusException;
import org.example.ride.exception.ride.InvalidRideStatusForChangingException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideStatusValidation {
    private final MessageSource messageSource;

    private void checkLogicUpdatingStatus(DriverRideStatus current, DriverRideStatus proposed, DriverRideStatus potential) {
        if (proposed != potential && proposed != DriverRideStatus.CANCELED) {
            throw new InvalidRideStatusForChangingException(messageSource.getMessage(
                    AppConstants.INVALID_PROPOSED_STATUS,
                    new Object[]{current, proposed},
                    LocaleContextHolder.getLocale()));
        }
    }

    private void checkCanceledStatus(DriverRideStatus status) {
        if (status == DriverRideStatus.CANCELED) {
            throw new CanceledRideStatusException(messageSource.getMessage(
                    AppConstants.CANCELED_STATUS,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }
    }

    public void validateUpdatingStatus(Ride ride, RideStatusDto rideStatusDto) {
        DriverRideStatus current = ride.getDriverRideStatus();
        checkCanceledStatus(current);

        DriverRideStatus proposed = rideStatusDto.driverRideStatus();

        switch (current) {
            case CREATED:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.ACCEPTED);
                break;
            case ACCEPTED:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.ON_WAY_FOR_PASSENGER);
                break;
            case ON_WAY_FOR_PASSENGER:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.ON_WAY_TO_DESTINATION);
                break;
            case ON_WAY_TO_DESTINATION:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.COMPLETED);
        }
    }
}
