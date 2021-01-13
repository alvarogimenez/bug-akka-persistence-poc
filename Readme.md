## Source code to illustrate a bug in Akka Persistence Cassandra

To run the code execute:

```
sbt run
```

The code fails when trying to persist 101 events (`akka.persistence.cassandra.journal.max-message-batch-size` is set to 100) with the following exception:

```
java.util.NoSuchElementException: head of empty list
        at scala.collection.immutable.Nil$.head(List.scala:469)
        at scala.collection.immutable.Nil$.head(List.scala:466)
        at akka.persistence.cassandra.journal.CassandraJournal.writeMessages(CassandraJournal.scala:358)
        at akka.persistence.cassandra.journal.CassandraJournal.rec$1(CassandraJournal.scala:276)
        at akka.persistence.cassandra.journal.CassandraJournal.$anonfun$asyncWriteMessages$8(CassandraJournal.scala:276)
```