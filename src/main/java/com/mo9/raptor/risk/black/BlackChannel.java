package com.mo9.raptor.risk.black;

import com.mo9.raptor.engine.state.event.impl.AuditResponseEvent;
import com.mo9.raptor.entity.UserEntity;

/**
 * Created by jyou on 2018/10/25.
 *
 * @author jyou
 */
public interface BlackChannel {

    boolean isOpen = true;

    int HTTP_OK = 200;

    AuditResponseEvent doBlackCheck(UserEntity userEntity);

    boolean channelIsOpen();
}
