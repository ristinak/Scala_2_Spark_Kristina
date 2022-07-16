package com.github.ristinak

import org.neo4j.driver.Values.parameters
import org.neo4j.driver.{AuthTokens, Config, GraphDatabase}

import scala.collection.mutable.ArrayBuffer

object Day16Neo4JConnection extends App {

  case class Movie(id: Int,
                   released: Int,
                   tagline: String,
                   title: String)

  println("Let's connect to our Neo4J database!")

  val noSSL = Config.builder().build() //so standard default configuration without SSL connection
  val pw = scala.util.Properties.envOrElse("NEO4J_PW", "na")
  val user = "neo4j"
  val uri = "neo4j+s://4a6d157f.databases.neo4j.io"
  val db = "Movies"


  val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pw), noSSL) // <p
  println("Opening Session")
  val session = driver.session

  val cypherQuery = "MATCH (m:Movie) " + "RETURN m as movie, id(m) as id"
  val result = session.run(cypherQuery, parameters())

  //TODO read result(s)
  val arrayBuffer = ArrayBuffer[Movie]()
  while (result.hasNext) {
    val record = result.next //we know this will not fail because we just checked with hasNext
    val movie = record.get("movie")
    val movieObject = Movie(
//                      record.get("id").asInt(),
                      movie.asNode().id().toInt, // alternative to the above
                      movie.get("released").asInt(),
                      movie.get("tagline").asString(),
                      movie.get("title").asString())
//    arrayBuffer += s"${movie.get("title")} - ${movie.get("released")} : ${movie.get("tagline")}"
    arrayBuffer += movieObject
  }

  println("Closing Session")
  session.close() //important to close the session

  val movies = arrayBuffer.toArray
  println(s"We got ${movies.length} movies!")
//  movies.take(5).foreach(println)

  val sortedByYear = movies.sortBy(_.released)
  sortedByYear.take(10).foreach(println)

  //TODO set up your own Movies database on https://neo4j.com/cloud/platform/aura-graph-database/?ref=neo4j-home-hero
  //TODO get all movies as case classes - with all attributes - title, released,tagline and id - i think that is it
  //TODO print earliest 10 movies in this database
}

