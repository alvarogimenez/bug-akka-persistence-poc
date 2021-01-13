import java.io.File

import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior, SupervisorStrategy}
import akka.persistence.cassandra.testkit.CassandraLauncher
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, ReplyEffect}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Main {

  def main(args: Array[String]): Unit = {
    startCassandraDatabase()
    ActorSystem(Main(), "test", ConfigFactory.load())
  }

  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val testActor = context.spawnAnonymous(TestAggregate("test"))
      testActor.tell(TestAggregate.Create(101))
      Behaviors.empty
    }

  def startCassandraDatabase(): Unit = {
    val databaseDirectory = new File("target/cassandra-db")
    CassandraLauncher.start(
      cassandraDirectory = databaseDirectory,
      configResource = CassandraLauncher.DefaultTestConfigResource,
      clean = true,
      port = 9042
    )
  }
}

object TestAggregate {
  sealed trait Command
  case class Create(nrOfEvents:Int) extends Command

  sealed trait Event
  case object SomeEvent extends Event

  case class State()

  def apply(id: String): Behavior[Command] =
    EventSourcedBehavior
      .withEnforcedReplies[Command, Event, State](
        PersistenceId("Aggregate", id),
        State(),
        (state, command) => handleCommand(state, command),
        (state, event) => handleEvent(state, event))
      .onPersistFailure(
        SupervisorStrategy.restartWithBackoff(200.millis, 5.seconds, 0.1))

  private def handleCommand(
      state: State,
      command: Command
  ): ReplyEffect[Event, State] =
    command match {
      case Create(nrOfEvents) =>
        Effect
          .persist(List.fill(nrOfEvents)(SomeEvent))
          .thenNoReply()
    }

  private def handleEvent(state: State, event: Event): State = {
    event match {
      case _ => state
    }
  }
}
