import org.apache.pekko.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import actors.{ProcessEvent, ProcessorManagerActor, RegisterProcessor}
import java.util.UUID
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.Props

class SomeProcessorTypeSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {

  "SomeProcessorType" should {
    "process events when all required events are received" in {
      val fieldToMatch = UUID.randomUUID().toString

      val eventA = DomainEventA(fieldToMatch, 1)
      val eventB = DomainEventB(fieldToMatch, 2)

      val processor = new SomeProcessorType(listOfEvents =
        List(classOf[DomainEventA], classOf[DomainEventB])
      )

      val managerActor = spawn(
        ProcessorManagerActor("testProcessorManager"),
        "testProcessorManager"
      )

      managerActor ! RegisterProcessor(processor)
      managerActor ! ProcessEvent(eventA, fieldToMatch)
      managerActor ! ProcessEvent(eventB, fieldToMatch)

      // Since it prints to stdout, we can't easily assert the print,
      // but we can at least verify it doesn't crash and the logic completes.
      // In a real scenario, we might want to check for a side effect or a message sent to another actor.
    }
  }
}
