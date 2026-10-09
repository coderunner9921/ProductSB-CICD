package com.example.ProductFin.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prod_id")
	private Integer prod_id;
	
	@NotBlank(message = "Product name is required")
	@Size(max = 100, message = "Product name cannot exceed 100 characters")
	@Column(name = "prod_name")
	private String prod_name;

	@Positive(message = "Price must be greater than zero")
	@Column(name = "prod_price")
	private double price;
    
    public Product(Integer prod_id, String prod_name, double price) {
		super();
		this.prod_id = prod_id;
		this.prod_name = prod_name;
		this.price = price;
	}
    
    public Product () {}
    
	@Override
	public String toString() {
		return "Product [prod_id=" + prod_id + ", prod_name=" + prod_name + ", price=" + price + "]";
	}

	public int getProd_id() {
		return prod_id;
	}

	public void setProd_id(Integer prod_id) {
		this.prod_id = prod_id;
	}

	public String getProd_name() {
		return prod_name;
	}

	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
