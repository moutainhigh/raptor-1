package com.mo9.raptor.engine.service;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.enums.ResCodeEnum;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 贷款订单service
 * Created by xzhang on 2018/7/6.
 */
public interface ILoanOrderService {

    /** 根据借款订单号，和还款 */

    /**
     * 查询用户在贷订单
     * 会刷新所有预下单订单
     * @param userCode  用户
     * @return  订单
     */
    List<LoanOrderEntity> listUserLoaningOrders(String userCode);

    /**
     * 计算orderId应付的地址生成费
     * @param userCode
     * @param orderId
     * @return
     */
    BigDecimal getShouldPayAddressFee(String userCode, String orderId) throws Exception;

    /**
     * 计算orderId总共应付的地址生成费
     * @param userCode
     * @param orderId
     * @return
     */
    BigDecimal getAddressFee(String userCode, String orderId) throws Exception;

    /**
     * 查询所有已放款订单
     * 会刷新所有预下单订单
     * @return  订单
     */
    List<LoanOrderEntity> listLentOrders();

    /**
     * 根据时区查询所有已放款订单
     * 会刷新所有预下单订单
     * @param timeZones  时区集合
     * @return  订单
     */
    List<LoanOrderEntity> listLentOrdersByTimeZones(List<Integer> timeZones);

    /**
     * 查询用户已放款订单
     * 会刷新所有预下单订单
     * @param userCode  用户
     * @return  订单
     */
    List<LoanOrderEntity> listUserLentOrders(String userCode);

    /**
     * 根据订单号查订单
     * 会刷新所有预下单订单
     * @param orderIds
     * @return
     */
    List<LoanOrderEntity> listByOrderIds(List<String> orderIds);

    /**
     * 根据用户和状态查询订单
     * 会刷新所有预下单订单
     * @param userCode  用户
     * @param statuses  状态
     * @return  订单
     */
    List<LoanOrderEntity> listUserOrderByStatuses(String userCode, List<String> statuses);

    /**
     * 根据订单号获取
     * 会刷新所有预下单订单
     * @param orderId  订单号
     * @return         订单
     */
    LoanOrderEntity getByOrderId(String orderId);

    /**
     * 获取预下单订单
     * @param userCode  订单号
     * @return         订单
     */
    List<LoanOrderEntity> getByPreLoanOrders(String userCode);


    /**
     * 检查用户额度是否足够
     * 1. 查询用户所有的抵押物
     * 2. 调用风控接口
     * 3. 风控数据与本地信用数据比较
     * @param userCode          用户唯一编号
     * @param anchorNumber      锚定金额
     * @param totalCollaterals  用户目前所有的抵押物
     * @param loanOrderEntities 用户目前所有的抵押物
     * @return                  是否通过检查
     */
    ResCodeEnum checkUserCreditLevel(String userCode, BigDecimal anchorNumber, Map<String, BigDecimal> totalCollaterals, List<LoanOrderEntity> loanOrderEntities) throws Exception;


    /**
     * 创建预借款订单
     * @param orderId      订单号
     * @param userCode     用户
     * @return             创建后的借款订单
     */
    LoanOrderEntity loanOrderCheckFlow(String orderId, String userCode) throws Exception;

    /**
     * 保存订单
     */
    LoanOrderEntity save(LoanOrderEntity loanOrder);


    /**
     * 获得一个可计算的借款订单
     * @param currency
     * @param amount
     * @return
     */
    List<LoanOrderEntity> getCalculableLoanOrder(String currency, BigDecimal amount);

}
