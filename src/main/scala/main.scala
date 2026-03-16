import org.apache.pekko.actor.typed.{
  ActorRef,
  ActorSystem,
  Props,
  SpawnProtocol
}
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.util.Timeout
import processors.BaseProcessor
import actors.{
  CborSerializable,
  ProcessEvent,
  ProcessorManagerActor,
  RegisterProcessor
}
import org.apache.pekko.Done

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

final case class DomainEventA(myPropertyA: String, myPropertyB: Int)
    extends CborSerializable
final case class DomainEventB(myPropertyX: String, myPropertyY: Int)
    extends CborSerializable

class SomeProcessorType(listOfEvents: List[Any])
    extends BaseProcessor(listOfEvents):

  def process(): Option[Done] = {
    for
      eventA <- getEventByType[DomainEventA]
      eventB <- getEventByType[DomainEventB]
      _ = println(s"<< all events received should send new command here >>")
    yield Done
  }

@main
def main(): Unit = {
  val system: ActorSystem[SpawnProtocol.Command] =
    ActorSystem(SpawnProtocol(), "TaggedActorsTyped")

  val field_to_match_1 = UUID.randomUUID().toString
  val field_to_match_2 = UUID.randomUUID().toString

  val eventA = DomainEventA(field_to_match_1, 1)
  val eventB = DomainEventB(field_to_match_1, 2)

  val processor = new SomeProcessorType(listOfEvents =
    List(classOf[DomainEventA], classOf[DomainEventB])
  )

  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler = system.scheduler

  val managerActorFuture = system.ask[ActorRef[Any]](replyTo =>
    SpawnProtocol.Spawn(
      ProcessorManagerActor("processorManager"),
      "processorManager",
      Props.empty,
      replyTo
    )
  )
  val managerActor = Await.result(managerActorFuture, timeout.duration)
  println("Manager actor spawned")
  managerActor ! RegisterProcessor(processor)
  managerActor ! ProcessEvent(eventA, field_to_match_1)
  managerActor ! ProcessEvent(eventB, field_to_match_1) // Use same ID as event
  system.terminate()
}
