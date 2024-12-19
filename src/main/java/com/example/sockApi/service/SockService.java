package com.example.sockApi.service;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.entity.Sock;
import com.example.sockApi.enums.SortBy;
import com.example.sockApi.exception.BusinessException;
import com.example.sockApi.exception.TechnicalException;
import com.example.sockApi.repository.SockRepository;
import com.example.sockApi.repository.specifications.SockSpecifications;
import com.example.sockApi.repository.specifications.SpecificationBuilder;
import com.example.sockApi.utils.MappingUtils;
import com.example.sockApi.utils.SockSortingUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class SockService {

    private static final String CSV_SEPARATOR = ",";

    private final SockRepository sockRepository;

    private final MappingUtils mappingUtils;

    private final SpecificationBuilder<Sock> specificationBuilder;

    private static Sock createSock(String color, double cottonPercentage, int count) {
        Sock newSock = new Sock();
        newSock.setColor(color);
        newSock.setCottonPercentage(cottonPercentage);
        newSock.setCount(count);
        return newSock;
    }

    public List<SockDto> getSocksByFilters(String color, Double exactCottonPercentage,
                                           Double minCottonPercentage, Double maxCottonPercentage) {

        Specification<Sock> specification = specificationBuilder.build(List.of(
                SockSpecifications.colorEquals(color),
                SockSpecifications.cottonPercentageEquals(exactCottonPercentage),
                SockSpecifications.cottonPercentageBetween(minCottonPercentage, maxCottonPercentage)
        ));

        return sockRepository
                .findAll(specification)
                .stream()
                .map(mappingUtils::mapToSockDto)
                .toList();

    }

    public SockDto addSocks(String color, double cottonPercentage, int count) throws BusinessException {
        if (cottonPercentage > 100 || cottonPercentage < 0) {
            String errorMessage = String.format("Socks income error - cotton percentage should be 0-100 (passed value is %f)",
                    cottonPercentage);
            log.error(errorMessage);
            throw new BusinessException(errorMessage);
        }

        Optional<Sock> optionalSock = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        Sock sock;

        if (optionalSock.isPresent()) {
            sock = optionalSock.get();
            sock.setCount(sock.getCount() + count);
        } else {
            sock = createSock(color, cottonPercentage, count);
        }
        return mappingUtils.mapToSockDto(sockRepository.save(sock));
    }

    public SockDto removeSocks(String color, double cottonPercentage, int count) throws BusinessException {
        Optional<Sock> optionalSock = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage);
        if (optionalSock.isPresent()) {
            Sock sock = optionalSock.get();
            int remains = sock.getCount() - count;
            if (remains >= 0) {
                sock.setCount(remains);
                sockRepository.save(sock);
                return mappingUtils.mapToSockDto(sock);
            } else {
                String errorMessage = String.format("Sock outcome error - the required socks are not in stock (color: %s, cotton percentage: %f, quantity: %d)",
                        color, cottonPercentage, count);
                log.error(errorMessage);
                throw new BusinessException(errorMessage);
            }
        } else {
            String errorMessage = String.format("Sock outcome error - the required socks are not in stock (color: %s, cotton percentage: %f, quantity: %d)",
                    color, cottonPercentage, count);
            log.error(errorMessage);
            throw new BusinessException(errorMessage);
        }
    }

    public SockDto updateSocks(Long id, String color, double cottonPercentage, int count) throws BusinessException {
        Optional<Sock> optionalSock = sockRepository.findById(id);
        if (optionalSock.isPresent()) {
            Sock sock = optionalSock.get();
            sock.setColor(color);
            sock.setCottonPercentage(cottonPercentage);
            sock.setCount(count);
            return mappingUtils.mapToSockDto(sockRepository.save(sock));
        } else {
            String errorMessage = String.format("Update error - missing items with the passed Id: %d", id);
            log.error(errorMessage);
            throw new BusinessException(errorMessage);
        }
    }

    public List<SockDto> getSocks(String color, Double exactCottonPercentage, Double minCottonPercentage, Double maxCottonPercentage,
                                  SortBy sortBy) {
        List<SockDto> sockList = getSocksByFilters(color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage);

        if (sortBy != null) {
            Comparator<SockDto> comparator = SockSortingUtils.getComparator(sortBy);
            sockList = sockList.stream()
                    .sorted(comparator)
                    .toList();
        }

        return sockList;
    }

    public Integer getSocksCount(String color, Double exactCottonPercentage, Double minCottonPercentage, Double maxCottonPercentage) {
        List<SockDto> sockList = getSocksByFilters(color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage);
        return sockList.stream()
                .map(SockDto::getCount)
                .reduce(0, Integer::sum);
    }

    @Transactional(rollbackFor = {TechnicalException.class, BusinessException.class, RuntimeException.class})
    public List<SockDto> processFileCsv(MultipartFile file) throws BusinessException, TechnicalException {

        Map<Long, SockDto> uploadedSocks = new HashMap<>();

        String line = "";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] lines = line.split(CSV_SEPARATOR);
                String color = lines[0];
                double cottonPercentage = Double.parseDouble(lines[1]);
                int count = Integer.parseInt(lines[2]);
                SockDto sockDto = addSocks(color, cottonPercentage, count);
                uploadedSocks.put(sockDto.getId(), sockDto);
            }
            return new ArrayList<>(uploadedSocks.values());
        } catch (IOException e) {
            String errorMessage = String.format("Error processing CSV-file %s",
                    file.getOriginalFilename());
            log.error(errorMessage);
            throw new TechnicalException(errorMessage, e);
        } catch (NumberFormatException e) {
            String errorMessage = String.format("Error parsing CSV-file on line %s, CSV line format: {String, double, int}",
                    line);
            log.error(errorMessage);
            throw new TechnicalException(errorMessage, e);
        } catch (BusinessException e) {
            String errorMessage = String.format("Error adding socks from CSV-file on line %s, %s",
                    line, e.getMessage());
            log.error(errorMessage);
            throw new BusinessException(errorMessage, e);
        }
    }

}
