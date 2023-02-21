package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OrderStatus)) {
            return false;
        }

        OrderStatus other = (OrderStatus) obj;
        if (isProxy(this) || isProxy(other)) {
            return Objects.equals(id, other.id);
        }

        return Objects.equals(id, other.id) && Objects.equals(title, other.title);
    }

    @Override
    public int hashCode() {
        if (isProxy(this)) {
            return Objects.hashCode(id);
        }

        return Objects.hash(id, title);
    }

    private boolean isProxy(Object obj) {
        return obj != null && obj.getClass().getName().contains("$$");
    }
}
