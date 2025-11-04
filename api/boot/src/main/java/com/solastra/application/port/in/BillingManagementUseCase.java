package com.solastra.application.port.in;

import com.solastra.application.port.in.model.BillingInfoResponse;
import com.solastra.application.port.in.model.CreateBillingInfoCommand;
import com.solastra.application.port.in.model.GetBillingInfoQuery;
import com.solastra.application.port.in.model.UpdateBillingInfoCommand;

public interface BillingManagementUseCase {
    /**
     * Get billing information by account ID
     *
     * @param query The query containing account ID
     * @return The billing info response
     */
    BillingInfoResponse getBillingInfo(GetBillingInfoQuery query);

    /**
     * Create billing information
     *
     * @param command The command containing billing info to create
     * @return The created billing info response
     */
    BillingInfoResponse createBillingInfo(CreateBillingInfoCommand command);

    /**
     * Update billing information
     *
     * @param command The command containing billing info to update
     * @return The updated billing info response
     */
    BillingInfoResponse updateBillingInfo(UpdateBillingInfoCommand command);

    /**
     * Delete billing information by account ID
     *
     * @param accountId The account ID
     */
    void deleteBillingInfo(String accountId);
}