package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.BandCreateDTO;
import cz.cvut.fit.cihlaond.DTO.BandDTO;
import cz.cvut.fit.cihlaond.service.BandService;
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

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class BandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BandService bandServiceMock;

    @Test
    void findById() throws Exception {
        BandDTO band = new BandDTO(5,"Slipknot", List.of(0, 1), 0L);

        BDDMockito.given(bandServiceMock.findByIdAsDTO(5)).willReturn(Optional.of(band));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/bands/{id}", 5)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(band.getId())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(band.getName())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.playerIds", CoreMatchers.is(band.getPlayerIds())))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).findByIdAsDTO(5);
    }

    @Test
    void findAll() throws Exception {
        int page = 0;
        int size = 2;
        int total = 3;
        Pageable pageable = PageRequest.of(page, size);

        List<BandDTO> data = List.of(
                new BandDTO(5,"Slipknot", List.of(0, 1), 0L),
                new BandDTO(6,"Rammstein", List.of(2, 3), 0L)
        );

        Page<BandDTO> pageExpected = new PageImpl<>(data, pageable, total);

        BDDMockito.given(bandServiceMock.findAll(pageable)).willReturn(pageExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/bands?page={page}&size={size}", page, size)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name", CoreMatchers.is("Slipknot")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name", CoreMatchers.is("Rammstein")))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).findAll(pageable);
    }

    @Test
    void findByName() throws Exception {
        BandDTO band = new BandDTO(5,"Slipknot", List.of(0, 1), 0L);

        BDDMockito.given(bandServiceMock.findByName("Slipknot")).willReturn(Optional.of(band));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/bands/?name={name}", band.getName())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(band.getId())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(band.getName())))
         .andExpect(MockMvcResultMatchers.jsonPath("$.playerIds", CoreMatchers.is(band.getPlayerIds())))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).findByName("Slipknot");
    }

    @Test
    void create() throws Exception {
        BandCreateDTO bandToCreate = new BandCreateDTO("Slipknot", List.of(0, 1));
        BandDTO bandExpected =  new BandDTO(5,"Slipknot", List.of(0, 1), 0L);
        BDDMockito.given(bandServiceMock.create(bandToCreate)).willReturn(bandExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"Slipknot\", \"playerIds\" : [0, 1] }")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(bandExpected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerIds", CoreMatchers.is(bandExpected.getPlayerIds())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).create(bandToCreate);
    }

    @Test
    void update() throws Exception {
        BandCreateDTO bandToCreate = new BandCreateDTO("Slipknot", List.of(0, 2));
        BandDTO bandExpected =  new BandDTO(5,"Slipknot", List.of(0, 2), 0L);
        BDDMockito.given(bandServiceMock.update(5, bandToCreate, "0")).willReturn(bandExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/bands/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\" : \"Slipknot\", \"playerIds\" : [0, 2] }")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(bandExpected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.playerIds", CoreMatchers.is(bandExpected.getPlayerIds())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).update(5, bandToCreate, "0");
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/bands/{id}", 5)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bandServiceMock, Mockito.atLeastOnce()).deleteById(5);
    }
}