import com.mo9.raptor.risk.entity.TRiskCallLog;
import com.mo9.raptor.risk.entity.TRiskTelInfo;
import com.mo9.raptor.risk.repo.RiskCallLogRepository;
import com.mo9.raptor.risk.service.RiskCallLogService;
import com.mo9.raptor.risk.service.RiskTelInfoService;
import org.junit.Test;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author wtwei .
 * @date 2018/9/17 .
 * @time 10:15 .
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RiskDataSourceTest extends BaseTest{
    
    @Resource
    private RiskCallLogService riskCallLogService;
    @Resource
    private RiskTelInfoService riskTelInfoService;
    
    
    @Test
    public void save(){
        for (int i = 0; i < 1000; i++) {
            TRiskCallLog callLog = new TRiskCallLog();
            callLog.setMobile("1590000100" + i);
            callLog.setCallTel("10010");
            riskCallLogService.save(callLog);
        }
        
    }
    
    @Test
    public void saveTelInfo(){
        for (int i = 0; i < 1000; i++) {
            TRiskTelInfo callLog = new TRiskTelInfo();
            callLog.setMobile("1590000100" + i);
            callLog.setAddress("浦东");
            riskTelInfoService.save(callLog);
        }
    }
}
