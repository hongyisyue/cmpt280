/* File Name: Fibonacci.java
 Author: David Kreiser
 Class: CMPT 340 Assignment 4
 Contents: Sample solution for Assignment 4 Problem 1
 */

package fibonacci;

import java.util.Scanner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class Fibonacci {
    
    // Number of seconds we are willing to wait for an answer
    private static final int TIMEOUT = 500;
    
    // Define static classes for any actors that will be needed
    private static class Worker extends UntypedActor {
        public void onReceive(Object message) {
            // Check what kind of message we received
            if (message instanceof Integer) {
                // Accept Integer n
                Integer n = (Integer) message;
                Integer result;
                
                if (n < 0) {
                    // Invalid case
                    throw new RuntimeException("Value " + n + " was invalid!");
                } else if (n <= 1) {
                    // Base case, return n
                    result = new Integer(n);
                } else {
                    Integer result1 = 0, result2 = 0;
                    
                    // Create two child actors
                    ActorRef child1 = getContext().actorOf(Props.create(Worker.class), "child1");
                    ActorRef child2 = getContext().actorOf(Props.create(Worker.class), "child2");
                    
                    // Ask the child actors for fib(n-1) and fib(n-2)
                    // respectively
                    Timeout timeout = new Timeout(Duration.create(TIMEOUT, TimeUnit.SECONDS));
                    Future<Object> future1 = Patterns.ask(child1, new Integer(n - 1), timeout);
                    Future<Object> future2 = Patterns.ask(child2, new Integer(n - 2), timeout);
                    
                    // Wait for a result on both result1 and result2
                    try {
                        result1 = (Integer) Await.result(future1, timeout.duration());
                        result2 = (Integer) Await.result(future2, timeout.duration());
                    } catch (Exception e) {
                        System.out.println("Timeout of " + TIMEOUT + " seconds exceeded for provided value of " + n
                                           + " or value was invalid");
                        System.exit(1);
                    }
                    
                    // Prepare the result
                    result = new Integer(result1 + result2);
                    
                }
                // Tell the sender our result
                getSender().tell(result, getSelf());
            }
        }
    }
    
    public static void main(String[] args) {
        
        // Prompt the user for an Integer
        System.out.print("Please enter a value of n from which to calculate the nth Fibonacci number: ");
        Scanner in = new Scanner(System.in);
        Integer num = new Integer(in.nextInt());
        in.close();
        
        // Create an actor system
        final ActorSystem actorSystem = ActorSystem.create("actor-system");
        
        // Create a worker actor
        final ActorRef worker = actorSystem.actorOf(Props.create(Worker.class), "worker");
        
        // Create an inbox
        final Inbox inbox = Inbox.create(actorSystem);
        
        // Tell the worker to calculate the num-th Fibonacci number
        inbox.send(worker, num);
        
        // Wait up to TIMEOUT seconds for a reply from the worker
        Integer reply = null;
        try {
            reply = (Integer) inbox.receive(Duration.create(TIMEOUT, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
            System.out.println(
                               "Got a timeout after " + TIMEOUT + " seconds waiting for response from top-level worker actor");
            System.exit(1);
        }
        
        // Print the reply received
        System.out.println("The " + num + "-th Fibonacci number is " + (Integer) reply);
        
        // Shut down the system
        actorSystem.terminate();
        
    }
    
}
