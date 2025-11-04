package com.solastra.application.port.in;

import com.solastra.application.port.in.model.AccountResponse;
import com.solastra.application.port.in.model.GetAccountQuery;
import com.solastra.application.port.in.model.UpdateAccountCommand;

public interface AccountManagementUseCase {
    /**
     * Get account details by ID
     *
     * @param query The query containing account ID
     * @return The account response
     */
    AccountResponse getAccount(GetAccountQuery query);

    /**
     * Update account details
     *
     * @param command The update command
     * @return The updated account response
     */
    AccountResponse updateAccount(UpdateAccountCommand command);
}