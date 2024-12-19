package com.example.sockApi.controller;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.enums.SortBy;
import com.example.sockApi.service.SockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SockService sockService;

    private static final String BASE_URL = "/api/socks";

    @Test
    public void testIncomeSocks() throws Exception {
        SockDto mockSockDto = new SockDto(1L, "Red", 75.0, 100);

        Mockito.when(sockService.addSocks("Red", 75.0, 100)).thenReturn(mockSockDto);

        mockMvc.perform(post(BASE_URL + "/income")
                        .param("color", "Red")
                        .param("cottonPercentage", "75.0")
                        .param("count", "100"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.color", is("Red")))
                .andExpect(jsonPath("$.cottonPercentage", is(75.0)))
                .andExpect(jsonPath("$.count", is(100)));

        Mockito.verify(sockService).addSocks("Red", 75.0, 100);
    }

    @Test
    public void testOutcomeSocks() throws Exception {
        SockDto mockSockDto = new SockDto(1L, "Blue", 50.0, 80);

        Mockito.when(sockService.removeSocks("Blue", 50.0, 20)).thenReturn(mockSockDto);

        mockMvc.perform(post(BASE_URL + "/outcome")
                        .param("color", "Blue")
                        .param("cottonPercentage", "50.0")
                        .param("count", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.color", is("Blue")))
                .andExpect(jsonPath("$.cottonPercentage", is(50.0)))
                .andExpect(jsonPath("$.count", is(80)));

        Mockito.verify(sockService).removeSocks("Blue", 50.0, 20);
    }

    @Test
    public void testUpdateSocks() throws Exception {
        SockDto mockSockDto = new SockDto(1L, "Green", 60.0, 200);

        Mockito.when(sockService.updateSocks(1L, "Green", 60.0, 200)).thenReturn(mockSockDto);

        mockMvc.perform(put(BASE_URL + "/1")
                        .param("color", "Green")
                        .param("cottonPercentage", "60.0")
                        .param("count", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.color", is("Green")))
                .andExpect(jsonPath("$.cottonPercentage", is(60.0)))
                .andExpect(jsonPath("$.count", is(200)));

        Mockito.verify(sockService).updateSocks(1L, "Green", 60.0, 200);
    }

    @Test
    public void testGetSocks_EmptyFilters() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, null, null, null, null)).thenReturn(mockSocks);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].color", is("Red")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].color", is("Blue")));

        Mockito.verify(sockService).getSocks(null, null, null, null, null);
    }

    @Test
    public void testGetSocks_FilterByColor() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks("Red", null, null, null, null)).thenReturn(List.of(mockSocks.get(0)));

        mockMvc.perform(get(BASE_URL)
                        .param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].color", is("Red")))
                .andExpect(jsonPath("$[0].id", is(1)));

        Mockito.verify(sockService).getSocks("Red", null, null, null, null);
    }

    @Test
    public void testGetSocks_FilterByExactCottonPercentage() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, 75.0, null, null, null)).thenReturn(List.of(mockSocks.get(0)));

        mockMvc.perform(get(BASE_URL)
                        .param("exactCottonPercentage", "75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cottonPercentage", is(75.0)))
                .andExpect(jsonPath("$[0].id", is(1)));

        Mockito.verify(sockService).getSocks(null, 75.0, null, null, null);
    }

    @Test
    public void testGetSocks_FilterByMinCottonPercentage() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, null, 50.0, null, null)).thenReturn(List.of(mockSocks.get(1)));

        mockMvc.perform(get(BASE_URL)
                        .param("minCottonPercentage", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cottonPercentage", is(50.0)))
                .andExpect(jsonPath("$[0].id", is(2)));

        Mockito.verify(sockService).getSocks(null, null, 50.0, null, null);
    }

    @Test
    public void testGetSocks_FilterByMaxCottonPercentage() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, null, null, 75.0, null)).thenReturn(List.of(mockSocks.get(0)));

        mockMvc.perform(get(BASE_URL)
                        .param("maxCottonPercentage", "75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cottonPercentage", is(75.0)))
                .andExpect(jsonPath("$[0].id", is(1)));

        Mockito.verify(sockService).getSocks(null, null, null, 75.0, null);
    }

    @Test
    public void testGetSocks_FilterByMinAndMaxCottonPercentage() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, null, 50.0, 75.0, null)).thenReturn(List.of(mockSocks.get(1), mockSocks.get(0)));

        mockMvc.perform(get(BASE_URL)
                        .param("minCottonPercentage", "50")
                        .param("maxCottonPercentage", "75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cottonPercentage", is(50.0)))
                .andExpect(jsonPath("$[1].cottonPercentage", is(75.0)));

        Mockito.verify(sockService).getSocks(null, null, 50.0, 75.0, null);
    }

    @Test
    public void testGetSocks_FilterByColorAndCottonPercentage() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Red", 50.0, 200)
        );

        Mockito.when(sockService.getSocks("Red", null, null, null, null)).thenReturn(List.of(mockSocks.get(0), mockSocks.get(1)));

        mockMvc.perform(get(BASE_URL)
                        .param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].color", is("Red")))
                .andExpect(jsonPath("$[1].color", is("Red")));

        Mockito.verify(sockService).getSocks("Red", null, null, null, null);
    }

    @Test
    public void testGetSocks_Sorting() throws Exception {
        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.getSocks(null, null, null, null, SortBy.COLOR_ASC)).thenReturn(List.of(mockSocks.get(1), mockSocks.get(0)));

        mockMvc.perform(get(BASE_URL)
                        .param("sortBy","COLOR_ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color", is("Blue")))
                .andExpect(jsonPath("$[1].color", is("Red")));

        Mockito.verify(sockService).getSocks(null, null, null, null, SortBy.COLOR_ASC);
    }

    @Test
    public void testGetSocksCount() throws Exception {
        Mockito.when(sockService.getSocksCount("Red", 75.0, null, null)).thenReturn(150);

        mockMvc.perform(get(BASE_URL + "/count")
                        .param("color", "Red")
                        .param("exactCottonPercentage", "75.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("150"));

        Mockito.verify(sockService).getSocksCount("Red", 75.0, null, null);
    }

    @Test
    public void testUploadBatch() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "socks.csv", "text/csv",
                "red,75.0,100\nblue,50.0,200".getBytes()
        );

        List<SockDto> mockSocks = List.of(
                new SockDto(1L, "Red", 75.0, 100),
                new SockDto(2L, "Blue", 50.0, 200)
        );

        Mockito.when(sockService.processFileCsv(Mockito.any(MultipartFile.class))).thenReturn(mockSocks);

        mockMvc.perform(multipart(BASE_URL + "/batch")
                        .file(mockFile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].color", is("Red")))
                .andExpect(jsonPath("$[1].color", is("Blue")));

        Mockito.verify(sockService).processFileCsv(Mockito.any(MultipartFile.class));
    }
}