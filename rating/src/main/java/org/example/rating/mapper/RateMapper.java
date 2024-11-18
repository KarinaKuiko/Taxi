package org.example.rating.mapper;

import org.example.rating.config.MapperConfiguration;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.entity.PassengerRate;
import org.example.rating.entity.Rate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface RateMapper {
    @Mapping(target = "id", ignore = true)
    DriverRate toDriverRate(RateCreateEditDto dto);

    @Mapping(target = "id", ignore = true)
    PassengerRate toPassengerRate(RateCreateEditDto dto);

    RateReadDto toReadDto(PassengerRate rate);
    RateReadDto toReadDto(DriverRate rate);

    void map(@MappingTarget Rate to, RateCreateEditDto from);
}
