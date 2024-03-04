package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreatedEvent;
import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderDAO;
import lombok.RequiredArgsConstructor;
import org.example.rest.CarDTO;
import org.example.rest.CarsApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDAO orderDAO;
    private final CarsApi carsApi;
    private final OrderCreatedEventProducer orderCreatedEventProducer;

    public OrderDTO create(OrderDTO order){
      Order orderToSave = new Order();
      orderToSave.setTimestamp(System.currentTimeMillis());

        List<CarDTO> cars = carsApi.getCars();

        Optional<CarDTO> desiredCar = cars.stream().filter(car -> Objects.equals(car.getBrand(), order.getBrand())).findFirst();

        desiredCar.ifPresent(carDTO -> orderToSave.setCarId(carDTO.getId()));

        Order savedOrder = orderDAO.save(orderToSave);

        orderCreatedEventProducer.produceOrderCreatedEvent(
                OrderCreatedEvent.builder()
                        .orderId(savedOrder.getId())
                        .carId(savedOrder.getCarId())
                        .build()
        );
        return OrderDTO
                .builder()
                .brand(order.getBrand())
                .timestamp(savedOrder.getTimestamp())
                .id(savedOrder.getId())
                .build();
    }

    public OrderDTO getOrderDetails(Integer orderId){
            Order order = orderDAO.findById(orderId).orElseThrow();

            CarDTO car = carsApi.getCar(order.getCarId());


        return OrderDTO.builder()
                .id(order.getId())
                .timestamp(order.getTimestamp())
                .brand(car.getBrand())
                .price(car.getPrice())
                .year(car.getYear())
                .build();
    }
}
