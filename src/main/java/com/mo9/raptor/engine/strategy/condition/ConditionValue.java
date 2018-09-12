package com.mo9.raptor.engine.strategy.condition;

public class ConditionValue {

    private Mode mode;

    private String value;

    public ConditionValue() {}

    public ConditionValue(Mode mode, String value) {
        this.mode = mode;
        this.value = value;
    }

    public boolean logicalEquals(ConditionValue compareValue, Situation situation) {
        if (this.mode == compareValue.getMode()) {
            return this.value.equals(compareValue.getValue());
        } else {
            switch (this.mode) {
                case SCOPE: {
                    ConditionValue value = situation.get(Scope.instance(this.value));
                    return value.logicalEquals(compareValue, situation);
                }
                case VALUE: {
                    ConditionValue value = situation.get(Scope.instance(compareValue.getValue()));
                    return this.logicalEquals(value, situation);
                }
                    default:{

                        return false;
                    }
            }
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
