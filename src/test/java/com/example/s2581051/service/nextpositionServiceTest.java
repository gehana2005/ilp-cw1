package com.example.s2581051.service;

import com.example.s2581051.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
public class nextpositionServiceTest {

    private nextpositionService service;
    private Validator validator;

    @BeforeEach
    void setUp(){
        service = new nextpositionService();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // VALIDATION TESTs
}
