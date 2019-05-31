/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.enums;

import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates different products
 */
 public enum Product {
    /**
     * Live odds product
     */
    LiveOdds(1, "liveodds"),

    /**
     * Managed trading services product
     */
    MTS(2, "mts"),

    /**
     * Live cycle-of-odds product
     */
    LCoO(3, "lcoo"),

    /**
     * Betpal product
     */
    Betpal(4, "betpal"),

    PremiumCricket(5, "premium_cricket"),

    Unknown(99, "?");

    /**
     * A {@link Map} used to map productId's to {@link Product} instances
     */
    private static  final Map<Integer, Product> lookup;

    private static boolean warnedForUnknownProducer;

    static {
        lookup = new HashMap<>();
        for (Product product : EnumSet.allOf(Product.class)) {
            lookup.put(product.productId, product);
        }
    }

    /**
     * The id of the current {@link Product} member
     */
    private final int productId;

    private String recoveryName;

    /**
     * Initializes a new instance of the {@link Product} instance
     *
     * @param productId The Id of the created {@link Product} instance
     */
    Product(int productId, String recoveryName) {
        this.productId = productId;
        this.recoveryName = recoveryName;
    }

    /**
     * Gets a {@link Product} member specified by it's id
     *
     * @param id The id of the {@link Product} to get
     * @return a {@link Product} member specified by it's id
     */
    public static Product getProductFromId(int id) {
        Product p = lookup.get(id);
        if (p == null) {
            if (!warnedForUnknownProducer) {
                LoggerFactory.getLogger("sdk").warn("Received messages from Unknown producer: " + id);
                warnedForUnknownProducer = true;
            }
            p = Unknown;
        }
        return p;
    }

    /**
     * The product id as seen in messages
     *
     * @return the product id as seen in messages.
     */
    public int getId() {
        return productId;
    }

    /**
     * The name of this product as used in the API for recovery
     *
     * @return the name of this product as used in the API for recovery
     */
    public String getRecoveryName() {
        return recoveryName;
    }
}