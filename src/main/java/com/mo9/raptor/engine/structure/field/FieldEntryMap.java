package com.mo9.raptor.engine.structure.field;

import java.math.BigDecimal;
import java.util.HashMap;


public class FieldEntryMap extends HashMap<EntryEnum, Field> {

    public FieldEntryMap() {
        this.put(EntryEnum.PAY_LOAN, new Field());
        this.put(EntryEnum.COUPON_LOAN, new Field());
        this.put(EntryEnum.PAY_STRATEGY, new Field());
        this.put(EntryEnum.COUPON_STRATEGY, new Field());
    }

    public BigDecimal fromPay () {
        return this.get(EntryEnum.PAY_LOAN).getNumber()
                .add(this.get(EntryEnum.PAY_STRATEGY).getNumber());
    }
}
