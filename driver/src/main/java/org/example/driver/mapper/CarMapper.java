package org.example.driver.mapper;

import org.example.driver.config.MapperConfiguration;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface CarMapper {
    @Mapping(target = "id", ignore = true)
    Car toCar(CarCreateEditDto dto);

    CarReadDto toReadDto(Car car);

    void map(@MappingTarget Car to, CarCreateEditDto from);
}
