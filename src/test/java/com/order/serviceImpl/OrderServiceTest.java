package com.order.serviceImpl;

import com.order.dto.OrderDto;
import com.order.entity.OrderEntity;
import com.order.exception.OrderNotFoundException;
import com.order.repository.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    private OrderService orderService;
    private OrderDto orderDto;
    private OrderEntity orderEntity;

    @BeforeEach
    public void setUp() {
        // Prepare test data
        orderDto = new OrderDto();
        orderDto.setOrderId("1");
        orderDto.setPrice(500);
        orderDto.setQuantity(2);
        orderDto.setStatus("pending");
        orderDto.setProductId("123");

        orderEntity = new OrderEntity();
        orderEntity.setOrderId("1");
        orderEntity.setPrice(500);
        orderEntity.setQuantity(2);
        orderEntity.setStatus("pending");
        orderEntity.setProductId("123");
    }

    @Test
    public void testCreateOrder_Success() {
        when(modelMapper.map(orderDto, OrderEntity.class)).thenReturn(orderEntity);
        when(orderRepo.save(orderEntity)).thenReturn(Mono.just(orderEntity));

        Mono<OrderEntity> result = orderService.createOrder(orderDto);

        StepVerifier.create(result)
                .expectNext(orderEntity)
                .verifyComplete();

        verify(modelMapper, times(1)).map(orderDto, OrderEntity.class);
        verify(orderRepo, times(1)).save(orderEntity);
        verify(kafkaTemplate, times(1)).send(eq("order-topic"), anyString());
    }

    @Test
    public void testGetOrderById_Success() {
        when(orderRepo.findById("1")).thenReturn(Mono.just(orderEntity));

        Mono<OrderEntity> result = orderService.getOrderById("1");

        StepVerifier.create(result)
                .expectNext(orderEntity)
                .verifyComplete();

        verify(orderRepo, times(1)).findById("1");
    }

    @Test
    public void testGetOrderById_NotFound() {
        when(orderRepo.findById("2")).thenReturn(Mono.empty());

        Mono<OrderEntity> result = orderService.getOrderById("2");

        StepVerifier.create(result)
                .expectError(OrderNotFoundException.class)
                .verify();

        verify(orderRepo, times(1)).findById("2");
    }
}
