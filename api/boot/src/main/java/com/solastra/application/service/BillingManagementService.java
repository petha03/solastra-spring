package com.solastra.application.service;

import com.solastra.application.port.in.BillingManagementUseCase;
import com.solastra.application.port.in.model.BillingInfoResponse;
import com.solastra.application.port.in.model.CreateBillingInfoCommand;
import com.solastra.application.port.in.model.GetBillingInfoQuery;
import com.solastra.application.port.in.model.UpdateBillingInfoCommand;
import com.solastra.application.port.out.BillingRepository;
import com.solastra.domain.model.BillingInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class BillingManagementService implements BillingManagementUseCase {

    private final BillingRepository billingRepository;

    public BillingManagementService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    @Override
    public BillingInfoResponse getBillingInfo(GetBillingInfoQuery query) {
        BillingInfo billingInfo = billingRepository.findByAccountId(query.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Billing info not found for account: " + query.accountId()));
        return BillingInfoResponse.from(billingInfo);
    }

    @Override
    public BillingInfoResponse createBillingInfo(CreateBillingInfoCommand command) {
        billingRepository.findByAccountId(command.accountId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Billing info already exists for account: " + command.accountId());
                });

        BillingInfo billingInfo = new BillingInfo();
        billingInfo.setId(UUID.randomUUID().toString());
        billingInfo.setAccountId(command.accountId());
        billingInfo.setStripeCustomerId(command.stripeCustomerId());
        billingInfo.setCreatedTimestamp(Instant.now());
        billingInfo.setUpdatedTimestamp(Instant.now());

        BillingInfo saved = billingRepository.save(billingInfo);
        return BillingInfoResponse.from(saved);
    }

    @Override
    public BillingInfoResponse updateBillingInfo(UpdateBillingInfoCommand command) {
        BillingInfo billingInfo = billingRepository.findByAccountId(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Billing info not found for account: " + command.accountId()));

        billingInfo.setStripeCustomerId(command.stripeCustomerId());
        billingInfo.setUpdatedTimestamp(Instant.now());

        BillingInfo updated = billingRepository.save(billingInfo);
        return BillingInfoResponse.from(updated);
    }

    @Override
    public void deleteBillingInfo(String accountId) {
        billingRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Billing info not found for account: " + accountId));
        billingRepository.deleteByAccountId(accountId);
    }
}