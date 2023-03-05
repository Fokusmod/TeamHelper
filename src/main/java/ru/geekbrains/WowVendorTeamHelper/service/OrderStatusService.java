package ru.geekbrains.WowVendorTeamHelper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.geekbrains.WowVendorTeamHelper.exeptions.WWTHResourceNotFoundException;
import ru.geekbrains.WowVendorTeamHelper.model.OrderStatus;
import ru.geekbrains.WowVendorTeamHelper.repository.OrderStatusRepository;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;


    public OrderStatus getOrderStatusByTitle(String title) {
        return orderStatusRepository.findByTitle(title)
                .orElseThrow(() -> new WWTHResourceNotFoundException("Статус заказа с указанным названием '" + title + "' не найден."));
    }

}
