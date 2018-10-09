package com.mo9.raptor.riskdb.entity;

import javax.persistence.*;

@Entity
@Table(name = "t_risk_third_black_list")
public class TRiskThirdBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BLACK_VALUE")
    private String blackValue;

    @Column(name = "VALUE_TYPE")
    private String valueType;

}
