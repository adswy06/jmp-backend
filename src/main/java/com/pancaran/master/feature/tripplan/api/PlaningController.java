package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.service.PlaningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planing")
@RequiredArgsConstructor
public class PlaningController {

    private final PlaningService planingService;

    @PostMapping
    public ResponseEntity<APIResponse<RouteCreateRequestDto>> planning(@RequestBody @Valid RouteCreateRequestDto request) {
        planingService.planRoute(request);
        return ResponseEntity.ok(APIResponse.success(request));
    }
}
