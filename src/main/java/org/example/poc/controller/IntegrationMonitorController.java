package org.example.poc.controller;

import org.example.poc.response.ResponseUltils;
import org.example.poc.service.IntegrationMonitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationMonitorController {
    private final IntegrationMonitorService integrationMonitorService;

    public IntegrationMonitorController(IntegrationMonitorService integrationMonitorService) {
        this.integrationMonitorService = integrationMonitorService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getOverview() {
        return ResponseUltils.success(
                integrationMonitorService.getOverview(),
                "Integration overview fetched successfully",
                "INTEGRATION_OVERVIEW_SUCCESS"
        );
    }

    @GetMapping("/connector/{name}")
    public ResponseEntity<?> getConnectorStatus(@PathVariable("name") String connectorName) {
        return ResponseUltils.success(
                integrationMonitorService.getConnectorStatus(connectorName),
                "Connector status fetched successfully",
                "INTEGRATION_CONNECTOR_STATUS_SUCCESS"
        );
    }
}
