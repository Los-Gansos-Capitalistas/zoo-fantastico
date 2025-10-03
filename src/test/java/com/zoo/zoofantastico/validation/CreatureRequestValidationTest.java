package com.zoo.zoofantastico.validation;

import com.zoo.zoofantastico.dto.request.CreateCreatureRequest;
import com.zoo.zoofantastico.dto.request.UpdateCreatureRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreatureRequestValidationTest {

private static Validator validator;

@BeforeAll
static void init() {
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
validator = factory.getValidator();
}

@Test
void createCreatureRequest_invalid_whenSizeNegative_orDangerOutOfRange() {
CreateCreatureRequest req = new CreateCreatureRequest();
req.setName("");
req.setSpecies("");
req.setSize(-1.0); // inválido
req.setDangerLevel(6); // inválido
req.setHealthStatus("");
req.setZoneId(1L);
req.setSize(0.0); // también inválido con DecimalMin("0.1")

Set<ConstraintViolation<CreateCreatureRequest>> v = validator.validate(req);
assertFalse(v.isEmpty());
assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("size")));
assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("dangerLevel")));
}

@Test
void createCreatureRequest_invalid_whenNameBlank() {
  CreateCreatureRequest req = new CreateCreatureRequest();
  req.setName(" ");          // inválido
  req.setSpecies("Ave");
  req.setSize(0.5);          // valor válido
  req.setDangerLevel(5);     // dentro de rango
  req.setHealthStatus("stable");
  req.setZoneId(1L);

  Set<ConstraintViolation<CreateCreatureRequest>> v = validator.validate(req);
  assertFalse(v.isEmpty());
  assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("name")));
}

@Test
void createCreatureRequest_valid_whenBoundsAreOk() {
CreateCreatureRequest req = new CreateCreatureRequest();
req.setName("Fénix");
req.setSpecies("Ave");
req.setSize(0.1); // límite válido
req.setDangerLevel(1); // límite válido
req.setHealthStatus("stable");
req.setZoneId(1L);

Set<ConstraintViolation<CreateCreatureRequest>> v = validator.validate(req);
assertTrue(v.isEmpty());
}

@Test
void updateCreatureRequest_allOptionalButValidatedWhenPresent() {
UpdateCreatureRequest req = new UpdateCreatureRequest();
req.setSize(-0.1); // inválido si se envía
req.setDangerLevel(0); // inválido si se envía

Set<ConstraintViolation<UpdateCreatureRequest>> v = validator.validate(req);
assertFalse(v.isEmpty());
assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("size")));
assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("dangerLevel")));
}
}