package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "order_statuses")
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() && !getClass().isAssignableFrom(o.getClass())) return false;
        OrderStatus orderStatus = (OrderStatus) (o instanceof OrderStatus ? o : ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation());
        return Objects.equals(id, orderStatus.id) &&
                Objects.equals(title, orderStatus.title);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }


}
