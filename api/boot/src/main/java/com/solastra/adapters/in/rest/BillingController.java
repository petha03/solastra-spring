package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.BillingInfoResponseDto;
import com.solastra.adapters.in.rest.model.CreateBillingInfoRequest;
import com.solastra.adapters.in.rest.model.UpdateBillingInfoRequest;
import com.solastra.application.port.in.BillingManagementUseCase;
import com.solastra.application.port.in.model.BillingInfoResponse;
import com.solastra.application.port.in.model.CreateBillingInfoCommand;
import com.solastra.application.port.in.model.GetBillingInfoQuery;
import com.solastra.application.port.in.model.UpdateBillingInfoCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingManagementUseCase billingManagementUseCase;

    public BillingController(BillingManagementUseCase billingManagementUseCase) {
        this.billingManagementUseCase = billingManagementUseCase;
    }

    @GetMapping
    public ResponseEntity<BillingInfoResponseDto> getBillingInfo(@RequestParam String accountId) {
        GetBillingInfoQuery query = new GetBillingInfoQuery(accountId);
        BillingInfoResponse response = billingManagementUseCase.getBillingInfo(query);

        BillingInfoResponseDto dto = new BillingInfoResponseDto(
                response.id(),
                response.accountId(),
                response.stripeCustomerId(),
                response.createdTimestamp(),
                response.updatedTimestamp()
        );

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PostMapping
    public ResponseEntity<BillingInfoResponseDto> createBillingInfo(
            @Valid @RequestBody CreateBillingInfoRequest request) {

        CreateBillingInfoCommand command = new CreateBillingInfoCommand(
                request.accountId(),
                request.stripeCustomerId()
        );
        BillingInfoResponse response = billingManagementUseCase.createBillingInfo(command);

        BillingInfoResponseDto dto = new BillingInfoResponseDto(
                response.id(),
                response.accountId(),
                response.stripeCustomerId(),
                response.createdTimestamp(),
                response.updatedTimestamp()
        );

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PutMapping
    public ResponseEntity<BillingInfoResponseDto> updateBillingInfo(
            @RequestParam String accountId,
            @Valid @RequestBody UpdateBillingInfoRequest request) {

        UpdateBillingInfoCommand command = new UpdateBillingInfoCommand(
                accountId,
                request.stripeCustomerId()
        );
        BillingInfoResponse response = billingManagementUseCase.updateBillingInfo(command);

        BillingInfoResponseDto dto = new BillingInfoResponseDto(
                response.id(),
                response.accountId(),
                response.stripeCustomerId(),
                response.createdTimestamp(),
                response.updatedTimestamp()
        );

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBillingInfo(@RequestParam String accountId) {
        billingManagementUseCase.deleteBillingInfo(accountId);
        return ResponseEntity.noContent()
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }
}