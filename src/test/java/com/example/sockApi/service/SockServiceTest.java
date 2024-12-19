package com.example.sockApi.service;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.entity.Sock;
import com.example.sockApi.exception.BusinessException;
import com.example.sockApi.exception.TechnicalException;
import com.example.sockApi.repository.SockRepository;
import com.example.sockApi.repository.specifications.SpecificationBuilder;
import com.example.sockApi.utils.MappingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class SockServiceTest {

    @InjectMocks
    private SockService sockService;

    @Mock
    private SockRepository sockRepository;

    @Mock
    private MappingUtils mappingUtils;

    @Mock
    private SpecificationBuilder<Sock> specificationBuilder;

    @Test
    void testAddSocks_Success_NewSock() throws BusinessException {
        Sock newSock = new Sock();
        newSock.setId(1L);
        newSock.setColor("Red");
        newSock.setCottonPercentage(50.0);
        newSock.setCount(100);

        Mockito.when(sockRepository.findByColorAndCottonPercentage("Red", 50.0))
                .thenReturn(Optional.empty());
        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenReturn(newSock);
        Mockito.when(mappingUtils.mapToSockDto(newSock))
                .thenReturn(new SockDto(1L, "Red", 50.0, 100));

        SockDto result = sockService.addSocks("Red", 50.0, 100);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Red", result.getColor());
        Assertions.assertEquals(50.0, result.getCottonPercentage());
        Assertions.assertEquals(100, result.getCount());

        Mockito.verify(sockRepository).findByColorAndCottonPercentage("Red", 50.0);
        Mockito.verify(sockRepository).save(Mockito.any(Sock.class));
    }

    @Test
    void testAddSocks_BusinessException_InvalidCottonPercentage() {
        double invalidCottonPercentage = 120.0;

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> sockService.addSocks("Red", invalidCottonPercentage, 100)
        );

        Assertions.assertEquals(
                String.format("Socks income error - cotton percentage should be 0-100 (passed value is %f)", invalidCottonPercentage),
                exception.getMessage()
        );

        Mockito.verifyNoInteractions(sockRepository);
    }

    @Test
    void testRemoveSocks_Success() throws BusinessException {
        Sock existingSock = new Sock();
        existingSock.setId(1L);
        existingSock.setColor("Blue");
        existingSock.setCottonPercentage(60.0);
        existingSock.setCount(100);

        Mockito.when(sockRepository.findByColorAndCottonPercentage("Blue", 60.0))
                .thenReturn(Optional.of(existingSock));
        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenReturn(existingSock);
        Mockito.when(mappingUtils.mapToSockDto(existingSock))
                .thenReturn(new SockDto(1L, "Blue", 60.0, 80));

        SockDto result = sockService.removeSocks("Blue", 60.0, 20);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(80, result.getCount());

        Mockito.verify(sockRepository).findByColorAndCottonPercentage("Blue", 60.0);
        Mockito.verify(sockRepository).save(existingSock);
    }

    @Test
    void testRemoveSocks_BusinessException_NotEnoughStock() {
        Sock existingSock = new Sock();
        existingSock.setId(1L);
        existingSock.setColor("Blue");
        existingSock.setCottonPercentage(60.0);
        existingSock.setCount(10);

        Mockito.when(sockRepository.findByColorAndCottonPercentage("Blue", 60.0))
                .thenReturn(Optional.of(existingSock));

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> sockService.removeSocks("Blue", 60.0, 20)
        );

        Assertions.assertTrue(exception.getMessage().contains("the required socks are not in stock"));

        Mockito.verify(sockRepository).findByColorAndCottonPercentage("Blue", 60.0);
        Mockito.verifyNoMoreInteractions(sockRepository);
    }

    @Test
    void testUpdateSocks_Success() throws BusinessException {
        Sock existingSock = new Sock();
        existingSock.setId(1L);

        Sock updatedSock = new Sock();
        updatedSock.setId(1L);
        updatedSock.setColor("green");
        updatedSock.setCottonPercentage(70.0);
        updatedSock.setCount(200);

        Mockito.when(sockRepository.findById(1L)).thenReturn(Optional.of(existingSock));
        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenReturn(updatedSock);
        Mockito.when(mappingUtils.mapToSockDto(updatedSock))
                .thenReturn(new SockDto(1L, "green", 70.0, 200));

        SockDto result = sockService.updateSocks(1L, "green", 70.0, 200);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("green", result.getColor());
        Assertions.assertEquals(70.0, result.getCottonPercentage());
        Assertions.assertEquals(200, result.getCount());

        Mockito.verify(sockRepository).findById(1L);
        Mockito.verify(sockRepository).save(Mockito.any(Sock.class));
    }

    @Test
    void testUpdateSocks_BusinessException_NotFound() {
        Mockito.when(sockRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = Assertions.assertThrows(
                BusinessException.class,
                () -> sockService.updateSocks(1L, "green", 70.0, 200)
        );

        Assertions.assertTrue(exception.getMessage().contains("missing items with the passed Id"));

        Mockito.verify(sockRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(sockRepository);
    }

    @Test
    void testProcessFileCsv_Success() throws IOException, BusinessException, TechnicalException {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "socks.csv", "text/csv",
                "red,50.0,100\nblue,75.0,50".getBytes()
        );

        Mockito.when(sockRepository.findByColorAndCottonPercentage(Mockito.anyString(), Mockito.anyDouble()))
                .thenReturn(Optional.empty());

        Sock sock1 = new Sock();
        sock1.setId(1L);
        Sock sock2 = new Sock();
        sock2.setId(2L);

        Mockito.when(sockRepository.save(Mockito.any(Sock.class))).thenReturn(sock1, sock2);
        Mockito.when(mappingUtils.mapToSockDto(Mockito.any(Sock.class)))
                .thenReturn(new SockDto(1L, "Red", 50.0, 100), new SockDto(2L, "Blue", 75.0, 50));

        List<SockDto> result = sockService.processFileCsv(mockFile);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Red", result.get(0).getColor());
        Assertions.assertEquals("Blue", result.get(1).getColor());

        Mockito.verify(sockRepository, Mockito.times(2)).save(Mockito.any(Sock.class));
    }

    @Test
    void testGetSocksCount() {
        String color = "red";
        double cottonPercentage = 75.0;
        List<Sock> socks = List.of(
                new Sock(1L, color, cottonPercentage, 100),
                new Sock(2L, color, cottonPercentage, 150)
        );

        List<SockDto> sockDtos = List.of(
                new SockDto(1L, color, cottonPercentage, 100),
                new SockDto(2L, color, cottonPercentage, 150)
        );

        Specification<Sock> specification = mock(Specification.class);

        Mockito.when(specificationBuilder.build(Mockito.anyList())).thenReturn(specification);
        Mockito.when(sockRepository.findAll(specification)).thenReturn(socks);
        Mockito.when(mappingUtils.mapToSockDto(Mockito.any(Sock.class)))
                .thenAnswer(invocation -> {
                    Sock sock = invocation.getArgument(0);
                    return new SockDto(sock.getId(), sock.getColor(), sock.getCottonPercentage(), sock.getCount());
                });

        Integer totalCount = sockService.getSocksCount(color, cottonPercentage, null, null);

        Assertions.assertEquals(250, totalCount);

        Mockito.verify(specificationBuilder).build(Mockito.anyList());
        Mockito.verify(sockRepository).findAll(specification);
        Mockito.verify(mappingUtils, Mockito.times(2)).mapToSockDto(Mockito.any(Sock.class));
    }

    @Test
    void testGetSocksByFilters() {
        String color = "blue";
        Double minCottonPercentage = 40.0;
        Double maxCottonPercentage = 60.0;

        List<Sock> socks = List.of(
                new Sock(1L, color, 50.0, 100),
                new Sock(2L, color, 45.0, 150)
        );

        List<SockDto> sockDtos = List.of(
                new SockDto(1L, color, 50.0, 100),
                new SockDto(2L, color, 45.0, 150)
        );

        Specification<Sock> specification = mock(Specification.class);

        Mockito.when(specificationBuilder.build(Mockito.anyList())).thenReturn(specification);
        Mockito.when(sockRepository.findAll(specification)).thenReturn(socks);
        Mockito.when(mappingUtils.mapToSockDto(Mockito.any(Sock.class)))
                .thenAnswer(invocation -> {
                    Sock sock = invocation.getArgument(0);
                    return new SockDto(sock.getId(), sock.getColor(), sock.getCottonPercentage(), sock.getCount());
                });

        List<SockDto> result = sockService.getSocksByFilters(color, null, minCottonPercentage, maxCottonPercentage);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(sockDtos, result);

        Mockito.verify(specificationBuilder).build(Mockito.anyList());
        Mockito.verify(sockRepository).findAll(specification);
        Mockito.verify(mappingUtils, Mockito.times(2)).mapToSockDto(Mockito.any(Sock.class));
    }
}

