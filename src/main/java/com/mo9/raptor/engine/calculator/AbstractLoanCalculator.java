package com.mo9.raptor.engine.calculator;

import com.mo9.raptor.engine.entity.LoanOrderEntity;
import com.mo9.raptor.engine.exception.MergeException;
import com.mo9.raptor.engine.exception.UnSupportTimeDiffException;
import com.mo9.raptor.engine.structure.Scheme;
import com.mo9.raptor.engine.structure.item.Item;
import com.mo9.raptor.engine.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * 借贷订单计算器
 */
@Component
public abstract class AbstractLoanCalculator implements ILoanCalculator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLoanCalculator.class);

    /**
     * 初始账单
     */
    @Override
    public Scheme originScheme(LoanOrderEntity loanOrder) {

        Scheme originalScheme = new Scheme();

        /** TODO: */

        for (Map.Entry<Integer, Item> entry : originalScheme.entrySet()) {

            /** 设定还款日 */
            Item item = entry.getValue();
            Long repayDay = this.repayDay(entry.getKey(), loanOrder);
            item.setRepayDate(repayDay);
        }

        return originalScheme;
    }

    /**
     * 计划账单
     */
    @Override
    public Scheme planScheme(Long date, LoanOrderEntity loanOrder) {

        Scheme planScheme = new Scheme();

        date = TimeUtils.extractDateTime(date);

        return planScheme;
    }

    /**
     * 实时账单
     */
    @Override
    public Scheme realScheme(Long date, LoanOrderEntity loanOrder) throws UnSupportTimeDiffException, MergeException {

        Scheme scheme = this.planScheme(date, loanOrder);

        int passInterestDays = this.passInterestDay(date, loanOrder);


        return scheme;
    }

    /**
     * 入账处理
     */
    @Override
    public LoanOrderEntity schemeEntry(LoanOrderEntity loanOrder, Scheme originalScheme, Scheme realScheme, Scheme entryScheme) {


        return loanOrder;
    }

    /**
     * 默认还款日计算方式，n期还款日 = n期账单日 + 账期，例：第1期账单日为9月10号，账期为1个月，则还款日为10月10日
     */
    @Override
    public long repayDay(int installment, LoanOrderEntity loanOrder) {

        long billDay = this.bookDate(installment, loanOrder);
        Calendar repayDay = Calendar.getInstance();
        repayDay.setTimeInMillis(billDay);

        return repayDay.getTimeInMillis();

    }

    public int passInterestDay(long date, LoanOrderEntity loanOrder) throws UnSupportTimeDiffException {

        int dateInstallment = this.dateInstallment(date, loanOrder);

        if (dateInstallment < 0) {
            return 0;
        }

        if (dateInstallment == 1) {
            return TimeUtils.dateDiff(loanOrder.getLentTime(), date) + 1;
        }

        long billDate = this.bookDate(dateInstallment, loanOrder);

        return TimeUtils.dateDiff(billDate, date) + 1;
    }

    /**
     * 指定时间所在分期
     */
    public int dateInstallment(long date, LoanOrderEntity loanOrder) throws UnSupportTimeDiffException {
        return 1;
    }

    /**
     * 订单记账方式下，计算指定分期的记账日
     *
     * @param installment                 指定分期
     * @param lentTime                    订单放款成功时间
     * @param installmentTerm             订单每期的借款期限，例：12（月、日）、1（年），等。年、月、日为相应的期限单位
     * @param installmentTermCalendarUnit 订单每期借款期限的日历单位，年、月、日
     * @return
     */
    public long bookDateByLoan(int installment, long lentTime, int installmentTerm, int installmentTermCalendarUnit) {

        Calendar bookDate = Calendar.getInstance();
        bookDate.setTimeInMillis(TimeUtils.extractDateTime(lentTime));
        bookDate.add(installmentTermCalendarUnit, (installment - 1) * installmentTerm);

        return bookDate.getTimeInMillis();
    }

    /**
     * 账单记账方式下，计算指定分期的记账日
     *
     * @param installment                 指定分期
     * @param lentTime                    订单放款成功时间
     * @param installmentTerm             订单每期的借款期限，例：12（月、日）、1（年），等。年、月、日为相应的期限单位
     * @param installmentTermCalendarUnit 订单每期借款期限的日历单位，年、月、日
     * @param bookDay                     用户每月记账日
     * @return
     */
    public long bookDateByBill(int installment, long lentTime, int installmentTerm, int installmentTermCalendarUnit, int bookDay) {

        Calendar lentDate = Calendar.getInstance();
        lentDate.setTimeInMillis(TimeUtils.extractDateTime(lentTime));

        Calendar bookDate = Calendar.getInstance();
        bookDate.setTimeInMillis(TimeUtils.extractDateTime(lentTime));
        bookDate.set(Calendar.DATE, bookDay);

        if (lentDate.after(bookDate)) {
            bookDate.add(Calendar.MONTH, 1);
        }

        bookDate.add(installmentTermCalendarUnit, (installment - 1) * installmentTerm);

        return bookDate.getTimeInMillis();
    }

    /**
     * 记账日
     */
    public long bookDate(int installment, LoanOrderEntity loanOrder) {

        Calendar billDate = Calendar.getInstance();
        billDate.setTimeInMillis(TimeUtils.extractDateTime(loanOrder.getLentTime()));

        return billDate.getTimeInMillis();
    }
}