package org.example.driver.service;

import com.example.exceptionhandlerstarter.exception.driver.DriverNotFoundException;
import com.example.exceptionhandlerstarter.exception.driver.DuplicatedDriverEmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.driver.constants.CommonConstants;
import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.RideReadDto;
import org.example.driver.dto.read.UserRateDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;

import org.example.driver.mapper.CarMapper;
import org.example.driver.mapper.DriverMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.example.driver.constants.RedisConstants.DRIVER_CACHE_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;
    private final MessageSource messageSource;
    private final CarService carService;
    private final CarMapper carMapper;
    private final ImageStorageService imageStorageService;

    @Transactional
    @CachePut(value = DRIVER_CACHE_VALUE, key = "#result.id()")
    public DriverReadDto create(DriverCreateEditDto driverDto, MultipartFile multipartFile) {
        driverRepository.findByEmailAndIsDeletedFalse(driverDto.email())
                .ifPresent(driver -> {
                    throw new DuplicatedDriverEmailException(messageSource.getMessage(
                            ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                            new Object[]{driverDto.email()},
                            LocaleContextHolder.getLocale()));
                });

        Driver driver = driverMapper.toDriver(driverDto);
        Car car = carRepository.findByNumberAndIsDeletedFalse(driverDto.carCreateEditDto().number())
                .orElseGet(() -> {
                    CarReadDto carReadDto = carService.create(driverDto.carCreateEditDto());
                    return carMapper.toCar(carReadDto);
                });
        driver.setCar(car);
        driver.setRating(CommonConstants.DEFAULT_RATING);
        if (multipartFile != null) {
            String imageUrl = imageStorageService.uploadImage(multipartFile);
            driver.setImageUrl(imageUrl);
        }
        return driverMapper.toReadDto(driverRepository.save(driver));

    }

    @Transactional
    @CachePut(value = DRIVER_CACHE_VALUE, key = "#id")
    public DriverReadDto update(Long id, DriverCreateEditDto driverDto, MultipartFile file) {
        return driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driver -> {
                    driverRepository.findByEmailAndIsDeletedFalse(driverDto.email())
                            .ifPresent(driverCheck -> {
                                if (!driverCheck.getId().equals(id)) {
                                    throw new DuplicatedDriverEmailException(messageSource.getMessage(
                                            ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                                            new Object[]{driverDto.email()},
                                            LocaleContextHolder.getLocale()));
                                }
                            });
                    driverMapper.map(driver, driverDto);
                    Car car = carRepository.findByNumberAndIsDeletedFalse(driverDto.carCreateEditDto().number())
                            .orElseGet(() -> {
                                CarReadDto carReadDto = carService.create(driverDto.carCreateEditDto());
                                return carMapper.toCar(carReadDto);
                            });
                    driver.setCar(car);
                    String newImageUrl = imageStorageService.updateImage(driver.getImageUrl(), file);
                    driver.setImageUrl(newImageUrl);
                    return driver;
                })
                .map(driverRepository::save)
                .map(driverMapper::toReadDto)
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        ExceptionConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    @Transactional
    @CacheEvict(value = DRIVER_CACHE_VALUE, key = "#id")
    public void safeDelete(Long id) {
        driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driver -> {
                    driver.setDeleted(true);
                    driver.setCar(null);
                    driverRepository.save(driver);
                    return driver;
                })
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        ExceptionConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    public Page<DriverReadDto> findAll(Integer page, Integer limit) {
        PageRequest request = PageRequest.of(page, limit);
        return driverRepository.findByIsDeletedFalse(request)
                .map(driverMapper::toReadDto);
    }

    public List<DriverReadDto> findFullList() {
        return driverRepository.findByIsDeletedFalse()
                .stream()
                .map(driverMapper::toReadDto)
                .toList();
    }

    public Page<DriverReadDto> findAllWithDeleted(Integer page, Integer limit) {
        PageRequest request = PageRequest.of(page, limit);
        return driverRepository.findAll(request)
                .map(driverMapper::toReadDto);
    }

    @Cacheable(value = DRIVER_CACHE_VALUE, key = "#id")
    public DriverReadDto findById(Long id) {
        return driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driverMapper::toReadDto)
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        ExceptionConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    public void notifyDriver(RideReadDto rideReadDto) {
        log.info(rideReadDto.toString());
    }

    @Transactional
    public void updateRating(UserRateDto userRateDto) {
        Driver driver = driverRepository.findById(userRateDto.userId()).get();
        driver.setRating(userRateDto.averageRate());
        log.info("Update rating to {}, driver's id {}", userRateDto.averageRate(), userRateDto.userId());
        driverRepository.save(driver);
    }
}
