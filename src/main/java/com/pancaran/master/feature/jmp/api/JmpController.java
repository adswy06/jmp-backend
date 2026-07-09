package com.pancaran.master.feature.jmp.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.jmp.dto.JmpRequestDto;
import com.pancaran.master.feature.jmp.entity.JmpEntity;
import com.pancaran.master.feature.jmp.service.JmpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.apik.core.common.FeatureOperation;
import com.apik.core.data.dto.impl.SearchInputImpl;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/jmp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "JMP Transactions", description = "Endpoints for creating and updating JMP orders and mapping trip plans")
public class JmpController {

    private final JmpService service;

    @PostMapping
    @Operation(summary = "Create or Update JMP Transaction", description = "Saves a complete JMP order transaction (trip plans, points, activities, extra costs, units, drivers, transit). If saveToMaster is true, propagates route points and activities back to the master route templates.")
    public ResponseEntity<APIResponse<JmpEntity>> saveJmp(@RequestBody JmpRequestDto request) {
        JmpEntity saved = service.saveJmp(request);
        return ResponseEntity.ok(APIResponse.success(saved));
    }

    @Operation(summary = "Listing, Searching & Filtering JMP Data")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<JmpEntity>> getJmpPage(
            @ParameterObject @PageableDefault(page = 0, size = 20) Pageable pageable,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String[] filter) throws Exception {
        SearchInputImpl input = new SearchInputImpl(null, FeatureOperation.SEARCH);
        input.pageable(pageable, search, filter);
        return ResponseEntity.ok(service.getJmpPage(input));
    }
}
