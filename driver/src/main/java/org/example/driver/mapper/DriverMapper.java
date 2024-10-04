package org.example.driver.mapper;

import org.example.driver.config.MapperConfiguration;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface DriverMapper {
    @Mapping(target = "id", ignore = true)
    Driver toDriver(DriverCreateEditDto dto);

    @Mapping(target = "carId", source = "car.id")
    DriverReadDto toReadDto(Driver driver);

    void map(@MappingTarget Driver to, DriverCreateEditDto from);
}
