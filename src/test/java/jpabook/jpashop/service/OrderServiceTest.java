package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {
        Member member = createMember();

        Book book = createBook("jpa book", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        System.out.println("getOrder.getOrderItems() = " + getOrder.getOrderItems());

        for (OrderItem orderItem : getOrder.getOrderItems()) {
            System.out.println("orderItem = " + orderItem);
        }


        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER여야 한다.");
        assertEquals(1, getOrder.getOrderItems().size(), "주문 상품의 종류 수가 일치하는지");
        assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "가격이 일치하는지");
        assertEquals(8, book.getStockQuantity(), "주문이 된 수량만큼 재고에서 수량이 줄어야 한다.");
    }


    @Test
    void 상품주문_재고수량초과() {
        Member member = createMember();
        Book book = createBook("k8s book", 25000, 5);

        assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), book.getId(), 6);
        });
    }


    @Test
    void 상품취소() {
        //given
        Member member = createMember();
        Book item = createBook("Operating System", 22000, 10);

        Long orderId = orderService.order(member.getId(), item.getId(), 3);

        //when
        orderService.cancel(orderId);

        //then  주문을 함과 동시에 빠져나갔던 재고 수량이 주문을 다시 취소 함으로서 재고 수량이 원상복귀 되었는지 검증
        Order order = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, order.getStatus(), "주문 상태가 CANCEL 상태로 변하였는지");
        assertEquals(10, item.getStockQuantity(), "재고 수량이 원상복귀 되었는지");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("cole");
        member.setAddress(new Address("서울", "올림픽대로", "12345"));
        em.persist(member);
        return member;
    }
}