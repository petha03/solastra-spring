package com.solastra.adapters.in.rest;

import com.solastra.adapters.in.rest.model.AccountResponseDto;
import com.solastra.adapters.in.rest.model.UpdateAccountRequest;
import com.solastra.application.port.in.AccountManagementUseCase;
import com.solastra.application.port.in.model.AccountResponse;
import com.solastra.application.port.in.model.GetAccountQuery;
import com.solastra.application.port.in.model.UpdateAccountCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountManagementUseCase accountManagementUseCase;

    public AccountController(AccountManagementUseCase accountManagementUseCase) {
        this.accountManagementUseCase = accountManagementUseCase;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable String accountId) {
        GetAccountQuery query = new GetAccountQuery(accountId);
        AccountResponse response = accountManagementUseCase.getAccount(query);

        AccountResponseDto dto = AccountResponseDto.from(response);

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable String accountId,
            @Valid @RequestBody UpdateAccountRequest request) {

        UpdateAccountCommand command = new UpdateAccountCommand(accountId, request.name());
        AccountResponse response = accountManagementUseCase.updateAccount(command);

        AccountResponseDto dto = AccountResponseDto.from(response);

        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .body(dto);
    }
}