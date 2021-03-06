INSERT into AUDIT
SELECT TRACE_ID as "TRACE_ID",
ROWKEY as "ORDER_ID",
TIMESTAMPTOSTRING(ROWTIME, 'yyyy-MM-dd HH:mm:ss.SSS') as "TIMESTAMP",
'EnhancedWithSwitchServiceId' as "EVENT",
CAST (SWITCH_SERVICE_ID AS VARCHAR) as "ASSOCIATION"
FROM POST_SINK_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID;
