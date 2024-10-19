package com.example.petstore.api.models;

public class Order {
    private long id;
    private int petId; // Изменяем на int
    private int quantity;
    private String shipDate; // Храним дату как строку
    private String status; // Статус заказа
    private boolean complete; // Поле, указывающее завершен ли заказ

    public Order(long id, int petId, int quantity, String shipDate, String status, boolean complete) {
        this.id = id;
        this.petId = petId; // Теперь хранится как int
        this.quantity = quantity;
        this.shipDate = shipDate; // Сохраняем как строку
        this.status = status;
        this.complete = complete;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPetId() { // Изменяем на int
        return petId;
    }

    public void setPetId(int petId) { // Изменяем на int
        this.petId = petId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShipDate() {
        return shipDate; // Возвращаем как строку
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate; // Устанавливаем как строку
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", petId=" + petId +
                ", quantity=" + quantity +
                ", shipDate='" + shipDate + '\'' +
                ", status='" + status + '\'' +
                ", complete=" + complete +
                '}';
    }
}
