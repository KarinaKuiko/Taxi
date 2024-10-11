package org.example.passenger.mapper;

import org.example.passenger.config.MapperConfiguration;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.entity.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface PassengerMapper {
    @Mapping(target = "id", ignore = true)
    Passenger toPassenger(PassengerCreateEditDto dto);

    PassengerReadDto toReadDto(Passenger passenger);

    void map(@MappingTarget Passenger to, PassengerCreateEditDto from);
}
