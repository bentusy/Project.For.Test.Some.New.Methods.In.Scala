import java.util.concurrent.TimeoutException

import akka.actor
import akka.actor.Actor.Receive
import akka.actor._
import akka.util.{Timeout, ByteString}
import akka.pattern._
import scala.concurrent.duration._
import scala.collection.mutable.{HashMap=>MHM}
import scala.collection
import scala.concurrent.Await

object test extends App{

/*  var m:MHM[Int,  String]=MHM()
  def genStr(a: Int): String={
    "genStr"+a
  }

  m.+=(1 -> "hhSSSS")
  val s =   m.getOrElse(2, genStr(5))

  println("----------"+s)*/

  val system = ActorSystem("hostsystem")
  system.actorOf(Props(new MapReader()), name = "hello")
  system.actorOf(Props(new mapHandler()), name = "mapHandler")
//  system.actorOf(Props(new t1("55")), name = "usermanager2")

   println("getActorSelection")
  val user = system.actorSelection("/user/hello/")
  val user2 = system.actorSelection("/sssssaaas")
  println("user="+user)
  println("user="+user2)
  user ! "User Print it!"
  user2 ! "User2 Print it!"
//  user ! startPrintMap(5)
//  val user2 = system.actorSelection("/user/usermanager/")
//  val user3 = system.actorSelection()


/*
  val a = Identify("hello")
  println("send comand'_'")
  user ! "_"
  println("send comand2'='")
  user2 ! "="
  println("send comand'*'")
  user ! "*"
  println("send comand2'Ping'")
  user2 ! Ping("/user/usermanager3/", system)
  println("end of sends")*/



}

case class Pong()
case class Ping(s: String, context: ActorSystem)
case class getMapForLook()
case class eqMap(m1: MHM[Int, String])
case class startPrintMap(a: Int)

class t1(str: String ="aa") extends Actor{

  type a;

  def get(s: String): String ={

    "plus " + s + "   " + str
  }

  override def receive: Actor.Receive = {

    case id:String => {
      var i =0
      while(i<100) {
        print(id)
        val s = this.toString
        i+=1
      }

    }

    case Ping(s, context2) => {
      print("Ping")
      implicit val timeout = new Timeout(1 seconds)
      var as = context.actorSelection(s)
      val future = as ? Pong
      try {
        val result = Await.result(future, timeout.duration).asInstanceOf[String]
        print(result)
      } catch {
        case  _: TimeoutException => {
           println("Нет такого пользователя надо создать...")

          context2.actorOf(Props(new t1("55")), name = "usermanager3")
          as = context.actorSelection(s)
          val future = as ? Pong
          val result = Await.result(future, timeout.duration).asInstanceOf[String]
          print(result)

        }

      }
    }

    case Pong => {

      sender ! "Pong"
    }


}
}

class mapHandler() extends Actor {
  private[this] var m: MHM[Int, String] = MHM()
  var i = 0;
  while (i < 100) {
  m += (i -> ("Val"+i))
  i+=1
}



  override def receive: Actor.Receive = {

    case eqMap(m1)=>{
      if(m1==m)print("Мапы передаются по ссылкам") else  print("Мапы передаются не по ссылкам")
    }

    case getMapForLook=>{
    sender ! m
    }
  }
}

class MapReader extends Actor {
  override def receive: Actor.Receive = {
    case s:String=>{
           print(s)
    }
    case startPrintMap(a)=>{
      println("получаю ссылку на файл"+a)
      implicit val timeout = {
        new Timeout(1 seconds)
      }
      val as = context.actorSelection("/user/mapHandler")
      val future = as ? Pong
      val result = Await.result(future, timeout.duration).asInstanceOf[MHM[Int, String]]
      println("Сверяю ссылки")
      as !   eqMap(result)
      println("Начинаю перебирать Мар"+self)
      for(a <- result){
         print(a+" ")
      }                    }

  }
}