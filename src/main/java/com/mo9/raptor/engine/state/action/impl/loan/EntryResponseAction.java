package com.mo9.raptor.engine.state.action.impl.loan;

import com.mo9.raptor.engine.entity.PayOrderDetailEntity;
import com.mo9.raptor.engine.service.IPayOrderDetailService;
import com.mo9.raptor.engine.state.action.IAction;
import com.mo9.raptor.engine.state.event.impl.pay.EntryResponseEvent;
import com.mo9.raptor.engine.state.launcher.IEventLauncher;
import com.mo9.raptor.engine.structure.Unit;
import com.mo9.raptor.engine.structure.field.Field;
import com.mo9.raptor.engine.structure.field.FieldTypeEnum;
import com.mo9.raptor.engine.structure.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gqwu on 2018/4/4.
 * 入账响应行为-创建还款订单入账响应事件，并发送给还款订单状态机
 */
public class EntryResponseAction implements IAction {

    private static final Logger logger = LoggerFactory.getLogger(EntryResponseAction.class);

    private String payOrderId;

    private String orderId;

    private Item realItem;

    private Item entryItem;

    private String userCode;

    private String payCurrency;

    private IEventLauncher payEventLauncher;

    private IPayOrderDetailService payOrderDetailService;

    public EntryResponseAction(
            String payOrderId, String orderId, Item realItem, Item entryItem,
            String userCode, String payCurrency, IEventLauncher payEventLauncher, IPayOrderDetailService payOrderDetailService) {
        this.payOrderId = payOrderId;
        this.orderId = orderId;
        this.realItem = realItem;
        this.entryItem = entryItem;
        this.userCode = userCode;
        this.payCurrency = payCurrency;
        this.payEventLauncher = payEventLauncher;
        this.payOrderDetailService = payOrderDetailService;
    }

    @Override
    public void run(){

        // 创建明细
        List<PayOrderDetailEntity> entityList = new ArrayList<PayOrderDetailEntity>();
        for (Map.Entry<FieldTypeEnum, Unit> entry : entryItem.entrySet()) {
            FieldTypeEnum fieldTypeEnum = entry.getKey();
            Unit unit = entry.getValue();
            for (Field field : unit) {
                BigDecimal fieldNumber = field.getNumber();
                if (BigDecimal.ZERO.compareTo(fieldNumber) >= 0) {
                    continue;
                }
                Field realField = realItem.get(fieldTypeEnum).get(0);
                PayOrderDetailEntity entity = new PayOrderDetailEntity();
                entity.setOwnerId(userCode);
                entity.setDestType(field.getDestinationId());
                entity.setLoanOrderId(orderId);
                entity.setSourceType(field.getSourceId());
                entity.setPayOrderId(payOrderId);
                entity.setPayCurrency(payCurrency);
                entity.setItemType(entryItem.getItemType().name());
                entity.setRepayDay(entryItem.getRepayDate());
                entity.setField(fieldTypeEnum.name());
                entity.setShouldPay(realField.getNumber());
                entity.setPaid(field.getNumber());
                entity.create();
                entityList.add(entity);
            }
        }
        payOrderDetailService.saveItem(entityList);
        EntryResponseEvent event = new EntryResponseEvent(payOrderId, entryItem.sum());
        try {
            payEventLauncher.launch(event);
        } catch (Exception e) {
            logger.error("自动发送发起放款事件异常，事件：[{}]", event, e);
        }
    }

    @Override
    public String getActionType() {
        return this.getClass().getName();
    }

    @Override
    public String getOrderId() {
        return payOrderId;
    }
}
