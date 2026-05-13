package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.DeviceController;
import org.asupg.asupgservice.model.Device;
import org.asupg.asupgservice.model.request.CreateDeviceRequest;
import org.asupg.asupgservice.model.request.DeviceUpdateRequest;
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
    public ResponseEntity<Device> getDevice(@PathVariable String id) {
        Device device = deviceService.getDevice(id);

        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> createDevice(
            @Validated @RequestBody CreateDeviceRequest createDeviceRequest
    ) {
        Device device = deviceService.createDevice(
                createDeviceRequest.getDeviceId(),
                createDeviceRequest.getDeviceName(),
                createDeviceRequest.getCompanyInn(),
                createDeviceRequest.getMonthlyRate(),
                createDeviceRequest.getFreeUntil(),
                createDeviceRequest.getStatus()
        );

        return new ResponseEntity<>(device, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> updateDevice(
            @PathVariable String id,
            @Validated @RequestBody DeviceUpdateRequest deviceUpdateRequest
    ) {
        Device device = deviceService.updateDevice(
                id,
                deviceUpdateRequest.getDeviceName(),
                deviceUpdateRequest.getCompanyInn(),
                deviceUpdateRequest.getMonthlyRate(),
                deviceUpdateRequest.getFreeUntil(),
                deviceUpdateRequest.getStatus()
        );

        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> deleteDevice(@PathVariable String id) {
        Device device = deviceService.deleteDevice(id);

        return new ResponseEntity<>(device, HttpStatus.OK);
    }
}
