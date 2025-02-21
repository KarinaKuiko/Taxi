package org.example.ride.validation;

import com.example.exceptionhandlerstarter.exception.ride.CanceledRideStatusException;
import com.example.exceptionhandlerstarter.exception.ride.InvalidRideStatusForChangingException;
import com.example.exceptionhandlerstarter.exception.ride.IrrelevantDriverStatusException;
import lombok.RequiredArgsConstructor;
import org.example.ride.constants.ExceptionConstants;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
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
                    ExceptionConstants.INVALID_PROPOSED_STATUS_MESSAGE,
                    new Object[]{current, proposed},
                    LocaleContextHolder.getLocale()));
        }
    }

    private void checkCanceledStatus(DriverRideStatus status) {
        if (status == DriverRideStatus.CANCELED) {
            throw new CanceledRideStatusException(messageSource.getMessage(
                    ExceptionConstants.CANCELED_STATUS_MESSAGE,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }
    }

    public void validateUpdatingDriverStatus(Ride ride, DriverRideStatus proposed) {
        DriverRideStatus current = ride.getDriverRideStatus();
        checkCanceledStatus(current);

        switch (current) {
            case CREATED:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.ACCEPTED);
                break;
            case ACCEPTED:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.ON_WAY_FOR_PASSENGER);
                break;
            case ON_WAY_FOR_PASSENGER:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.WAITING);
                break;
            case WAITING:
                checkLogicUpdatingStatus(current, proposed,DriverRideStatus.ON_WAY_TO_DESTINATION);
                break;
            case ON_WAY_TO_DESTINATION:
                checkLogicUpdatingStatus(current, proposed, DriverRideStatus.COMPLETED);
                break;
        }
    }

    public void validateUpdatingPassengerStatus(Ride ride) {
        DriverRideStatus current = ride.getDriverRideStatus();
        checkWaitingStatus(current);
    }

    private void checkWaitingStatus(DriverRideStatus status) {
        if (status != DriverRideStatus.WAITING) {
            throw new IrrelevantDriverStatusException(messageSource.getMessage(
                    ExceptionConstants.IRRELEVANT_DRIVER_STATUS,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }
    }
}
