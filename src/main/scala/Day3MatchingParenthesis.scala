package com.github.ristinak

object Day3MatchingParenthesis extends App {
  def areParenthesisMatching(text:String):Boolean = {
  //TODO check if all opening ( are properly )
  // () -> true
  // (()()) -> true
  // (())() -> true
  // )()( -> false
  // ((()) -> false
  //TODO bonus if you can also ignore an non parenthesis
  //only thing here i think break or early return might help

    val parentheses = text.filter(char => char=='(' | char==')')
    if (parentheses(0) == ')') return false
    val parenthesisStack = scala.collection.mutable.Stack[Char]()
    for (char <- parentheses) {
      if (char == '(')
      {
        parenthesisStack.push(char)
      }
      else try {
        parenthesisStack.pop()
      }
      catch {
        case e: NoSuchElementException => return false
      }
    }

    if (parenthesisStack.isEmpty) true else false

  }

  def AlternativeParenthesisMatching(text:String):Boolean = {
    val parentheses = text.filter(char => char=='(' | char==')')
    if (parentheses(0) == ')') return false
    val openingCount = parentheses.count(_ == '(')
    val closingCount = parentheses.count(_ == ')')
    if (openingCount != closingCount) false else true
  }

  println(areParenthesisMatching("()((()))))"))
  println(AlternativeParenthesisMatching("()((()))))"))

}

