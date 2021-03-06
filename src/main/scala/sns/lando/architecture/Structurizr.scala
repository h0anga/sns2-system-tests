package sns.lando.architecture

import com.structurizr.Workspace
import com.structurizr.api.StructurizrClient
import com.structurizr.model.{InteractionStyle, Tags}
import com.structurizr.view.{Routing, Shape}

//object Structurizr extends App {
object Structurizr {

  private val WORKSPACE_ID = 43191
  // Don't push the real values for these!
  private val API_KEY = ""
  private val API_SECRET = ""
  //

  private val MICROSERVICE_TAG = "Microservice"
  private val KAFKA_TOPIC_TAG = "Kafka Topic"
  private val KSQL_TAG = "Kafka KSQL"
  private val KAFKA_CONNECT_TAG = "Kafka Connect"
  private val DATASTORE_TAG = "Database"

  // a Structurizr workspace is the wrapper for a software architecture model, views and documentation
  val workspace = new Workspace("SNS 2.0", "Event Driven Netstream on Kafka")
  val model = workspace.getModel


  val mySoftwareSystem = model.addSoftwareSystem("Customer Information System", "Stores information ")
  val snsNorth = model.addPerson("SNS North", "SNS North")
  val snsNorthAdapter = mySoftwareSystem.addContainer("SNS North Adapter", "Receives HTTP requests and hands them to kafka", "Java")

  val incomingOperatorMessagesTopic = mySoftwareSystem.addContainer("incoming.op.msgs", "All messages received from SNS North in XML", "Kafka, XML")
  incomingOperatorMessagesTopic.addTags(KAFKA_TOPIC_TAG)

  val xmlToJsonConverter = mySoftwareSystem.addContainer("XML to JSON Converter", "Turns any XML into equivalent JSON", "Scala")
  xmlToJsonConverter.addTags(MICROSERVICE_TAG)

  val modifyOperatorMessagesTopic = mySoftwareSystem.addContainer("modify.op.msgs", "All messages received from SNS North in JSON", "Kafka, JSON")
  modifyOperatorMessagesTopic.addTags(KAFKA_TOPIC_TAG)

  val instructionsStream1 = mySoftwareSystem.addContainer("Modify Voice Stream", "Parses Modify Voice Instruction", "KSQL")
  instructionsStream1.addTags(KSQL_TAG)

  val rawVoipInstructionsTopic = mySoftwareSystem.addContainer("RAW_VOIP_INSTRUCTIONS", "Parsed Json representing Modify Voice Features Instructions", "Kafka, JSON")
  rawVoipInstructionsTopic.addTags(KAFKA_TOPIC_TAG)

  val serviceEnricher = mySoftwareSystem.addContainer("Service Enrichment", "Enriches message with DN", "Scala")
  serviceEnricher.addTags(MICROSERVICE_TAG)

  val enrichedModificationInstructionsWithServiceTopic = mySoftwareSystem.addContainer("enriched.modification.instructions.with.dn", "Modify Voice message enhanced with DN", "Kafka, JSON")
  enrichedModificationInstructionsWithServiceTopic.addTags(KAFKA_TOPIC_TAG)

  val ksqlSwitchEnhancerStream = mySoftwareSystem.addContainer("Switch Enrichment", "Joins switchId into Modify Voice Features", "KSQL")
  ksqlSwitchEnhancerStream.addTags(KSQL_TAG)

  val sinkModifyVoipInstructionsWithSwitchIdTopic = mySoftwareSystem.addContainer("SINK_MODIFY_VOIP_INSTRUCTIONS_WITH_SWITCH_ID", "Modify Voice Features enhanced with SwitchId", "Kafka, JSON")
  sinkModifyVoipInstructionsWithSwitchIdTopic.addTags(KAFKA_TOPIC_TAG)

  val knitwareConverter = mySoftwareSystem.addContainer("Knitware Converter", "Turns Modify Voice Features Instruction into a Knitware message", "Scala")
  knitwareConverter.addTags(MICROSERVICE_TAG)


  val servicesDatabase = mySoftwareSystem.addContainer("Services Database", "Stores service information", "Oracle")
  servicesDatabase.addTags(DATASTORE_TAG)

  val jdbcServicesSource = mySoftwareSystem.addContainer("Services JDBC Connector", "Extracts services data", "Oracle")
  jdbcServicesSource.addTags(KAFKA_CONNECT_TAG)
  val jdbcServicesSourceTopic = mySoftwareSystem.addContainer("services", "Service data", "Kafka, JSON")
  jdbcServicesSourceTopic.addTags(KAFKA_TOPIC_TAG)

  val jdbcSwitchSource = mySoftwareSystem.addContainer("Switch JDBC Connector", "Extracts switch data", "Oracle")
  jdbcSwitchSource.addTags(KAFKA_CONNECT_TAG)
  val jdbcSwitchSourceTopic = mySoftwareSystem.addContainer("voip-switch-services", "Switch data", "Kafka, JSON")
  jdbcSwitchSourceTopic.addTags(KAFKA_TOPIC_TAG)


//  val customerService = mySoftwareSystem.addContainer("Customer Service", "The point of access for customer information.", "Java and Spring Boot")
//  customerService.addTags(MICROSERVICE_TAG)
//
//  val reportingService = mySoftwareSystem.addContainer("Reporting Service", "Creates normalised data for reporting purposes.", "Ruby")
//  reportingService.addTags(MICROSERVICE_TAG)
//  val reportingDatabase = mySoftwareSystem.addContainer("Reporting Database", "Stores a normalised version of all business data for ad hoc reporting purposes.", "MySQL")
//  reportingDatabase.addTags(DATASTORE_TAG)
//
//  val auditService = mySoftwareSystem.addContainer("Audit Service", "Provides organisation-wide auditing facilities.", "C# .NET")
//  auditService.addTags(MICROSERVICE_TAG)
//  val auditStore = mySoftwareSystem.addContainer("Audit Store", "Stores information about events that have happened.", "Event Store")
//  auditStore.addTags(DATASTORE_TAG)

//  val messageBus = mySoftwareSystem.addContainer("Message Bus", "Transport for business events.", "RabbitMQ")
//  messageBus.addTags(MESSAGE_BUS_TAG)
  jdbcServicesSource.uses(servicesDatabase, "Sources Service data from", "JDBC")
  jdbcServicesSource.uses(jdbcServicesSourceTopic, "Writes to")
  jdbcSwitchSource.uses(servicesDatabase, "Sources Switch data from", "JDBC")
  jdbcSwitchSource.uses(jdbcSwitchSourceTopic, "Writes to")

  snsNorth.uses(snsNorthAdapter, "Sends Commands to", "XML/HTTPS", InteractionStyle.Synchronous)
  snsNorthAdapter.uses(incomingOperatorMessagesTopic, "Writes to")
  xmlToJsonConverter.uses(incomingOperatorMessagesTopic, "Reads from")
  xmlToJsonConverter.uses(modifyOperatorMessagesTopic, "Writes to")
  instructionsStream1.uses(modifyOperatorMessagesTopic, "Reads from")
  instructionsStream1.uses(rawVoipInstructionsTopic, "Writes to")
  serviceEnricher.uses(rawVoipInstructionsTopic, "Reads from")
  serviceEnricher.uses(jdbcServicesSourceTopic, "Reads from")
  serviceEnricher.uses(enrichedModificationInstructionsWithServiceTopic, "Writes to")
  ksqlSwitchEnhancerStream.uses(enrichedModificationInstructionsWithServiceTopic, "Reads from")
  ksqlSwitchEnhancerStream.uses(jdbcSwitchSourceTopic, "Reads from")
  ksqlSwitchEnhancerStream.uses(sinkModifyVoipInstructionsWithSwitchIdTopic, "Writes to")
  knitwareConverter.uses(sinkModifyVoipInstructionsWithSwitchIdTopic, "Reads from")




//  customerService.uses(messageBus, "Sends customer update events to", "", InteractionStyle.Asynchronous)
//  customerService.uses(customerDatabase, "Stores data in", "JDBC", InteractionStyle.Synchronous)
//  customerService.uses(snsNorthAdapter, "Sends events to", "WebSocket", InteractionStyle.Asynchronous)
//  messageBus.uses(reportingService, "Sends customer update events to", "", InteractionStyle.Asynchronous)
//  messageBus.uses(auditService, "Sends customer update events to", "", InteractionStyle.Asynchronous)
//  reportingService.uses(reportingDatabase, "Stores data in", "", InteractionStyle.Synchronous)
//  auditService.uses(auditStore, "Stores events in", "", InteractionStyle.Synchronous)

  val views = workspace.getViews

  val containerView = views.createContainerView(mySoftwareSystem, "Containers", null)
  containerView.addAllElements()

  val dynamicView = views.createDynamicView(mySoftwareSystem, "CustomerUpdateEvent", "This diagram shows what happens when a customer updates their details.")
//  dynamicView.add(snsNorth, snsNorthAdapter)
//  dynamicView.add(snsNorthAdapter, customerService)
//
//  dynamicView.add(customerService, customerDatabase)
//  dynamicView.add(customerService, messageBus)
//
//  dynamicView.startParallelSequence()
//  dynamicView.add(messageBus, reportingService)
//  dynamicView.add(reportingService, reportingDatabase)
//  dynamicView.endParallelSequence()
//
//  dynamicView.startParallelSequence()
//  dynamicView.add(messageBus, auditService)
//  dynamicView.add(auditService, auditStore)
//  dynamicView.endParallelSequence()
//
//  dynamicView.startParallelSequence()
//  dynamicView.add(customerService, "Confirms update to", snsNorthAdapter)
//  dynamicView.endParallelSequence()

  val styles = views.getConfiguration.getStyles
  styles.addElementStyle(Tags.ELEMENT).color("#000000")
  styles.addElementStyle(Tags.PERSON).background("#ffbf00").shape(Shape.Person)
  styles.addElementStyle(Tags.CONTAINER).background("#facc2E")
  styles.addElementStyle(KAFKA_TOPIC_TAG).width(500).shape(Shape.Pipe)
  styles.addElementStyle(MICROSERVICE_TAG).shape(Shape.Box)
  styles.addElementStyle(KSQL_TAG).shape(Shape.Hexagon)
  styles.addElementStyle(DATASTORE_TAG).background("#f5da81").shape(Shape.Cylinder)
  styles.addRelationshipStyle(Tags.RELATIONSHIP).routing(Routing.Orthogonal)

  styles.addRelationshipStyle(Tags.ASYNCHRONOUS).dashed(true)
  styles.addRelationshipStyle(Tags.SYNCHRONOUS).dashed(false)

  val client = new StructurizrClient(API_KEY, API_SECRET)
  client.putWorkspace(WORKSPACE_ID, workspace)

}