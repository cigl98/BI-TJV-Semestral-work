package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.RehearsalCreateDTO;
import cz.cvut.fit.cihlaond.DTO.RehearsalDTO;
import cz.cvut.fit.cihlaond.service.RehearsalService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class RehearsalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RehearsalService rehearsalServiceMock;

    @Test
    void findById() throws Exception {
        LocalDateTime start = LocalDateTime.parse("2020-01-01T15:00");
        LocalDateTime end = LocalDateTime.parse("2020-01-01T18:00");
        RehearsalDTO rehearsal = new RehearsalDTO(1, start, end, 0);

        BDDMockito.given(rehearsalServiceMock.findByIdAsDTO(1)).willReturn(Optional.of(rehearsal));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/rehearsals/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(rehearsal.getId())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.startTime", CoreMatchers.is(rehearsal.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.endTime", CoreMatchers.is(rehearsal.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.rehearsalOfBandId", CoreMatchers.is(rehearsal.getRehearsalOfBandId())))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).findByIdAsDTO(1);
    }

    @Test
    void findAll() throws Exception {
        int page = 0;
        int size = 2;
        int total = 3;
        Pageable pageable = PageRequest.of(page, size);

        List<RehearsalDTO> data = List.of(
                new RehearsalDTO(5,LocalDateTime.parse("2020-01-01T15:00"), LocalDateTime.parse("2020-01-01T18:00"), 0),
                new RehearsalDTO(6,LocalDateTime.parse("2020-01-01T20:00"), LocalDateTime.parse("2020-01-01T21:00"), 1)
        );

        Page<RehearsalDTO> pageExpected = new PageImpl<>(data, pageable, total);

        BDDMockito.given(rehearsalServiceMock.findAll(pageable)).willReturn(pageExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/rehearsals?page={page}&size={size}", page, size)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", CoreMatchers.is(5)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id", CoreMatchers.is(6)))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).findAll(pageable);
    }

    @Test
    void findAllByRehearsalOfBandName() throws Exception {
        int page = 0;
        int size = 2;
        int total = 3;
        Pageable pageable = PageRequest.of(page, size);

        List<RehearsalDTO> data = List.of(
                new RehearsalDTO(5,LocalDateTime.parse("2020-01-01T15:00"), LocalDateTime.parse("2020-01-01T18:00"), 0),
                new RehearsalDTO(6,LocalDateTime.parse("2020-01-01T20:00"), LocalDateTime.parse("2020-01-01T21:00"), 0)
        );

        Page<RehearsalDTO> pageExpected = new PageImpl<>(data, pageable, total);

        BDDMockito.given(rehearsalServiceMock.findAllByRehearsalOfBandName("Slipknot", pageable)).willReturn(pageExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/rehearsals?page={page}&size={size}&name=Slipknot", page, size)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", CoreMatchers.is(5)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].rehearsalOfBandId", CoreMatchers.is(0)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].id", CoreMatchers.is(6)))
         .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].rehearsalOfBandId", CoreMatchers.is(0)))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).findAllByRehearsalOfBandName("Slipknot", pageable);
    }

    @Test
    void create() throws Exception {
        LocalDateTime x = LocalDateTime.parse("2020-01-01T15:00");
        LocalDateTime y = LocalDateTime.parse("2020-01-01T18:00");
        RehearsalCreateDTO rehearsalToCreate = new RehearsalCreateDTO(x, y, 0);
        RehearsalDTO rehearsalExpected = new RehearsalDTO(1, x, y, 0);

        BDDMockito.given(rehearsalServiceMock.create(rehearsalToCreate)).willReturn(rehearsalExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/rehearsals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\" : \"2020-01-01T15:00\", \"endTime\" : \"2020-01-01T18:00\", \"rehearsalOfBandId\" : 0 }")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(rehearsalExpected.getId())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.startTime", CoreMatchers.is(rehearsalExpected.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.endTime", CoreMatchers.is(rehearsalExpected.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.rehearsalOfBandId", CoreMatchers.is(rehearsalExpected.getRehearsalOfBandId())))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).create(rehearsalToCreate);
    }

    @Test
    void update() throws Exception {
        LocalDateTime x = LocalDateTime.parse("2020-01-01T15:00");
        LocalDateTime y = LocalDateTime.parse("2020-01-01T18:00");
        RehearsalCreateDTO rehearsalToCreate = new RehearsalCreateDTO(x, y, 0);
        RehearsalDTO rehearsalExpected = new RehearsalDTO(1, x, y, 0);

        BDDMockito.given(rehearsalServiceMock.update(1,rehearsalToCreate)).willReturn(rehearsalExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/rehearsals/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\" : \"2020-01-01T15:00\", \"endTime\" : \"2020-01-01T18:00\", \"rehearsalOfBandId\" : 0 }")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(rehearsalExpected.getId())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.startTime", CoreMatchers.is(rehearsalExpected.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.endTime", CoreMatchers.is(rehearsalExpected.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME))))
         .andExpect(MockMvcResultMatchers.jsonPath("$.rehearsalOfBandId", CoreMatchers.is(rehearsalExpected.getRehearsalOfBandId())))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).update(1, rehearsalToCreate);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/rehearsals/{id}", 0)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).deleteById(0);
    }

    @Test
    void make() throws Exception {
        LocalDateTime x = LocalDateTime.parse("2020-01-01T15:00");
        LocalDateTime y = LocalDateTime.parse("2020-01-01T18:00");
        RehearsalCreateDTO rehearsalToCreate = new RehearsalCreateDTO(x, y, 0);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/rehearsals/make")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startTime\" : \"2020-01-01T15:00\", \"endTime\" : \"2020-01-01T18:00\", \"rehearsalOfBandId\" : 0 }")
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(rehearsalServiceMock, Mockito.atLeastOnce()).makeRehearsal(rehearsalToCreate);
    }
}