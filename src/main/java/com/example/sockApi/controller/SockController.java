package com.example.sockApi.controller;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.enums.SortBy;
import com.example.sockApi.exception.BusinessException;
import com.example.sockApi.exception.TechnicalException;
import com.example.sockApi.service.SockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/socks")
@AllArgsConstructor
@Slf4j
public class SockController {

    private final SockService sockService;

    @PostMapping("/income")
    public ResponseEntity<SockDto> incomeSocks(@RequestParam String color,
                                               @RequestParam double cottonPercentage,
                                               @RequestParam int count) throws BusinessException {
        log.info("Request to income socks. Color: {}, cotton percentage: {}, count: {}",
                color, cottonPercentage, count);
        SockDto sock = sockService.addSocks(color, cottonPercentage, count);
        return new ResponseEntity<>(sock, HttpStatus.CREATED);
    }

    @PostMapping("/outcome")
    public ResponseEntity<SockDto> outcomeSocks(@RequestParam String color,
                                                @RequestParam double cottonPercentage,
                                                @RequestParam int count) throws BusinessException {
        log.info("Request to remove socks. Color: {}, cotton percentage: {}, count: {}", color, cottonPercentage, count);
        SockDto changedSock = sockService.removeSocks(color, cottonPercentage, count);

        return new ResponseEntity<>(changedSock, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SockDto> updateSocks(@PathVariable Long id,
                                               @RequestParam String color,
                                               @RequestParam double cottonPercentage,
                                               @RequestParam int count) throws BusinessException {
        log.info("Request to update the socks. Id: {}, new color: {}, new cotton percentage: {}, new count: {}",
                id, color, cottonPercentage, count);
        SockDto updatedSock = sockService.updateSocks(id, color, cottonPercentage, count);
        return new ResponseEntity<>(updatedSock, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SockDto>> getSocks(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double exactCottonPercentage,
            @RequestParam(required = false) Double minCottonPercentage,
            @RequestParam(required = false) Double maxCottonPercentage,
            @RequestParam(required = false) SortBy sortBy) {
        log.info("Request for socks. Color: {}, exact percentage of cotton: {}," +
                        "minimum percentage of cotton: {}, maximum percentage of cotton: {}, sorting: {}",
                color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage, sortBy);
        List<SockDto> socks = sockService.getSocks(color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage, sortBy);
        return new ResponseEntity<>(socks, HttpStatus.OK);
    }


    @GetMapping("/count")
    public ResponseEntity<Integer> getSocksCount(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double exactCottonPercentage,
            @RequestParam(required = false) Double minCottonPercentage,
            @RequestParam(required = false) Double maxCottonPercentage) {
        log.info("Request for count of socks. Color: {}, exact percentage of cotton: {}," +
                        "minimum percentage of cotton: {}, maximum percentage of cotton: {}",
                color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage);
        Integer count = sockService.getSocksCount(color, exactCottonPercentage, minCottonPercentage, maxCottonPercentage);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SockDto>> uploadBatch(@RequestParam("file") MultipartFile file) throws BusinessException, TechnicalException {
        log.info("Request for adding socks from file: {}", file.getOriginalFilename());
        List<SockDto> uploadedSocks = sockService.processFileCsv(file);
        return new ResponseEntity<>(uploadedSocks, HttpStatus.CREATED);
    }
}