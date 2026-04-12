package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.manas.skincare.dto.requests.IngredientRequest;
import kg.manas.skincare.dto.response.IngredientResponse;
import kg.manas.skincare.enums.SkinConcern;
import kg.manas.skincare.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients", description = "Управление базой ингредиентов (Админка)")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить новый ингредиент (с указанием min_age и уровней раздражения)")
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.createIngredient(request));
    }

    @GetMapping
    @Operation(summary = "Список всех активных ингредиентов")
    public ResponseEntity<List<IngredientResponse>> getAll() {
        return ResponseEntity.ok(ingredientService.getAllActiveIngredients());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Частично обновить данные (можно поменять только min_age или название)")
    public ResponseEntity<IngredientResponse> patch(
            @PathVariable Long id,
            @RequestBody IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.patchIngredient(id, request));
    }

    @PostMapping("/{id}/benefits")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Назначить суперсилу ингредиенту (например: Ретинол -> WRINKLES, сила 0.9)")
    public ResponseEntity<Void> addBenefit(
            @PathVariable Long id,
            @RequestParam SkinConcern concern,
            @RequestParam Double score) {
        ingredientService.addBenefitToIngredient(id, concern, score);
        return ResponseEntity.ok().build();
    }
}