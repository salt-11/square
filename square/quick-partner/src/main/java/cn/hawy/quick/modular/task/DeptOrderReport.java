package cn.hawy.quick.modular.task;


import cn.hawy.quick.modular.api.service.TDeptOrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class DeptOrderReport {

    @Autowired
    TDeptOrderReportService deptOrderReportService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cron() {
        System.out.println("DeptOrderReport执行 >>>>>每天凌晨2点执行！");
        deptOrderReportService.insertDeptOrderReport();
    }


}
