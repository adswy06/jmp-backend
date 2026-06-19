package com.pancaran.master.feature.routeplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.constant.ResponseMessageConstant;
import com.pancaran.master.feature.routeplan.dto.RouteHeaderDto;
import com.pancaran.master.feature.routeplan.dto.request.RoutePlanRequest;
import com.pancaran.master.feature.routeplan.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Master Route Api", description = "Mapping Endpoint for Master Route")
@RestController
@RequestMapping("/master/routes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MasterRouteAPI {

    @Autowired
    private RouteService routeService;

    @Operation(summary = "Create master route JMP V1")
    @PostMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<RouteHeaderDto>> createPlan(@RequestBody @Valid RoutePlanRequest request) throws Exception {
        String dataId = routeService.saveBaseRoute(request);
        RouteHeaderDto result = routeService.findRouteHeaderById(dataId);

        return ResponseEntity.ok(APIResponse.success(result, ResponseMessageConstant.SUCCESS_CREATE));
    }
}
