package org.example.ride.mapper;

import org.example.ride.config.MapperConfiguration;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.dto.create.RideStatusDto;
import org.example.ride.entity.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface RideMapper {
    @Mapping(target = "id", ignore = true)
    Ride toRide(RideCreateEditDto dto);

    RideReadDto toReadDto(Ride ride);

    void map(@MappingTarget Ride to, RideCreateEditDto from);

    void mapStatus(@MappingTarget Ride to, RideStatusDto rideStatusDto);
}
