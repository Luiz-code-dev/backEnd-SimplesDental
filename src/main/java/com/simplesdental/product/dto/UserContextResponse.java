package com.simplesdental.product.dto;

import com.simplesdental.product.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContextResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.Role role;
}
