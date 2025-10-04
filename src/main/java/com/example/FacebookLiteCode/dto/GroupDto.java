package com.example.FacebookLiteCode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private Long groupId;
    private String groupName;
    private String privacy;
    private int memberCount;
    private List<UserDto> members;
    private List<UserDto> admins;
}
