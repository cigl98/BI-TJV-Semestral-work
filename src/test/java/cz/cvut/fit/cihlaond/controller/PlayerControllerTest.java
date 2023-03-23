package cz.cvut.fit.cihlaond.controller;

import cz.cvut.fit.cihlaond.DTO.PlayerCreateDTO;
import cz.cvut.fit.cihlaond.DTO.PlayerDTO;
import cz.cvut.fit.cihlaond.service.PlayerService;
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
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerService playerServiceMock;

    @Test
    void findById() throws Exception {
        PlayerDTO player = new PlayerDTO(0, "Mick", "Thomson", "Guitar");

        BDDMockito.given(playerServiceMock.findByIdAsDTO(0)).willReturn(Optional.of(player));

        mockMvc.perform(
                MockMvcRequestBuilders
                .get("/players/{id}", 0)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(player.getId())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(player.getFirstName())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(player.getLastName())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.instrument", CoreMatchers.is(player.getInstrument())))
        .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).findByIdAsDTO(0);
    }

    @Test
    void findAll() throws Exception {
        int page = 0;
        int size = 2;
        int total = 3;
        Pageable pageable = PageRequest.of(page, size);

        List<PlayerDTO> data = List.of(
                new PlayerDTO(0,"Till", "Lindemann", "vocals"),
                new PlayerDTO(1,"Richard", "Kruspe", "guitar"));
        Page<PlayerDTO> pageExpected = new PageImpl<>(data, pageable, total);

        BDDMockito.given(playerServiceMock.findAll(pageable)).willReturn(pageExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                .get("/players?page={page}&size={size}", page, size)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstName", CoreMatchers.is("Till")))
         .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].firstName", CoreMatchers.is("Richard")))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).findAll(pageable);


        page = 1;
        pageable = PageRequest.of(page, size);
        data = List.of(new PlayerDTO(2, "Christopher", "Schneider", "drums"));
        pageExpected = new PageImpl<>(data, pageable, total);
        BDDMockito.given(playerServiceMock.findAll(pageable)).willReturn(pageExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/players?page={page}&size={size}", page, size)
        ).andExpect(MockMvcResultMatchers.jsonPath("$.content[0].firstName", CoreMatchers.is("Christopher")))
         .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).findAll(pageable);

    }

    @Test
    void create() throws Exception {
        PlayerCreateDTO playerToCreate = new PlayerCreateDTO("Mick", "Thomson", "Guitar");
        PlayerDTO playerExpected = new PlayerDTO(0, "Mick", "Thomson", "Guitar");

        BDDMockito.given(playerServiceMock.create(playerToCreate)).willReturn(playerExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\" : \"Mick\", \"lastName\" : \"Thomson\", \"instrument\" : \"Guitar\"}")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(playerExpected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(playerExpected.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(playerExpected.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instrument", CoreMatchers.is(playerExpected.getInstrument())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).create(playerToCreate);
    }

    @Test
    void update() throws Exception {
        PlayerCreateDTO playerToCreate = new PlayerCreateDTO("Mick", "Thomson", "violin");
        PlayerDTO playerExpected = new PlayerDTO(0, "Mick", "Thomson", "violin");

        BDDMockito.given(playerServiceMock.update(0, playerToCreate)).willReturn(playerExpected);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/players/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\" : \"Mick\", \"lastName\" : \"Thomson\", \"instrument\" : \"violin\"}")
        ).andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(playerExpected.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is(playerExpected.getFirstName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is(playerExpected.getLastName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instrument", CoreMatchers.is(playerExpected.getInstrument())))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).update(0, playerToCreate);
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/players/{id}", 0)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(playerServiceMock, Mockito.atLeastOnce()).deleteById(0);
    }
}