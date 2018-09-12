package com.mo9.raptor.engine.strategy.condition;

import com.mo9.raptor.engine.exception.InvalidConditionException;

public class Condition {

    private Scope scope;

    private ConditionValue value;

    private ConditionOperator operator;

    private Condition compositeCondition;

    private LogicalOperator compositeOperator;

    public Condition () {}

    public Condition (Scope scope, ConditionValue value, ConditionOperator operator) {
        this.scope = scope;
        this.value = value;
        this.operator = operator;
    }

    public boolean verify (Situation situation) throws InvalidConditionException {

        ConditionValue scopeValue = situation.get(this.scope);
        if (scopeValue == null) {
            return false;
        }

        boolean single = scopeValue.logicalEquals(this.value, situation);

        switch (this.operator) {
            case EQUAL: break;
            case NOT_EQUAL: {
                single = !single;
            } break;
                default: {
                    throw new InvalidConditionException("不能识别的条件运算符！运算符：" + this.operator);
                }
        }

        if (this.compositeOperator == null || this.compositeCondition == null) {
            return single;
        }

        switch (this.compositeOperator) {
            case AND: {
                return this.compositeCondition.verify(situation) && single;
            }
            case OR: {
                return this.compositeCondition.verify(situation) || single;
            }
                default: {
                    throw new InvalidConditionException("不能识别的复合运算符！运算符：" + this.compositeOperator);
                }
        }
    }

    public Condition and (Condition condition) {
        /** 若入参已设置复合条件，将被覆盖，TODO:抛异常 */
        condition.setCompositeOperator(LogicalOperator.AND);
        condition.setCompositeCondition(this);
        return condition;
    }

    public Condition or (Condition condition) {
        /** 若入参已设置复合条件，将被覆盖，TODO:抛异常 */
        condition.setCompositeOperator(LogicalOperator.OR);
        condition.setCompositeCondition(this);
        return condition;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public ConditionValue getValue() {
        return value;
    }

    public void setValue(ConditionValue value) {
        this.value = value;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }

    public Condition getCompositeCondition() {
        return compositeCondition;
    }

    public void setCompositeCondition(Condition compositeCondition) {
        this.compositeCondition = compositeCondition;
    }

    public LogicalOperator getCompositeOperator() {
        return compositeOperator;
    }

    public void setCompositeOperator(LogicalOperator compositeOperator) {
        this.compositeOperator = compositeOperator;
    }
}
