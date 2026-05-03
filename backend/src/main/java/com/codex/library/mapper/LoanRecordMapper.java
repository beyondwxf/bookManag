package com.codex.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codex.library.entity.LoanRecord;
import java.util.List;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface LoanRecordMapper extends BaseMapper<LoanRecord> {

    @Update("""
            update loan_records
            set status = 'OVERDUE',
                updated_at = now()
            where deleted = 0
              and return_time is null
              and due_date < current_date()
              and status = 'BORROWED'
            """)
    int refreshOverdueStatus();

    @Select("""
            select date(borrow_time) as borrowDate, count(*) as borrowCount
            from loan_records
            where deleted = 0
            group by date(borrow_time)
            order by borrowDate desc
            limit 14
            """)
    List<java.util.Map<String, Object>> loanTrend();
}
