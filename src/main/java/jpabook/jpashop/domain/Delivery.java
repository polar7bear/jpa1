package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    //enum타입에서 Ordinal이 default값인데 Ordinal은 타입들을 숫자로 순번대로 저장하기 때문에
    //다른 타입이 중간에 추가 될 경우 숫자가 1씩 뒤로 밀려나면서 절대 사용X. 꼭 STRING으로 설정해야한다
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //READY, COMP
}
