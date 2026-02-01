package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.DeviceController;
import org.asupg.asupgservice.model.DeviceDTO;
import org.asupg.asupgservice.model.request.CreateDeviceRequest;
import org.asupg.asupgservice.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/devices")
@RequiredArgsConstructor
public class DeviceControllerImpl implements DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable String id) {
        DeviceDTO device = deviceService.getDevice(id);

        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDTO> createDevice(
            @Validated @RequestBody CreateDeviceRequest createDeviceRequest
    ) {
        DeviceDTO device = deviceService.createDevice(
                createDeviceRequest.getDeviceId(),
                createDeviceRequest.getDeviceName(),
                createDeviceRequest.getCompanyInn(),
                createDeviceRequest.getMonthlyRate(),
                createDeviceRequest.getFreeUntil(),
                createDeviceRequest.getStatus()
        );

        return new ResponseEntity<>(device, HttpStatus.CREATED);
    }
}
