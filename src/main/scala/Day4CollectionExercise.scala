package com.github.ristinak

object Day4CollectionExercise extends App {
  println("Day 4 Exercise on Partial Functions and collect")

  val numbers = (-5 to 28).toArray

  val getEvenSquare: PartialFunction[Int, Int] = {
    case x: Int if (x % 2 == 0) && (x > 0) => x*x
  }

  val getOddCube: PartialFunction[Int, Int] = {
    case x: Int if (x % 2 != 0) && (x > 0) => x*x*x
  }

  val doPositives = getEvenSquare orElse getOddCube
  val processedNumbers = numbers collect doPositives
  println(processedNumbers.mkString(","))

}

