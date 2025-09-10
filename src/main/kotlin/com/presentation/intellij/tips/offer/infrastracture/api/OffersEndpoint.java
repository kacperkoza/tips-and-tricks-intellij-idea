package com.presentation.intellij.tips.offer.infrastracture.api;

import com.presentation.intellij.tips.offer.Offer;
import com.presentation.intellij.tips.offer.Offers;
import com.presentation.intellij.tips.offer.OffersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class OffersEndpoint {

    private final OffersService offersService;

    public OffersEndpoint(OffersService offersService) {
        this.offersService = offersService;
    }

    @GetMapping("/offers")
    public Offers getOffers(
            @RequestParam("limit") Integer limit,
            @RequestParam("offset") Integer offset
    ) {
        List<Offer> offers = offersService.getOffers(limit, offset);
        return new Offers(offers);
    }

    @PostMapping("/users/{userId}/offers")
    public void addOffer(
            @PathVariable("userId") String userId,
            @RequestBody Offer offer
    ) {
        offersService.add(offer, userId, UUID.randomUUID().toString());
    }


}
