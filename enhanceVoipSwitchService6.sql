CREATE STREAM SINK_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID WITH (PARTITIONS=4,REPLICAS=3)
AS SELECT TRACEID as "TRACE_ID",
       OPERATORID as "OPERATOR_ID",
       ORDERID as "ORDER_ID",
       SERVICEID as "SERVICE_ID",
       DIRECTORYNUMBER as "DIRECTORY_NUMBER",
       OPERATORORDERID as "OPERATOR_ORDER_ID",
       SWITCHSERVICEID as "SWITCH_SERVICE_ID",
       FEATURES as "FEATURES"
FROM STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID PARTITION by ORDER_ID;
