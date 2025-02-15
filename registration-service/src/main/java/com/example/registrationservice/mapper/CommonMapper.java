package com.example.registrationservice.mapper;

import com.example.registrationservice.config.MapperConfiguration;
import com.example.registrationservice.dto.create.DriverCreateEditDto;
import com.example.registrationservice.dto.create.SignUpDto;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface CommonMapper {

    DriverCreateEditDto toDriverCreateEditDto(SignUpDto signUpDto);
}
