package org.example;

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(Clothing clothing);
}

class ObserverManager {
    private List<Observer> observers = new ArrayList<>();

    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Clothing clothing) {
        for (Observer observer : observers) {
            observer.update(clothing);
        }
    }
}

abstract class Clothing {
    public String name;
    public double price;
    public int supply;
    public ObserverManager observerManager;

    boolean discount;

    public Clothing(String name, double price, int supply) {
        this.name = name;
        this.price = price;
        this.supply = supply;
        this.observerManager = new ObserverManager();
    }

    public void updateSupply(int newSupply) {
        this.supply = newSupply;
        System.out.println("Supply of " + getName() + " updated: " + newSupply);
        observerManager.notifyObservers(this);

    }

    public void updatePrice(double newPrice) {
        double oldPrice = price;
        this.price = newPrice;
        if(newPrice < oldPrice) {
            System.out.println("Price dropped by: " + (oldPrice - newPrice));
        }else if (newPrice > oldPrice){
            System.out.println("Price went up by: " + (newPrice - oldPrice));
        }
        double priceDropPercentage = (1 - newPrice / oldPrice) * 100;
        if (priceDropPercentage >= 10) {
            discount = true;
            observerManager.notifyObservers(this);
        }

    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getSupply() {
        return supply;
    }

    public void attachObserver(Observer observer) {
        observerManager.attachObserver(observer);
    }

    public void detachObserver(Observer observer) {
        observerManager.detachObserver(observer);
    }

    private void printHeader(){
        System.out.println("=== Clothing Details ===");
    }
    private void printCommonDetails(){
        System.out.println("Name: " + getName());
        System.out.println("Price: " + getPrice());
        System.out.println("Supply: " + getSupply());
    }

    protected abstract void printSpecificDetails();

    private void printFooter(){
        System.out.println("=========================");
    }

    public final void displayDetails() {
        printHeader();
        printCommonDetails();
        printSpecificDetails();
        printFooter();
    }
}

class Shirt extends Clothing {
    private String size;

    public Shirt(String name, double price, int supply, String size) {
        super(name, price, supply);
        this.size = size;
    }

    @Override
    protected void printSpecificDetails() {
        System.out.println("Size: " + size);
    }


}

class Pants extends Clothing {
    private String material;

    public Pants(String name, double price, int supply, String material) {
        super(name, price, supply);
        this.material = material;
    }

    @Override
    protected void printSpecificDetails() {
        System.out.println("Material: " + material);
    }


}

class StoreManager implements Observer {
    @Override
    public void update(Clothing clothing) {
        if (clothing.getSupply() < 10) {
            System.out.println("Notification for Store Manager: " + clothing.getName() + " supply is low. Order new supply!");
            clothing.updateSupply(15);
        }
    }
}

class Customer implements Observer {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    public void buy(Clothing clothing) {
        int supply = clothing.getSupply();
        System.out.println(getName() + " bought: " + clothing.getName());
        clothing.updateSupply(supply - 1);
    }

    @Override
    public void update(Clothing clothing) {
        if (clothing.discount) {
            System.out.println("Notification for Customer: " + clothing.getName() + " price dropped. Buy now! (New Price: " + clothing.getPrice() + ")");
            clothing.discount = false;
        }
    }

    public String getName() {
        return name;
    }
}

class ClothingStore {

    private List<Clothing> inventory;

    public ClothingStore() {
        inventory = new ArrayList<>();
    }

    public void addClothing(Clothing clothing) {
        inventory.add(clothing);
    }

    public void removeClothing(Clothing clothing) {
        inventory.remove(clothing);
    }

    public void displayInventory() {
        for (Clothing clothing : inventory) {
            clothing.displayDetails();
            System.out.println();
        }
    }
}


public class Main {
    public static void main(String[] args) {

        Clothing shirt = new Shirt("T-Shirt", 25.0, 15, "M");
        Clothing pants = new Pants("Jeans", 50.0, 8, "Denim");


        Observer storeManager = new StoreManager();
        Customer customer = new Customer("Nick");


        shirt.attachObserver(storeManager);
        pants.attachObserver(storeManager);
        shirt.attachObserver(customer);
        pants.attachObserver(customer);


        ClothingStore clothingStore = new ClothingStore();
        clothingStore.addClothing(shirt);
        clothingStore.addClothing(pants);


        shirt.updateSupply(5);

        shirt.updatePrice(25.0);

        customer.buy(shirt);
        customer.buy(pants);



        clothingStore.displayInventory();
    }
}