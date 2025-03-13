package com.email.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
* 보안 관련 유틸리티 클래스입니다.
*/
public class SecurityUtils {
   
   private SecurityUtils() {
       throw new IllegalStateException("Utility class");
   }
   
   /**
    * 현재 인증된 사용자의 ID를 반환합니다.
    *
    * @return 사용자 ID
    */
   public static String getCurrentUsername() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
       if (authentication == null || !authentication.isAuthenticated()) {
           return null;
       }
       
       return authentication.getName();
   }
   
   /**
    * 현재 사용자가 인증되었는지 확인합니다.
    *
    * @return 인증 여부
    */
   public static boolean isAuthenticated() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       return authentication != null && authentication.isAuthenticated();
   }
   
   /**
    * 현재 사용자가 특정 역할을 가지고 있는지 확인합니다.
    *
    * @param role 역할
    * @return 역할 보유 여부
    */
   public static boolean hasRole(String role) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
       if (authentication == null || !authentication.isAuthenticated()) {
           return false;
       }
       
       Set<String> roles = authentication.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .collect(Collectors.toSet());
       
       return roles.contains("ROLE_" + role) || roles.contains(role);
   }
   
   /**
    * 현재 사용자의 역할 목록을 반환합니다.
    *
    * @return 역할 목록
    */
   public static Set<String> getCurrentUserRoles() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
       if (authentication == null || !authentication.isAuthenticated()) {
           return Collections.emptySet();
       }
       
       return authentication.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .collect(Collectors.toSet());
   }
}