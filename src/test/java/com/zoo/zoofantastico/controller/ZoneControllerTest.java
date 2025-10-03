package com.zoo.zoofantastico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.zoofantastico.dto.ZoneSummaryDTO;
import com.zoo.zoofantastico.dto.request.CreateZoneRequest;
import com.zoo.zoofantastico.dto.request.UpdateZoneRequest;
import com.zoo.zoofantastico.exception.ApiExceptionHandler;
import com.zoo.zoofantastico.model.Zone;
import com.zoo.zoofantastico.service.ZoneService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZoneController.class)
@Import(ApiExceptionHandler.class)
class ZoneControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper om;

  @MockBean private ZoneService service;

  @Test
  void post_createZone_returns201_andDTO() throws Exception {
    CreateZoneRequest body = new CreateZoneRequest();
    body.setName("Bosque");
    body.setDescription("Frondosa");
    body.setCapacity(80);

    Zone saved = new Zone();
    saved.setId(10L); saved.setName("Bosque");
    saved.setDescription("Frondosa"); saved.setCapacity(80);

    Mockito.when(service.create(any(CreateZoneRequest.class))).thenReturn(saved);

    mvc.perform(post("/api/zones")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(body)))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/zones/10"))
      .andExpect(jsonPath("$.id", is(10)))
      .andExpect(jsonPath("$.name", is("Bosque")))
      .andExpect(jsonPath("$.description", is("Frondosa")))
      .andExpect(jsonPath("$.capacity", is(80)));
  }

  @Test
  void put_updateZone_returns200_andDTO() throws Exception {
    UpdateZoneRequest body = new UpdateZoneRequest();
    body.setDescription("Más frondosa");
    body.setCapacity(90);

    Zone updated = new Zone();
    updated.setId(10L); updated.setName("Bosque");
    updated.setDescription("Más frondosa"); updated.setCapacity(90);

    Mockito.when(service.update(eq(10L), any(UpdateZoneRequest.class))).thenReturn(updated);

    mvc.perform(put("/api/zones/10")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(body)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(10)))
      .andExpect(jsonPath("$.description", is("Más frondosa")))
      .andExpect(jsonPath("$.capacity", is(90)));
  }

  @Test
  void get_summary_returns200_andList() throws Exception {
    List<ZoneSummaryDTO> list = List.of(
        ZoneSummaryDTO.builder().id(1L).name("Bosque").description("F").capacity(80).creaturesCount(2L).build(),
        ZoneSummaryDTO.builder().id(2L).name("Desierto").description("A").capacity(50).creaturesCount(0L).build()
    );

    Mockito.when(service.findSummary()).thenReturn(list);

    mvc.perform(get("/api/zones/summary"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].name", is("Bosque")))
      .andExpect(jsonPath("$[0].creaturesCount", is(2)))
      .andExpect(jsonPath("$[1].name", is("Desierto")));
  }

  @Test
  void post_createZone_400_whenNameBlank() throws Exception {
    CreateZoneRequest body = new CreateZoneRequest();
    body.setName(" "); // inválido
    body.setCapacity(10);

    mvc.perform(post("/api/zones")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(body)))
      .andExpect(status().isBadRequest());
  }

@Test
void post_createZone_409_whenNameDuplicated() throws Exception {
  CreateZoneRequest body = new CreateZoneRequest();
  body.setName("Bosque"); body.setDescription("F"); body.setCapacity(80);

  when(service.create(any(CreateZoneRequest.class)))
      .thenThrow(new DataIntegrityViolationException("Duplicate entry 'Bosque' for key 'uk_zone_name'"));

  mvc.perform(post("/api/zones")
      .contentType(MediaType.APPLICATION_JSON)
      .content(om.writeValueAsString(body)))
    .andExpect(status().isConflict())
    .andExpect(jsonPath("$.error", is("Conflict")))
    .andExpect(jsonPath("$.message", containsString("Zone name")));
}


}