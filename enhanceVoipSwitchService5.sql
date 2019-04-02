CREATE STREAM STREAM_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID
WITH (VALUE_FORMAT='JSON', PARTITIONS=4,REPLICAS=3)
AS SELECT o.traceId, o.operatorId, o.orderId, o.serviceId as "SERVICEID", o.directoryNumber, o.operatorOrderId, v.switchServiceId, o.features
FROM REKEYED_FROM_MODIFICATION_INSTRUCTIONS_WITH_SERVICE o
LEFT JOIN T_FROM_VOIP_SWITCH_SERVICES_PARTITIONED_FOR_JOIN v
ON o.serviceId = v.serviceId;
