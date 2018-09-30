package com.mo9.raptor.engine.structure;

import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.structure.field.EntryEnum;

import java.math.BigDecimal;
import java.util.HashMap;


public class SchemeEntryMap extends HashMap<EntryEnum, Scheme> {

    public SchemeEntryMap() {
        Scheme payLoanScheme = new Scheme();
        Scheme couponLoanScheme = new Scheme();
        Scheme payStrategyScheme = new Scheme();
        Scheme couponStrategyScheme = new Scheme();

        this.put(EntryEnum.PAY_LOAN, payLoanScheme);
        this.put(EntryEnum.COUPON_LOAN, couponLoanScheme);
        this.put(EntryEnum.PAY_STRATEGY, payStrategyScheme);
        this.put(EntryEnum.COUPON_STRATEGY, couponStrategyScheme);
    }
}
