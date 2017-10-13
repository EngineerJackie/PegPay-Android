package com.pegasus.pegpay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Zed on 4/25/2016.
 */
public enum TransactionType {
    TOPUP, AIRTIME, MERCHANT, BILL, SENT;

    private static final List<TransactionType> values = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int size = values.size();
    private static final Random random = new Random();

    public static TransactionType randomType()  {
        return values.get(random.nextInt(size));
    }
}
