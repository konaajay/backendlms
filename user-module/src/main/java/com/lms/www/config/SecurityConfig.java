package com.lms.www.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

            	    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            	    
            	    // 2️⃣ PLATFORM IMPORT (EXPLICIT METHOD FIRST)
                    .requestMatchers(
                            org.springframework.http.HttpMethod.POST,
                            "/platform/themes/import"
                    ).permitAll()
            	    
            	    // 3️⃣ ALL PLATFORM APIs
                    .requestMatchers("/platform/**").permitAll()
                    
                     // 4️⃣ PUBLIC AUTH APIs
                    .requestMatchers("/auth/**").permitAll()
            	    // 🔓 PUBLIC
            	    .requestMatchers("/auth/login").permitAll()
            	    .requestMatchers("/auth/logout").permitAll()
            	    .requestMatchers("/auth/password-reset/**").permitAll()
            	    .requestMatchers("/auth/account-unlock/**").permitAll()
            	    .requestMatchers("/platform/**").permitAll()
            	    .requestMatchers("/s/pages/**").permitAll()
                    .requestMatchers("/uploads/**").permitAll()

            	    // 🔓 SUPER ADMIN SIGNUP (ONLY OTP FLOW)
            	    .requestMatchers("/super-admin/signup/**").permitAll()
            	    .requestMatchers("/super-admin/request-disable").permitAll()

            	    // 🔐 SUPER ADMIN (ALL OTHER SUPER ADMIN APIs)
            	    .requestMatchers("/super-admin/**").hasAuthority("ROLE_SUPER_ADMIN")
            	    
            	    .requestMatchers(HttpMethod.GET, "/website/marketing-community").permitAll()
            	    .requestMatchers("/website/marketing-community").permitAll()
            	    .requestMatchers("/website/**")
            	    .authenticated()

            	    // 🔐 ADMIN
            	    .requestMatchers("/admin/**")
            	    .hasAnyAuthority("ROLE_ADMIN","ROLE_SUPER_ADMIN")

            	    // 🔐 STUDENT
            	    .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT")

            	    // 🔐 INSTRUCTOR
            	    .requestMatchers("/instructor/**").hasAuthority("ROLE_INSTRUCTOR")

            	    // 🔐 PARENT
            	    .requestMatchers("/parent/**").hasAuthority("ROLE_PARENT")
            	    
            	     // 🔐 PARENT
            	    .requestMatchers("/driver/**").hasAuthority("ROLE_DRIVER")
            	    
            	     // 🔐 PARENT
            	    .requestMatchers("/conductor/**").hasAuthority("ROLE_CONDUCTOR")
            	    
            	    // 🔐 ACCOUNTANT
            	    .requestMatchers("/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
            	    
            	    // 🔐 AFFILIATE
            	    .requestMatchers("/affiliate/**").hasAuthority("ROLE_AFFILIATE")
            	    
            	    // 🔐 COMMUNITY MANAGER
            	    .requestMatchers("/community-manager/**").hasAuthority("ROLE_COMMUNITY_MANAGER")
            	    
            	    // 🔐 DEPARTMENT HEAD
            	    .requestMatchers("/department-head/**").hasAuthority("ROLE_DEPARTMENT_HEAD")
            	    
            	    // 🔐 EVALUATOR
            	    .requestMatchers("/evaluator/**").hasAuthority("ROLE_EVALUATOR")
            	    
            	    // 🔐 INVENTORY MANAGER
            	    .requestMatchers("/inventory-manager/**").hasAuthority("ROLE_INVENTORY_MANAGER")
            	    
            	    // 🔐 LIBRARIAN
            	    .requestMatchers("/librarian/**").hasAuthority("ROLE_LIBRARIAN")
            	    
            	    // 🔐 MARKETING MANAGER
            	    .requestMatchers("/marketing-manager/**").hasAuthority("ROLE_MARKETING_MANAGER")
            	    
            	    // 🔐 MENTOR
            	    .requestMatchers("/mentor/**").hasAuthority("ROLE_MENTOR")
            	    
            	    // 🔐 TRANSPORT MANAGER
            	    .requestMatchers("/transport-manager/**").hasAuthority("ROLE_TRANSPORT_MANAGER")
            	    
            	    // 🔐 WARDEN
            	    .requestMatchers("/warden/**").hasAuthority("ROLE_WARDEN")
            	    
            	                 	    .requestMatchers(HttpMethod.POST, "/api/leads").permitAll()
             	    .requestMatchers("/api/v1/public/affiliates/**").permitAll()


            	    // 🔐 SELF
            	    .requestMatchers("/me/**").authenticated()
            	    
             	    // 🔐 AFFILIATE & MARKETING ADMIN
             	    .requestMatchers("/api/v1/marketing/admin/**", "/api/v1/admin/affiliates/**")
             	    .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ALL_PERMISSIONS")

             	    // 🔐 PUBLIC ACCESSIBLE
             	    .requestMatchers(HttpMethod.GET, "/api/v1/settings/**", "/api/courses", "/api/v1/courses/**", "/api/v1/fee-management/batches/course/**").permitAll()
             	    .requestMatchers("/api/v1/settings/**")
             	    .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")

             	    // 🔐 CAMPUS & HOSTEL
             	    .requestMatchers("/campus/**")
             	    .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_WARDEN")

                    // 🔐 INVENTORY MODULE
                    .requestMatchers(
                        "/api/items/**", 
                        "/api/vendors/**", 
                        "/api/inventory/**", 
                        "/api/inventory-transactions/**",
                        "/api/digital-assets/**",
                        "/api/returns/**",
                        "/api/course-inventory/**",
                        "/api/stock-inward/**",
                        "/api/stock-outward/**"
                    ).hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_INVENTORY_MANAGER")

                    // 🔐 MANAGEMENT MODULE (Inventory, Attendance, Certificates, Sessions, Exams)
                    .requestMatchers("/api/management/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_INVENTORY_MANAGER", "ROLE_INSTRUCTOR")

                    .requestMatchers("/api/v1/fee-management/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_ACCOUNTANT", "ROLE_STUDENT", "ROLE_PARENT", "ALL_PERMISSIONS")

                    .anyRequest().authenticated()
            	)

            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration =
                new org.springframework.web.cors.CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
