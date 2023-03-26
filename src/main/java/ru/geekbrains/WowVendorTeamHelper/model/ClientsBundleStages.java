package ru.geekbrains.WowVendorTeamHelper.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients_bundle_stages")
public class ClientsBundleStages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private WowClient client;

    @ManyToOne
    @JoinColumn(name = "bundle_stage_id")
    private BundleStage bundleStage;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private OrderStatus orderStatus;
}
