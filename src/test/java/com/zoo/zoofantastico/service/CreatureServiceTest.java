package com.zoo.zoofantastico.service;

import com.zoo.zoofantastico.exception.ResourceNotFoundException;
import com.zoo.zoofantastico.model.Creature;
import com.zoo.zoofantastico.model.Zone;
import com.zoo.zoofantastico.repository.CreatureRepository;
import com.zoo.zoofantastico.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.zoo.zoofantastico.dto.request.UpdateCreatureRequest;
import com.zoo.zoofantastico.dto.request.CreateCreatureRequest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreatureServiceTest {

  @Mock private CreatureRepository creatureRepository;
  @Mock private ZoneRepository zoneRepository;

  @InjectMocks private CreatureService service;

  @BeforeEach
  void setup() { MockitoAnnotations.openMocks(this); }

  @Test
  void getCreatureById_returnsCreature_whenExists() {
    Creature c = new Creature();
    c.setId(1L);
    when(creatureRepository.findById(1L)).thenReturn(Optional.of(c));

    Creature out = service.getCreatureById(1L);

    assertNotNull(out);
    assertEquals(1L, out.getId());
    verify(creatureRepository).findById(1L);
  }

  @Test
  void getCreatureById_throws_whenNotFound() {
    when(creatureRepository.findById(9L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getCreatureById(9L));
    verify(creatureRepository).findById(9L);
  }

  @Test
  void createCreature_loadsZone_andSaves() {
    Zone z = new Zone();
    z.setId(2L); z.setName("Desierto");

    CreateCreatureRequest req = new CreateCreatureRequest();
req.setName("Fénix");
req.setSpecies("Ave");
req.setSize(1.0);          // evita NPE
req.setDangerLevel(5);     // dentro del rango
req.setHealthStatus("stable");
req.setZoneId(2L);

when(zoneRepository.findById(2L)).thenReturn(Optional.of(z));
when(creatureRepository.save(any(Creature.class))).thenAnswer(inv -> inv.getArgument(0));

Creature saved = service.createCreature(req);


    assertEquals("Desierto", saved.getZone().getName());
    verify(zoneRepository).findById(2L);
    verify(creatureRepository).save(any(Creature.class));
  }

  @Test
  void createCreature_throws_whenZoneIdMissing() {
    CreateCreatureRequest req = new CreateCreatureRequest(); // sin zoneId
assertThrows(IllegalArgumentException.class, () -> service.createCreature(req));
verifyNoInteractions(creatureRepository);
  }

  
  @Test
  void updateCreature_updatesFields_andZone() {
    Creature existing = new Creature();
    existing.setId(1L);
    existing.setName("Old");

    Zone z = new Zone(); z.setId(3L); z.setName("Bosque");

    UpdateCreatureRequest incoming = new UpdateCreatureRequest();
incoming.setName("New");
incoming.setSpecies(null);
incoming.setSize(null);
incoming.setDangerLevel(null);
incoming.setHealthStatus(null);
incoming.setZoneId(3L);

    when(creatureRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(zoneRepository.findById(3L)).thenReturn(Optional.of(z));
    when(creatureRepository.save(any(Creature.class))).thenAnswer(inv -> inv.getArgument(0));

    Creature out = service.updateCreature(1L, incoming);

    assertEquals("New", out.getName());
    assertEquals("Bosque", out.getZone().getName());
    verify(creatureRepository).save(any(Creature.class));
  }

  @Test
  void deleteCreature_blocks_whenCritical() {
    Creature c = new Creature();
    c.setId(1L); c.setHealthStatus("critical");
    when(creatureRepository.findById(1L)).thenReturn(Optional.of(c));

    assertThrows(IllegalStateException.class, () -> service.deleteCreature(1L));
    verify(creatureRepository, never()).delete(any());
  }

  @Test
  void deleteCreature_deletes_whenNotCritical() {
    Creature c = new Creature();
    c.setId(1L); c.setHealthStatus("stable");
    when(creatureRepository.findById(1L)).thenReturn(Optional.of(c));

    service.deleteCreature(1L);

    verify(creatureRepository).delete(c);
  }

  @Test
  void getAllCreatures_returnsList() {
    when(creatureRepository.findAll()).thenReturn(List.of(new Creature(), new Creature()));
    assertEquals(2, service.getAllCreatures().size());
  }
}
