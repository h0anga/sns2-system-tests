INSERT into AUDIT
SELECT TRACEID as "TRACE_ID",
ROWKEY as "ORDER_ID",
TIMESTAMPTOSTRING(ROWTIME, 'yyyy-MM-dd HH:mm:ss.SSS') as "TIMESTAMP",
'EnhancedWithDN' as "EVENT",
DIRECTORYNUMBER as "ASSOCIATION"
FROM FROM_ENRICHED_MODIFICATION_INSTRUCTIONS_WITH_SERVICE;
