package com.github.ristinak

import CassandraExample.{getClusterSession, rawQuery, runQuery}

import com.datastax.driver.core.ResultSet

import scala.collection.convert.ImplicitConversions.`iterable AsScalaIterable`

object Day15CassandraCRUD extends App {

  //TODO store host and port also in System Enviroment so no need for outside parties to know your host address
  val host = "cassandra-1255d130-ristinak-37bf.aivencloud.com" //your url will be slightly different
  val port = 17638 //port might also be a different
  val username = "avnadmin" //most likely the same
  val password = scala.util.Properties.envOrElse("CASSANDRA_PW", "na")
  //  val caPath = "C:\\certs\\"
  val caPath = "./src/resources/certs/ca.pem" //you need to download your own cert, do not commit to GIT!!
  println("Opening up a connection to Cassandra cluster")
  val (cluster, session) = getClusterSession(host=host,port=port,username=username,password=password, caPath=caPath)

  //so instead of vs_keyspace we could use any name for our database (keyspace)
  val keyspaceCreationQuery =
    """
      |CREATE KEYSPACE IF NOT EXISTS
      |vs_keyspace WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'aiven' : 3 };
      |""".stripMargin

  //should do nothing if keyspace is already present
  rawQuery(session, keyspaceCreationQuery)

  //dropping table from vs_keyspace keyspace/database
  //  session.execute("DROP TABLE IF EXISTS vs_keyspace.users_by_country")

  //will do nothing if users_by_country already exist
  val tableCreationQuery =
    """
      |CREATE TABLE IF NOT EXISTS users_by_country (
      |    country text,
      |    user_email text,
      |    first_name text,
      |    last_name text,
      |    age int,
      |    PRIMARY KEY ((country), user_email)
      |)
      |""".stripMargin

  runQuery(session, "vs_keyspace", tableCreationQuery)


  //we need to convert our default int (4bytes) to 2 byte Short because that Column requires 2 byte integer
  //  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
  //    " VALUES (?, ?,?,?,?)", "US", "john@example.com", "John","Wick",55.toShort)
  //  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
  //    " VALUES (?, ?,?,?,?)", "UK", "mrbean@example.com", "Rowan","Atkinson",65.toShort)
  //  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
  //    " VALUES (?, ?,?,?,?)", "LV", "kk@example.com", "Krišjānis","Kariņš",60.toShort)

  case class User(
                 country: String,
                 user_email: String,
                 first_name: String,
                 last_name: String,
                 age: Int
                 )

  def userResultToUser(resultRow: com.datastax.driver.core.Row): User = {
//    val resultArray = resultRow.toString.split(", ")
//    val country: String = resultArray(0).substring(4)
//    val user_email: String = resultArray(1)
//    val first_name: String = resultArray(3)
//    val last_name: String = resultArray(4).dropRight(1)
//    val age: Int = resultArray(2).toInt
  val country = resultRow.getString("country")
  val user_email = resultRow.getString("user_email")
  val first_name = resultRow.getString("first_name")
  val last_name = resultRow.getString("last_name")
  val age = resultRow.getInt("age")
    User(country, user_email, first_name, last_name, age)
  }

  //if column is regular int then we give it regular int of course
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "US", "john@example.com", "John","Wick",55)
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "UK", "mrbean@example.com", "Rowan","Atkinson",65)
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "UK", "boris@example.com", "Boris","Johnson",58)
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "LV", "kk@example.com", "Krišjānis","Kariņš",60)
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "LV", "eriks@example.com", "Eriks","Johnsons",39)
  session.execute("INSERT INTO vs_keyspace.users_by_country (country,user_email,first_name,last_name,age)" +
    " VALUES (?,?,?,?,?)", "LT", "ingrida@example.com", "Ingrida","Šimonytė",45)
  //unlike SQL where primary key is usually inserted automatically the above queries would keep adding more data
  //here we just have the 4 rows (and also 3 partitions)

  println("All users:")
  val userResults = session.execute("SELECT * FROM vs_keyspace.users_by_country")
  userResults.forEach(row => println(row))

  //TODO add 2 more users, one from LV, one from LT
  //return Latvian users
  //return Lithuanian users
  //ideally you would not only print but save the users into a case class User - we can look at that later

  println("\nLatvian user(s):")
  val latvianUserResults = session.execute("SELECT * FROM vs_keyspace.users_by_country WHERE country = 'LV'")
  val latvianUsers = latvianUserResults.map(resultRow => userResultToUser(resultRow)).toArray
//  latvianUserResults.forEach(row => println(row))
  latvianUsers.foreach(user => println(user))

  println("\nLithuanian user(s):")
  val lithuanianUserResults = session.execute("SELECT * FROM vs_keyspace.users_by_country WHERE country = 'LT'")
//  lithuanianUsers.forEach(row => println(row))
  val lithuanianUsers = lithuanianUserResults.map(resultRow => userResultToUser(resultRow)).toArray
  lithuanianUsers.foreach(user => println(user))

  println("Will close our Cassandra Cluster")
  cluster.close()
  println("Connection should be closed now")
}

