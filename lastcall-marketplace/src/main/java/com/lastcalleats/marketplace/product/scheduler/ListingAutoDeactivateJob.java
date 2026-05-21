package com.lastcalleats.marketplace.product.scheduler;

import com.lastcalleats.marketplace.product.repository.ProductListingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListingAutoDeactivateJob {

    private final ProductListingRepo productListingRepo;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deactivateExpiredListings() {
        log.info("Running expired listing deactivation task...");
        try {
            int count = productListingRepo.deactivateExpiredListings(LocalDate.now());
            if (count > 0) {
                log.info("Deactivated {} expired listings", count);
            }
        } catch (Exception e) {
            log.error("Listing deactivation task failed", e);
        }
    }
}
