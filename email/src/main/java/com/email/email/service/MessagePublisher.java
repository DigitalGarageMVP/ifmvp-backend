package com.email.email.service;

import com.email.email.domain.EmailEvent;

/**
* 메시지 발행 인터페이스입니다.
*/
public interface MessagePublisher {
   
   /**
    * 이메일 이벤트를 발행합니다.
    *
    * @param event 이메일 이벤트
    */
   void publishEmailEvent(EmailEvent event);
}
