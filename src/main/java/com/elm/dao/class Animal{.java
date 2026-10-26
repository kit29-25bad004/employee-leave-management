class Animal{
    public void sound(){
      System.out.println("Sound of the animal");
    }
  }
  class Dog extends Animal{
    public void sound(){
      System.out.println("Dog barks");
    }
  }
  public class Main{
    public static void main(String[] args){
      Dog d = new Dog();
      d.sound();
    }
  }
    