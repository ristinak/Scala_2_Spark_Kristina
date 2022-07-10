package com.github.ristinak

import com.redis.RedisClient //

object Day10RedisClientTesting extends App {

  println("Testing Redis Client Capability")
  //get port and connection url from your Redis Cloud console configuration tab
  val port = 19428
  val url = "redis-19428.c257.us-east-1-3.ec2.cloud.redislabs.com"
  println(s"Will connect to Redis database at: $url")
  val dbName = "Scala2SparkCourse22"

  //  val pw = Some("This should not be public") //best practice would be to load int from enviroment variable TODO show how
  val pw = Some("G3gCvewndsgyX69jP0JD1iFWY5nqAecd")

  val r = new RedisClient(host = url, port, 0, secret = pw)

  r.set("myName", "Kristina")
  r.incr(key = "myCount") //so either initialize value to 1 or increment by 1
  //we print values directly from database
  println(r.get("myName"))
  //or save into a value/variable
  val myCounter = r.get("myCount")
  val actualCounter = myCounter.getOrElse("0").toInt
  println(s"My counter is at $myCounter -> $actualCounter")


  //TODO connect to your own database not mine :)
  //maybe test a few more commands incrBy or something else ?
}