package models

/**
 * Created with IntelliJ IDEA.
 * User: antoine
 * Date: 06/06/12
 * Time: 21:02
 */

object Forms{

  case class Form(val fields:Field*)

  case class Field(val name:String, val fieldType:FieldType=Text(), val constraint:List[Constraint]=List()){
    def ofType(fType:FieldType)=this.copy(fieldType=fType)
    def withConstraint(c:Constraint)=this.copy(constraint=c::constraint)
  }

  sealed trait FieldType{
    def className:String
  }

  case class Text(className:String="text") extends FieldType
  case class Date(className:String="date") extends FieldType
  case class Integer(className:String="int") extends FieldType
  case class Decimal(className:String="decimal") extends FieldType

  def text=Text()
  def date=Date()
  def integer=Integer()
  def decimal=Decimal()

  sealed trait Constraint

  case class IsMandatory() extends Constraint
  case class MinLen(val size:Int) extends Constraint
  case class MaxLen(val size:Int) extends Constraint
  case class GreaterThan(val nb:Int) extends Constraint
  case class LessThan(val nb:Int) extends Constraint

  def isMandatory=IsMandatory()
  def minLen(nb:Int)=MinLen(nb)
  def maxLen(nb:Int)=MaxLen(nb)
  def greaterThan(nb:Int)=GreaterThan(nb)
  def lessThan(nb:Int)=LessThan(nb)

}

