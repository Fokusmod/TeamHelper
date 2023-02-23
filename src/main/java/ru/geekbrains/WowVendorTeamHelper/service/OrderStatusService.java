package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.ResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.repository.OrderStatusRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;


    public OrderStatus getOrderStatusByTitle(String title) {
        return orderStatusRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Статус заказа с указанным названием '" + title + "' не найден."));
    }

}
